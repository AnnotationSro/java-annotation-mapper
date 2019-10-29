package sk.annotation.library.jam.processor.data;

import com.sun.tools.javac.code.Type;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import sk.annotation.library.jam.annotations.Mapper;
import sk.annotation.library.jam.processor.Constants;
import sk.annotation.library.jam.processor.data.generator.method.AbstractMethodSourceInfo;
import sk.annotation.library.jam.processor.data.generator.method.DeclaredMethodSourceInfo;
import sk.annotation.library.jam.processor.data.keys.MethodConfigKey;
import sk.annotation.library.jam.processor.data.mapi.MethodApiFullSyntax;
import sk.annotation.library.jam.processor.data.mapi.MethodApiKey;
import sk.annotation.library.jam.processor.sourcewriter.ImportsTypeDefinitions;
import sk.annotation.library.jam.processor.utils.*;
import sk.annotation.library.jam.processor.utils.annotations.AnnotationValueUtils;
import sk.annotation.library.jam.processor.utils.annotations.data.AnnotationMapperConfig;
import sk.annotation.library.jam.utils.MapperUtil;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;
import java.util.*;

@Getter
public class MapperClassInfo {

    @Getter
    final protected Set<String> usedNames = new HashSet<>(); // in method context !!!

    static public Map<String, Optional<MapperClassInfo>> cache = new HashMap<>();


    private String fullClassName;
    private String simpleClassName;
    final List<AnnotationMapperConfig> classAndPackageConfigurations;

    static public MapperClassInfo getOrCreate(ProcessingEnvironment processingEnv, TypeElement element) {
        String fullNamePath = ElementUtils.getQualifiedName(element);
        Optional<MapperClassInfo> ofRet = cache.get(fullNamePath);
        if (ofRet == null) {
            try {
                ofRet = Optional.of(new MapperClassInfo(processingEnv, element));
            } catch (Exception e) {
                ofRet = Optional.empty();
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, ExceptionUtils.getFullStackTrace(e));
            }
        }

