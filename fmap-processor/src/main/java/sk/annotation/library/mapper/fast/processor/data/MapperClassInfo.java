package sk.annotation.library.mapper.fast.processor.data;

import com.sun.tools.javac.code.Type;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import sk.annotation.library.mapper.fast.annotations.FastMapper;
import sk.annotation.library.mapper.fast.annotations.MapperFieldConfig;
import sk.annotation.library.mapper.fast.processor.Constants;
import sk.annotation.library.mapper.fast.processor.data.mapi.MethodApiFullSyntax;
import sk.annotation.library.mapper.fast.processor.data.mapi.MethodApiKey;
import sk.annotation.library.mapper.fast.processor.data.methodgenerator.AbstractMethodSourceInfo;
import sk.annotation.library.mapper.fast.processor.data.methodgenerator.DeclaredMethodSourceInfo;
import sk.annotation.library.mapper.fast.processor.sourcewriter.ImportsTypeDefinitions;
import sk.annotation.library.mapper.fast.processor.utils.*;
import sk.annotation.library.mapper.fast.utils.MapperInstanceUtil;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
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
	final List<MapperFieldConfig> classAndPackageConfigurations;

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
	final public FastMapper fastMapperConfig;
	final public boolean parentTypeAsAbstractClass;

	final public List<Element> canUseInCustomer = new LinkedList<>();

	final public ImportsTypeDefinitions imports;
	final private Set<Modifier> mapperModifiers = new LinkedHashSet<>();
	final public List<FieldInfo> fieldsToImplement = new LinkedList<>();
	final public ConstantsMethodGeneratorInfo topMethodsRegistrator = new ConstantsMethodGeneratorInfo();


	final public AnnotationsInfo generateAnnotations;

	@Getter
	protected FeatureSourceUtils features;


	private MapperClassInfo(ProcessingEnvironment processingEnv, TypeElement element) {
		this.parentElement = element;
		this.fastMapperConfig = element.getAnnotation(FastMapper.class);
		this.fullClassName = ElementUtils.getQualifiedName(this.parentElement) + MapperInstanceUtil.constPostFixClassName;
		this.simpleClassName = NameUtils.getClassSimpleName(this.fullClassName);

		if (!ApiUtil.canImplementMapper(element)) {
			throw new IllegalStateException("Mapper " + ElementUtils.getQualifiedName(element) + " cannot implement!");
		}
		this.parentTypeAsAbstractClass = parentElement.getKind() == ElementKind.CLASS;

		this.features = new FeatureSourceUtils(element);

		// Prepare IMPORT SCOPE !!!
		imports = new ImportsTypeDefinitions(element);

		if (parentElement.getModifiers().contains(Modifier.PUBLIC))
			mapperModifiers.add(Modifier.PUBLIC);
		else if (parentElement.getModifiers().contains(Modifier.PROTECTED))
			mapperModifiers.add(Modifier.PROTECTED);
		else if (parentElement.getModifiers().contains(Modifier.DEFAULT))
			mapperModifiers.add(Modifier.DEFAULT);

		// Configuration ...

		classAndPackageConfigurations = ElementUtils.findAllAnnotationsInStructure(processingEnv, element, MapperFieldConfig.class);


		// Annotations ...
		generateAnnotations = new AnnotationsInfo()
				.withAnnotation(Constants.annotationFastMapperGenerated)
				.mergeValues(IoCUtils.resolveMapperAnnotation(parentElement))
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
		AnnotationsInfo fieldInjectionAnnotations = IoCUtils.getFieldAnnotationType(parentElement, null);
		registerCustomFields(processingEnv, fieldInjectionAnnotations, element);
		for (ExecutableElement method : allMethods) {
			registerCustomFields(processingEnv, fieldInjectionAnnotations, method);
		}

		// Analyze customClass and fields
		for (VariableElement field : allFields) {
			registerField(processingEnv, field);
		}

		// Analyze methods ...
		for (ExecutableElement method : allMethods) {
			registerMethod(processingEnv, method);
		}


		// analyze classes to uses
		for (DeclaredMethodSourceInfo analyzedMethodSourceInfo : topMethods) {
			analyzedMethodSourceInfo.analyzeAndGenerateDependMethods(processingEnv);
		}


//		//////////////////////////////
//		// START: testing data
//
//		mapperModifiers.remove(Modifier.ABSTRACT);
//
//		// END: testing data
//		//////////////////////////////

	}


	private void registerCustomFields(ProcessingEnvironment processingEnv, AnnotationsInfo fieldInjectionAnnotations, Element element) {
		List<Type> values = AnnotationValueUtils.findWithCustomClasses(processingEnv, element);
		if (values == null || values.isEmpty()) return;

		for (Type fieldType : values) {
			for (Type existFieldValue : allFieldsTypes.values()) {
				if (processingEnv.getTypeUtils().isSameType(existFieldValue, fieldType)) continue; // uz existuje
			}

			// Skusime nazov
			String fieldName = NameUtils.findBestName(usedNames, StringUtils.uncapitalize(NameUtils.getClassSimpleName(fieldType.toString())));
			usedNames.add(fieldName);
			allFieldsTypes.put(fieldName, fieldType);

			fieldsToImplement.add(new FieldInfo(fieldName, new TypeInfo(fieldType)).withInjections(fieldInjectionAnnotations));
			List<ExecutableElement> methods = ApiUtil.readElementApi(processingEnv, fieldType);
			registerApiForPath(processingEnv, fieldName, methods);
		}
	}

	Map<String, Type> allFieldsTypes = new LinkedHashMap<>();

	private void registerField(ProcessingEnvironment processingEnv, VariableElement field) {
		if (ApiUtil.ignoreUsing(false, field)) return;
		Type type = TypeUtils.findType(field);
		allFieldsTypes.put(field.getSimpleName().toString(), type);
		List<ExecutableElement> methods = ApiUtil.readElementApi(processingEnv, type);
		registerApiForPath(processingEnv, field.getSimpleName().toString(), methods);
	}

	private void registerApiForPath(ProcessingEnvironment processingEnv, String pathApi, List<ExecutableElement> executableElements) {
		Map<MethodApiKey, MethodApiFullSyntax> mapApi = extUsableMethods.computeIfAbsent(pathApi, a -> new HashMap<>());

		// treba zistit, na co mozem byt pouzivany dany field
		for (ExecutableElement method : executableElements) {
			if (ApiUtil.ignoreUsing(false, method)) continue;

			MethodApiFullSyntax methodSyntax = MethodApiFullSyntax.analyze(processingEnv, method);
			if (methodSyntax.getReturnType()==null) continue;

			mapApi.put(methodSyntax.getApiKey(), methodSyntax);
		}
	}


	@Getter
	private Map<MethodApiKey, MethodApiFullSyntax> myUsableMethods = new LinkedHashMap<>();
	private Map<String, Map<MethodApiKey, MethodApiFullSyntax>> extUsableMethods = new LinkedHashMap<>();
	@Getter
	private Map<MethodApiFullSyntax, AbstractMethodSourceInfo> methodsToImplement = new LinkedHashMap<>();
	private List<DeclaredMethodSourceInfo> topMethods = new LinkedList<>();

	public String findBestNewMethodName_transformFromTo(ProcessingEnvironment processingEnv, MethodApiKey transformApiKey) {
		StringBuilder sb = new StringBuilder();
		sb.append("transf");

		TypeMirror visibleType = transformApiKey.getVisibleTypes()[0];
		if (visibleType!=null) {
			String strType = TypeUtils.getClassSimpleName(visibleType);
			if (StringUtils.isNotEmpty(strType)) {
				sb.append("_to").append(strType);
			}


			// parameters
			if (visibleType instanceof Type.ClassType) {
				Type.ClassType tp = (Type.ClassType) visibleType;
				if (tp.allparams()!=null && !tp.allparams().isEmpty()) {
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
	public String findBestNewMethodName(ProcessingEnvironment processingEnv, String newExpectedName, List<TypeWithVariableInfo> nexExpectedParams) {
		return NameUtils.findBestName(usedNames, newExpectedName);
	}

	@Getter
	private Set<String> usedField = new HashSet<>();
	public MethodCallApi findMethodApiToCall(MethodApiKey apiKey) {
		// only visible method can by called !!!

		String path = "";
		MethodApiFullSyntax myMethod = myUsableMethods.get(apiKey);
		if (myMethod == null) {
			for (Map.Entry<String, Map<MethodApiKey, MethodApiFullSyntax>> e : extUsableMethods.entrySet()) {
				myMethod = e.getValue().get(apiKey);
				if (myMethod != null) {
					path = e.getKey() + ".";
					usedField.add(e.getKey());
					break;
				}
			}
		}
		if (myMethod == null) return null;

		return MethodCallApi.createFrom(path, myMethod, methodsToImplement.get(myMethod));
	}

	public MethodCallApi registerNewGeneratedMethod(AbstractMethodSourceInfo methodSourceInfo) {
		MethodApiFullSyntax syntax = methodSourceInfo.getMethodApiFullSyntax();
		methodsToImplement.put(syntax, methodSourceInfo);
		myUsableMethods.put(syntax.getApiKey(), syntax);
		usedNames.add(syntax.getName());

		return MethodCallApi.createFrom(methodSourceInfo);
	}


	private void registerMethod(ProcessingEnvironment processingEnv, ExecutableElement method) {
		MethodApiFullSyntax methodSyntax = MethodApiFullSyntax.analyze(processingEnv, method);

		if (ApiUtil.canImplementMethod(this.parentTypeAsAbstractClass, method)) {
			// register implementation
			DeclaredMethodSourceInfo methodSourceInfo = new DeclaredMethodSourceInfo(this, methodSyntax, method);
			methodsToImplement.put(methodSyntax, methodSourceInfo);
			topMethods.add(methodSourceInfo);
		}
		// pouzivas sa moze iba neimplementovana metoda (implementovana metoda je wrapnuta d vzdy povolena na pouzitie):
		else if (!ApiUtil.ignoreUsing(true, method)) {
			// register possible using
			myUsableMethods.put(methodSyntax.getApiKey(), methodSyntax);
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
//			// Postupne budeme analyzovat vstupy d podla toho vytvorime odpoved ...
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