        return ofRet.orElse(null);
    }


    final public TypeElement parentElement;
    final public Mapper jamMapperConfig;
    final public boolean parentTypeAsAbstractClass;

    final public List<Element> canUseInCustomer = new LinkedList<>();

    final public ImportsTypeDefinitions imports;
    final private Set<Modifier> mapperModifiers = new LinkedHashSet<>();
    final public List<FieldInfo> fieldsToImplement = new LinkedList<>();
    public ConstantsMethodGeneratorInfo topMethodsRegistrator = new ConstantsMethodGeneratorInfo();


    final public AnnotationsInfo generateAnnotations;

    @Getter
    final protected FeatureSourceUtils features;


    private MapperClassInfo(ProcessingEnvironment processingEnv, TypeElement element) {
        this.parentElement = element;
        this.jamMapperConfig = element.getAnnotation(Mapper.class);
        this.fullClassName = ElementUtils.getQualifiedName(this.parentElement) + MapperUtil.constPostFixClassName;
        this.simpleClassName = NameUtils.getClassSimpleName(this.fullClassName);

        if (!ApiUtil.canImplementMapper(element)) {
            throw new IllegalStateException("Mapper " + ElementUtils.getQualifiedName(element) + " cannot implement!");
        }
        this.parentTypeAsAbstractClass = parentElement.getKind() == ElementKind.CLASS;

        this.features = new FeatureSourceUtils(this);

        // Prepare IMPORT SCOPE !!!
        imports = new ImportsTypeDefinitions(element);

        if (parentElement.getModifiers().contains(Modifier.PUBLIC))
            mapperModifiers.add(Modifier.PUBLIC);
        else if (parentElement.getModifiers().contains(Modifier.PROTECTED))
            mapperModifiers.add(Modifier.PROTECTED);
        else if (parentElement.getModifiers().contains(Modifier.DEFAULT))
            mapperModifiers.add(Modifier.DEFAULT);

        // Configuration ...
        classAndPackageConfigurations = AnnotationValueUtils.resolveMapperConfigData(processingEnv, element);

        // Annotations ...
        generateAnnotations = new AnnotationsInfo()
                //.withAnnotation(Constants.annotationJamMapperGenerated)
                .mergeValues(IoCUtils.resolveMapperAnnotation(processingEnv, parentElement))
                .mergeValues(Constants.createAnnotationGenerated());


        List<? extends Element> allMembers = ElementUtils.findAllAcceptedMember(processingEnv, element);
        List<VariableElement> allFields = ElementFilter.fieldsIn(allMembers);
        List<ExecutableElement> allMethods = ElementFilter.methodsIn(allMembers);


        // Remember UsedNames ...
        for (VariableElement field : allFields) {
            usedNames.add(field.getSimpleName().toString());
        }
        for (ExecutableElement method : allMethods) {
            usedNames.add(method.getSimpleName().toString());
        }
        // Custom fields from API
        if (classAndPackageConfigurations != null && !classAndPackageConfigurations.isEmpty()) {
            for (AnnotationMapperConfig classAndPackageConfiguration : classAndPackageConfigurations) {
                registerCustomFields(processingEnv, classAndPackageConfiguration.getWithCustom());
            }
        }

        // Analyze customClass and fields
        for (VariableElement field : allFields) {
            registerField(processingEnv, field);
        }

        // Analyze methods ...
        for (ExecutableElement method : allMethods) {
            registerTopMethod(processingEnv, method);
        }

        // analyze classes to uses
        for (DeclaredMethodSourceInfo analyzedMethodSourceInfo : topMethods) {
            analyzedMethodSourceInfo.analyzeAndGenerateDependMethods(processingEnv);
        }


        ///////////////////////////////////////
        // clearing unnecessary params
        // if it is possible, delete context params from generated methods
        if (!getFeatures().isRequiredInputWithMethodId()) {
            for (AbstractMethodSourceInfo value : this.methodsToImplement) {
                value.getMethodApiFullSyntax().getParams().remove(Constants.methodParamInfo_ctxForMethodId);
            }
            topMethodsRegistrator.constanctsForTopMethods.clear();
        }

        if (!getFeatures().isRequiredInputWithContextData()) {
            for (AbstractMethodSourceInfo value : this.methodsToImplement) {
                value.getMethodApiFullSyntax().getParams().remove(Constants.methodParamInfo_ctxForRunData);
            }
        }

        // analyze 2 - check delegatedTopMethods
        for (DeclaredMethodSourceInfo analyzedMethodSourceInfo : topMethods) {
            analyzedMethodSourceInfo.tryUnwrapMethods(processingEnv);
        }

        // clear unused custom fields
        List<FieldInfo> notUsedFields = new LinkedList<>();
        for (FieldInfo fieldInfo : this.fieldsToImplement) {
            if (this.usedField.contains(fieldInfo.getName())) continue;
            notUsedFields.add(fieldInfo);
        }
        fieldsToImplement.removeAll(notUsedFields);


//		//////////////////////////////
//		// START: testing data
//
//		mapperModifiers.remove(Modifier.ABSTRACT);
//
//		// END: testing data
//		//////////////////////////////

    }


    private void registerCustomFields(ProcessingEnvironment processingEnv, List<Type> values) {
        registerCustomFields(processingEnv, values, null);
    }

    private void registerCustomFields(ProcessingEnvironment processingEnv, List<Type> values, MethodConfigKey topMmethodConfigKey) {
        if (values == null || values.isEmpty()) return;

        AnnotationsInfo fieldInjectionAnnotations = IoCUtils.getFieldAnnotationType(processingEnv, parentElement, null);
        for (Type fieldType : values) {
            for (Type existFieldValue : allFieldsTypes.values()) {
                if (processingEnv.getTypeUtils().isSameType(existFieldValue, fieldType)) continue; // already exists
                if (processingEnv.getTypeUtils().isSameType(existFieldValue, parentElement.asType())) continue; // already exists
            }

            // Try name
            String fieldName = NameUtils.findBestName(usedNames, StringUtils.uncapitalize(NameUtils.getClassSimpleName(fieldType.toString())));
            usedNames.add(fieldName);
            allFieldsTypes.put(fieldName, fieldType);

            fieldsToImplement.add(new FieldInfo(fieldName, new TypeInfo(fieldType)).withInjections(fieldInjectionAnnotations));
            List<ExecutableElement> methods = ApiUtil.readElementApi(processingEnv, fieldType);
            if (methods == null || methods.isEmpty()) return;
            registerApiForPath(processingEnv, fieldName, fieldType, methods, topMmethodConfigKey);
        }
    }

    Map<String, Type> allFieldsTypes = new LinkedHashMap<>();

    private void registerField(ProcessingEnvironment processingEnv, VariableElement field) {
        if (ApiUtil.ignoreUsing(true, field)) return;
        Type type = TypeUtils.findType(processingEnv, (Type) parentElement.asType(), field);
        // Authomatically ignored same mapper
        if (TypeUtils.isSame(processingEnv, type, parentElement.asType())) return;

        allFieldsTypes.put(field.getSimpleName().toString(), type);
        List<ExecutableElement> methods = ApiUtil.readElementApi(processingEnv, type);
        if (methods == null || methods.isEmpty()) return;
        registerApiForPath(processingEnv, field.getSimpleName().toString(), type, methods, null);
    }

    private void registerApiForPath(ProcessingEnvironment processingEnv, String pathApi, Type fieldType, List<ExecutableElement> executableElements, MethodConfigKey topMmethodConfigKey) {
        // cannot register own paths
        if (TypeUtils.isSame(processingEnv, fieldType, parentElement.asType())) return;

        // we need to find out, what can be given field used for
        for (ExecutableElement method : executableElements) {
            if (ApiUtil.ignoreUsing(false, method)) continue;

            MethodApiFullSyntax methodSyntax = MethodApiFullSyntax.analyze(processingEnv, fieldType, method);
            if (methodSyntax == null || !methodSyntax.getErrorsMapping().isEmpty()) {
                // Ignore bad API
                processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, methodSyntax.getErrorsMapping().toString(), method);
                continue;
            }

            resolveExtUsableMethods(topMmethodConfigKey)
                    .computeIfAbsent(pathApi, a -> new LinkedList<>()).add(methodSyntax);
        }
    }


    private Map<String, List<MethodApiFullSyntax>> _myUsableMethods = new LinkedHashMap<>();

    public List<MethodApiFullSyntax> resolveMyUsableMethods(MethodConfigKey topMmethodConfigKey) {
        return _myUsableMethods.
                computeIfAbsent(topMmethodConfigKey == null ? "*" : topMmethodConfigKey.getForTopMethod(), a -> new LinkedList<>());
    }

    public List<MethodCallApi> getAllInterceptors(ProcessingEnvironment processingEnv, TypeMirror srcType, TypeMirror dstType) {
        List<MethodCallApi> interceptors = new LinkedList<>();
        for (MethodApiFullSyntax methodApiFullSyntax : resolveMyUsableMethods(null)) {
            MethodApiKey methodApiKey = methodApiFullSyntax.getApiKey();
            if (methodApiKey.isApiWithReturnType()) continue;

            ExecutableType testMethodType = methodApiKey.createMethodExecutableType(processingEnv, this.parentElement);
            if (TypeMethodUtils.isMethodCallableForInterceptor(processingEnv, srcType, dstType, testMethodType)) {
                // Function is OK, thay can be call
                interceptors.add(MethodCallApi.createFrom("", methodApiFullSyntax, null));
                continue;
            }
        }
        Map<String, List<MethodApiFullSyntax>> extUsableMethods = resolveExtUsableMethods(null);
        for (Map.Entry<String, List<MethodApiFullSyntax>> e : extUsableMethods.entrySet()) {
            for (MethodApiFullSyntax methodApiFullSyntax : e.getValue()) {
                MethodApiKey methodApiKey = methodApiFullSyntax.getApiKey();
                if (methodApiKey.isApiWithReturnType()) continue;

                ExecutableType testMethodType = methodApiKey.createMethodExecutableType(processingEnv, this.parentElement);
                if (TypeMethodUtils.isMethodCallableForInterceptor(processingEnv, srcType, dstType, testMethodType)) {
                    // Function is OK, thay can be call
                    interceptors.add(MethodCallApi.createFrom(e.getKey()+".", methodApiFullSyntax, null));
                    usedField.add(e.getKey()); // mark field as used
                    continue;
                }
            }
        }

        return interceptors;
    }

    private Map<String, Map<String, List<MethodApiFullSyntax>>> _extUsableMethods = new LinkedHashMap<>();

    private Map<String, List<MethodApiFullSyntax>> resolveExtUsableMethods(MethodConfigKey topMmethodConfigKey) {
        return _extUsableMethods
                .computeIfAbsent(topMmethodConfigKey == null ? "*" : topMmethodConfigKey.getForTopMethod(), a -> new HashMap<>());
    }

    @Getter
    private List<AbstractMethodSourceInfo> methodsToImplement = new LinkedList<>();
    private List<DeclaredMethodSourceInfo> topMethods = new LinkedList<>();

    public String findBestNewMethodName_transformFromTo(ProcessingEnvironment processingEnv, MethodApiKey transformApiKey) {
        StringBuilder sb = new StringBuilder();
        sb.append("transf");

        TypeMirror visibleType = transformApiKey.getVisibleTypes()[0];
        if (visibleType != null) {
            String strType = TypeUtils.getClassSimpleName(visibleType);
            if (StringUtils.isNotEmpty(strType)) {
                sb.append("_to").append(strType);
            }


            // parameters
            if (visibleType instanceof Type.ClassType) {
                Type.ClassType tp = (Type.ClassType) visibleType;
                if (tp.allparams() != null && !tp.allparams().isEmpty()) {
                    for (Type allparam : tp.allparams()) {
                        String miniNameType = TypeUtils.getClassSimpleName(allparam);
                        if (StringUtils.isNotEmpty(miniNameType)) {
                            sb.append("_with").append(miniNameType);
                        }
                    }
                }
            }
        }
        return NameUtils.findBestName(usedNames, sb.toString());
    }

    @Getter
    private Set<String> usedField = new HashSet<>();

    public MethodCallApi findMethodApiToCall(ProcessingEnvironment processingEnv, MethodApiKey _apiKey, MethodConfigKey topMethodConfigKey) {
        // only visible method can by called !!!

        List<MethodApiKey> tryFindApiKeys = new ArrayList<>(2);
        tryFindApiKeys.add(_apiKey);
        if (_apiKey.isApiWithReturnType()) {
            tryFindApiKeys.add(MethodApiKey.createWithoutReturnTypeInParam(_apiKey));
        }

        List<MethodConfigKey> methodConfigKeyList = new ArrayList<>(2);
        methodConfigKeyList.add(topMethodConfigKey);
        if (topMethodConfigKey != null) methodConfigKeyList.add(null);

        MethodApiFullSyntax myMethod = null;
        for (MethodConfigKey methodConfigKey : methodConfigKeyList) {
            List<MethodApiFullSyntax> myUsableMethods = resolveMyUsableMethods(methodConfigKey);

            for (MethodApiKey apiKey : tryFindApiKeys) {
                myMethod = findBestMatchMethodApiFullSyntax(processingEnv, myUsableMethods, apiKey);
                if (myMethod != null) {
                    for (AbstractMethodSourceInfo abstractMethodSourceInfo : methodsToImplement) {
                        if (Objects.equals(abstractMethodSourceInfo.getMethodApiFullSyntax().getApiKey(), apiKey)) {
                            return MethodCallApi.createFrom("", myMethod, abstractMethodSourceInfo);
                        }
                    }

                    // Generated method is not found
                    return MethodCallApi.createFrom("", myMethod, null);
                }
            }

            Map<String, List<MethodApiFullSyntax>> extUsableMethods = resolveExtUsableMethods(methodConfigKey);
            for (Map.Entry<String, List<MethodApiFullSyntax>> e : extUsableMethods.entrySet()) {
                for (MethodApiKey apiKey : tryFindApiKeys) {
                    myMethod = findBestMatchMethodApiFullSyntax(processingEnv, e.getValue(), apiKey);
                    if (myMethod != null) {
                        usedField.add(e.getKey());
                        return MethodCallApi.createFrom(e.getKey() + ".", myMethod, null);
                    }
                }
            }
        }
//        if (myMethod == null) return null;
//        return MethodCallApi.createFrom(path, myMethod, methodsToImplement.get(myMethod));
        return null;
    }

    private MethodApiFullSyntax findBestMatchMethodApiFullSyntax(ProcessingEnvironment processingEnv, List<MethodApiFullSyntax> allMethods, MethodApiKey apiKey) {
        ExecutableType requiredMethodType = apiKey.createMethodExecutableType(processingEnv, this.parentElement);

        for (MethodApiFullSyntax method : allMethods) {
            ExecutableType testedMethodType = method.getApiKey().createMethodExecutableType(processingEnv, this.parentElement);

            if (TypeMethodUtils.isMethodCallableForMapper(processingEnv, requiredMethodType, testedMethodType)) {
                return method;
            }
        }

        return null;
    }

    public MethodCallApi registerNewGeneratedMethod(AbstractMethodSourceInfo methodSourceInfo) {
        MethodApiFullSyntax syntax = methodSourceInfo.getMethodApiFullSyntax();
        methodsToImplement.add(methodSourceInfo);
        resolveMyUsableMethods(null).add(syntax);
        usedNames.add(syntax.getName());

        return MethodCallApi.createFrom(methodSourceInfo);
    }


    private void registerTopMethod(ProcessingEnvironment processingEnv, ExecutableElement method) {
        MethodApiFullSyntax methodSyntax = MethodApiFullSyntax.analyze(processingEnv, (Type) parentElement.asType(), method);
        if (methodSyntax == null || !methodSyntax.getErrorsMapping().isEmpty()) {
            // Ignore bad API
            processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, methodSyntax.getErrorsMapping().toString(), method);
            return;
        }

        if (ApiUtil.canImplementMethod(this.parentTypeAsAbstractClass, method)) {
            // register implementation
            DeclaredMethodSourceInfo methodSourceInfo = new DeclaredMethodSourceInfo(this, methodSyntax, processingEnv, method);
            methodsToImplement.add(methodSourceInfo);
            topMethods.add(methodSourceInfo);

            // Register all custom types for this method
            if (methodSourceInfo.getCustomMethodConfig() != null && !methodSourceInfo.getCustomMethodConfig().getWithCustom().isEmpty()) {
                registerCustomFields(processingEnv, methodSourceInfo.getCustomMethodConfig().getWithCustom(), methodSourceInfo.getMethodConfigKey());
            }
        }
        // only not implemented method can be used (implemented method is wrapped and always allowed for use)
        else if (!ApiUtil.ignoreUsing(true, method)) {
            // register possible using
            resolveMyUsableMethods(null).add(methodSyntax);
        }

    }


//
//	private void generateSubMethods(ProcessingEnvironment processingEnv, AbstractMethodSourceInfo generateMethod, String syntaxMethodName) {
//		MethodApiKey requiredApiKey = generateMethod.getMethodApiFullSyntax().getApiKey();
//		generateMethod.getUsedByMethods().add(syntaxMethodName);
//
//		// ObjRet transf(Obj1 o1, Obj2 o2, Obj3 o3)  =>
//		// ObjRet transf(Obj1 o1, Obj2 o2, Obj3 o3, @Return Obj2 o);
//		if (!requiredApiKey.isApiWithReturnType()) {
//			if (requiredApiKey.getVisibleTypes().length<2 || requiredApiKey.getVisibleTypes()[0]==null) {
//				//TODO // empty implementation with warning
//				return;
//			}
//
//			// We will analyze inputs step by step and create response according to it ...
//			MethodApiKey newMethodApiKey = new MethodApiKey(true, requiredApiKey.getVisibleTypes());
//			MethodApiFullSyntax method = findMethodApiOrCreate(newMethodApiKey);
//
//			generateMethod.getLines().
//
//			// If methodgenerator exists ....
//
//			return;
//		}
//
//		AbstractMethodSourceInfo newMethodRequired = new AbstractMethodSourceInfo(null);
//		;
//	}
//
//	private void generateTransformBody(ProcessingEnvironment processingEnv, AbstractMethodSourceInfo generateMethod, String syntaxMethodName) {
//		MethodApiKey requiredApiKey = generateMethod.getMethodApiFullSyntax().getApiKey();
//	}


    @Override
    public String toString() {
        return "MapperClassInfo{" +
                "parentElement=" + parentElement +
                ", methodsToImplement=" + methodsToImplement +
                ", canUseInCustomer=" + canUseInCustomer +
                '}';
    }

}
