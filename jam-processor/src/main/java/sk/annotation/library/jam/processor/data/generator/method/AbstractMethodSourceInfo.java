package sk.annotation.library.jam.processor.data.generator.method;

import sk.annotation.library.jam.annotations.enums.MapperFeature;
import sk.annotation.library.jam.processor.Constants;
import sk.annotation.library.jam.processor.data.MapperClassInfo;
import sk.annotation.library.jam.processor.data.MethodCallApi;
import sk.annotation.library.jam.processor.data.TypeInfo;
import sk.annotation.library.jam.processor.data.TypeWithVariableInfo;
import sk.annotation.library.jam.processor.data.constructors.TypeConstructorInfo;
import sk.annotation.library.jam.processor.data.generator.row.AbstractRowValueTransformator;
import sk.annotation.library.jam.processor.data.keys.MethodConfigKey;
import sk.annotation.library.jam.processor.data.mapi.MethodApiFullSyntax;
import sk.annotation.library.jam.processor.data.mapi.MethodApiKey;
import sk.annotation.library.jam.processor.sourcewriter.ImportsTypeDefinitions;
import sk.annotation.library.jam.processor.sourcewriter.SourceGenerator;
import sk.annotation.library.jam.processor.sourcewriter.SourceGeneratorContext;
import sk.annotation.library.jam.processor.sourcewriter.SourceRegisterImports;
import sk.annotation.library.jam.processor.utils.NameUtils;
import sk.annotation.library.jam.processor.utils.TypeUtils;
import sk.annotation.library.jam.processor.utils.commons.StringEscapeUtils;
import sk.annotation.library.jam.processor.utils.commons.StringUtils;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.type.TypeMirror;
import java.util.*;

abstract public class AbstractMethodSourceInfo implements SourceGenerator, SourceRegisterImports {
    final protected MethodApiFullSyntax methodApiFullSyntax;
    public MethodApiFullSyntax getMethodApiFullSyntax() {
        return methodApiFullSyntax;
    }

    final protected MapperClassInfo ownerClassInfo;
    public MapperClassInfo getOwnerClassInfo() {
        return ownerClassInfo;
    }

    final protected List<SourceRegisterImports> sourcesForImports = new LinkedList<>();
    final protected Set<String> usedNames = new HashSet<>(); // in method context !!!

    public AbstractMethodSourceInfo(MapperClassInfo ownerClassInfo, MethodApiFullSyntax methodApiFullSyntax) {
        this.ownerClassInfo = ownerClassInfo;
        this.methodApiFullSyntax = methodApiFullSyntax;
        this.usedNames.addAll(ownerClassInfo.getUsedNames());
        for (TypeWithVariableInfo param : this.methodApiFullSyntax.getParams()) {
            this.usedNames.add(param.getVariableName());
        }
    }


    private List<MethodCallApi> interceptors = null;
    protected boolean hasInterceptors() {
        return interceptors!=null && !interceptors.isEmpty();
    }
    final private Set<MethodApiFullSyntax> usedByMethods = new HashSet<>();

    final protected void analyzeAndGenerateDependMethods(ProcessingEnvironment processingEnv, MethodConfigKey forMethodConfig, AbstractMethodSourceInfo parentMethod) {
        if (interceptors == null && canCallInterceptors() && methodApiFullSyntax.getReturnType() != null) {
            List<TypeWithVariableInfo> requiredParams = this.methodApiFullSyntax.getRequiredParams();
            if (requiredParams.size() > 0) {
                //Call find interceptors (mark as used fields)
                interceptors = ownerClassInfo.getAllInterceptors(processingEnv, requiredParams.get(0).getVariableType().getType(processingEnv), methodApiFullSyntax.getReturnType().getType(processingEnv));
                if (interceptors == null) interceptors = Collections.emptyList();
            }
        }


        if (parentMethod != null && parentMethod.getMethodApiFullSyntax() != null) {
            usedByMethods.add(parentMethod.methodApiFullSyntax);
        }
        analyzeAndGenerateDependMethods(processingEnv, forMethodConfig);
    }

    abstract protected void analyzeAndGenerateDependMethods(ProcessingEnvironment processingEnv, MethodConfigKey forMethodConfig);

    public boolean hasMultipleVariants(ProcessingEnvironment processingEnv) {
        return false;
    }

    protected TypeWithVariableInfo varCtxVariable;
    public TypeWithVariableInfo getVarCtxMethodId() {
        return varCtxMethodId;
    }

    protected TypeWithVariableInfo varCtxMethodId;
    public TypeWithVariableInfo getVarCtxVariable() {
        return varCtxVariable;
    }

    protected TypeWithVariableInfo varRet;
    public TypeWithVariableInfo getVarRet() {
        return varRet;
    }

    @Override
    public boolean writeSourceCode(SourceGeneratorContext ctx) {
        methodApiFullSyntax.writeMethodDeclaration(ctx);

        ctx.pw.print(" {");
        ctx.pw.levelSpaceUp();

        List<TypeWithVariableInfo> requiredParams = methodApiFullSyntax.getRequiredParams();
        if (!requiredParams.isEmpty()) {
            boolean hasPrimitiveInput = false;
            for (TypeWithVariableInfo requiredParam : requiredParams) {
                if (requiredParam.getVariableType().getType(ctx.processingEnv).getKind().isPrimitive()) {
                    hasPrimitiveInput = true;
                    break;
                }
            }

            if (!hasPrimitiveInput) {
                ctx.pw.print("\n// check null inputs ");
                ctx.pw.print("\nif (");
                boolean addAnd = false;
                for (TypeWithVariableInfo requiredParam : requiredParams) {
                    if (requiredParam.isMarkedAsReturn()) continue;
                    if (addAnd) ctx.pw.print(" && ");
                    addAnd = true;
                    ctx.pw.print(requiredParam.getVariableName());
                    ctx.pw.print("==" + TypeUtils.createNullValue(requiredParam.getVariableType().getType(ctx.processingEnv)));
                }
                ctx.pw.print(") return");

                if (methodApiFullSyntax.getReturnType() != null) {
                    varRet = null;
                    if (this.methodApiFullSyntax.isGenerateReturnParamRequired()) {
                        for (TypeWithVariableInfo param : methodApiFullSyntax.getParams()) {
                            if (param.isMarkedAsReturn() && this.methodApiFullSyntax.isGenerateReturnParamRequired()) {
                                varRet = param;
                                break;
                            }
                        }
                    }

                    if (varRet != null) {
                        ctx.pw.print(" " + varRet.getVariableName());
                    }
                    else {
                        ctx.pw.print(" " + TypeUtils.createNullValue(methodApiFullSyntax.getReturnType().getType(ctx.processingEnv)));
                    }
                }
                ctx.pw.print(";\n");
            }
        }


        this.usedNames.clear();
        this.usedNames.addAll(ownerClassInfo.getUsedNames());
        for (TypeWithVariableInfo param : this.methodApiFullSyntax.getParams()) {
            this.usedNames.add(param.getVariableName());
        }
        varCtxMethodId = MethodCallApi.ctx_findVariable(Constants.methodParamInfo_ctxForMethodId, methodApiFullSyntax.getParams());
        if (varCtxMethodId != null) usedNames.add(varCtxMethodId.getVariableName());
        else if (ownerClassInfo.getFeatures().isRequiredInputWithMethodId()) {
            if (!(this instanceof DeclaredMethodSourceInfo)) {
                throw new IllegalStateException("Problem with required variable!");
            }
        }
        varCtxVariable = MethodCallApi.ctx_findVariable(Constants.methodParamInfo_ctxForRunData, methodApiFullSyntax.getParams());
        if (varCtxVariable != null) usedNames.add(varCtxVariable.getVariableName());
        else if (ownerClassInfo.getFeatures().isRequiredInputWithContextData()) {
            if (!(this instanceof DeclaredMethodSourceInfo)) {
                throw new IllegalStateException("Problem with required variable!");
            }
        }

        varRet = null;
        if (methodApiFullSyntax.getReturnType() != null) {
            for (TypeWithVariableInfo param : methodApiFullSyntax.getParams()) {
                if (param.isMarkedAsReturn()) {
                    varRet = param;
                    break;
                }
            }
            if (varRet == null) {
                String bestRetName = NameUtils.findBestNameAndUpdateSet(this.usedNames, "ret");
                varRet = new TypeWithVariableInfo(bestRetName, methodApiFullSyntax.getReturnType(), null, false);
            }
        }
        if (varRet != null) usedNames.add(varRet.getVariableName());

        // Here will be test
        writeSourceCodeBody(ctx);
        writeSourceCodeBodyReturn(ctx);

        ctx.pw.levelSpaceDown();
        ctx.pw.print("\n}");

        return true;
    }

    protected boolean canCallInterceptors() {
        return true;
    }

    protected void writeSourceCodeBodyReturn(SourceGeneratorContext ctx) {
        if (varRet != null) {
            List<TypeWithVariableInfo> requiredParams = methodApiFullSyntax.getRequiredParams();
            if (requiredParams.size() > 0) {
                writeInterceptors(ctx, requiredParams.get(0), varRet);
            }
        }

        if (methodApiFullSyntax.getReturnType() != null) {
            ctx.pw.print("\nreturn " + varRet.getVariableName() + ";");
        }

    }

    protected void writeSourceInstanceCacheLoad(SourceGeneratorContext ctx, TypeWithVariableInfo input, TypeWithVariableInfo varRet) {
        if (!ownerClassInfo.getFeatures().isDisabled_CYCLIC_MAPPING()) {
            ctx.pw.printNewLine();
            ctx.pw.print("\n// Check cyclic mapping - can disable " + ownerClassInfo.getFeatures().getInfoHowCanBeDisabled(MapperFeature.PREVENT_CYCLIC_MAPPING));
            ctx.pw.print("\n" + Constants.typeInstanceCacheValue.getClsType().getSimpleName() + "<");
            varRet.getVariableType().writeSourceCode(ctx);
            ctx.pw.print(
                    "> cacheValue = "
                            + varCtxVariable.getVariableName()
                            + ".getInstanceCache().getCacheValues(\"" + StringEscapeUtils.escapeJava(methodApiFullSyntax.getName())
                            + "\", "
                            + input.getVariableName()
                            + ");"
            );


            ctx.pw.print("\n");
            if (this.methodApiFullSyntax.isGenerateReturnParamRequired())
                ctx.pw.print("if (" + varRet.getVariableName() + "==" + TypeUtils.createNullValue(varRet.getVariableType().getType(ctx.processingEnv)) + ") \n\t");
            ctx.pw.print("if (cacheValue.isRegisteredAnyValue()) return cacheValue.getValue();");
            ctx.pw.print("\n");
            if (this.methodApiFullSyntax.isGenerateReturnParamRequired()) {
                ctx.pw.print("\nelse if (cacheValue.isRegistered(" + varRet.getVariableName() + ")");
                ctx.pw.print(") \n\treturn " + varRet.getVariableName() + ";\n\n");
            }
        }
    }

    protected void writeSourceInstanceCacheRegister(SourceGeneratorContext ctx, TypeWithVariableInfo input, TypeWithVariableInfo varRet) {
        if (!ownerClassInfo.getFeatures().isDisabled_CYCLIC_MAPPING()) {
            if (varCtxVariable == null) throw new IllegalStateException("Illegal state work");
            ctx.pw.print("\ncacheValue.registerValue(" + varRet.getVariableName() + ");"
            );
        }
    }

    protected void writeInterceptors(SourceGeneratorContext ctx, TypeWithVariableInfo varSrc, TypeWithVariableInfo varRet) {
        if (varSrc == null || varRet == null) return;

        // Find all interceptors
        if (interceptors!=null && !interceptors.isEmpty()) {
            List<TypeWithVariableInfo> otherVariables = methodApiFullSyntax.getParams();

            List<String> params = new ArrayList<>(2);
            params.add(varSrc.getVariableName());
            params.add(varRet.getVariableName());
            ctx.pw.printNewLine();
            ctx.pw.print("\n// Call Interceptors ... ");
            for (MethodCallApi methodCallApi : interceptors) {
                ctx.pw.print("\n");
                methodCallApi.genSourceForCallWithStringParam(ctx, params, otherVariables, this);
                ctx.pw.print(";");
            }
        }

    }

    protected void writeConstructor(SourceGeneratorContext ctx, TypeWithVariableInfo field) {
        MethodApiKey constructorApiKey = new MethodApiKey(field.getVariableType(), Collections.emptyList());
        MethodCallApi methodCallApi = ownerClassInfo.findMethodApiToCall(ctx.processingEnv, constructorApiKey, null /*TODO: This constructore has to be created during analyzes */);
        if (methodCallApi != null) {
            if (StringUtils.isNotEmpty(methodCallApi.getPathToSyntax())) {
                ctx.pw.print(methodCallApi.getPathToSyntax());
                if (!StringUtils.endsWith(methodCallApi.getPathToSyntax(), ".")) ctx.pw.print(".");
                ctx.pw.print(methodCallApi.getMethodSyntax().getName());
                ctx.pw.print("()");
                return;
            }

            ctx.pw.print(ownerClassInfo.getSimpleClassName());
            ctx.pw.print(".this.");
            ctx.pw.print(methodCallApi.getMethodSyntax().getName());
            ctx.pw.print("()");
            return;
        }

        new TypeConstructorInfo(varRet.getVariableType(), false).writeSourceCode(ctx);
    }


    abstract protected void writeSourceCodeBody(SourceGeneratorContext ctx);
//	{
//		if (!bodyGenerator.isEmpty()) {
//			boolean advanceModeRequied = bodyGenerator.size() == 1;
//
//			boolean separatorMethodsRequired = false;
//			for (Map.Entry<MethodConfigKey, BodyGenerator> entry : bodyGenerator.entrySet()) {
//				MethodConfigKey key = entry.getKey();
//				BodyGenerator body = entry.getValue();
//
//				if (separatorMethodsRequired) {
//					ctx.pw.printNewLine();
//				}
//				separatorMethodsRequired = true;
//
//				if (advanceModeRequied) {
//					ctx.pw.print("\n// TODO: Implements for ");
//					ctx.pw.print(key.getForTopMethod());
//					ctx.pw.print("\n{");
//					ctx.pw.levelSpaceUp();
//				}
//				body.writeSourceCode(ctx);
//				if (advanceModeRequied) {
//					ctx.pw.levelSpaceDown();
//					ctx.pw.print("\n}");
//				}
//			}
//		}
//
//	}

    @Override
    public void registerImports(ProcessingEnvironment processingEnv, ImportsTypeDefinitions imports) {
        if (!ownerClassInfo.getFeatures().isDisabled_CYCLIC_MAPPING()) {
            Constants.typeInstanceCacheValue.registerImports(processingEnv, imports);
        }
        methodApiFullSyntax.registerImports(processingEnv, imports);
        for (SourceRegisterImports v : sourcesForImports) {
            v.registerImports(processingEnv, imports);
        }
    }


    protected MethodCallApi findOrCreateOwnMethod(ProcessingEnvironment processingEnv, MethodConfigKey forMethodConfig, String requiredMethodName, TypeMirror sourceType, TypeMirror destinationType) {
        return findOrCreateOwnMethod(processingEnv, forMethodConfig, requiredMethodName, sourceType, destinationType, this.methodApiFullSyntax.isReturnLastParamRequired());
    }

    protected MethodCallApi findOrCreateOwnMethod(ProcessingEnvironment processingEnv, MethodConfigKey forMethodConfig, String requiredMethodName, TypeInfo sourceType, TypeInfo destinationType, boolean returnLastParamRequired) {
        return findOrCreateOwnMethod(processingEnv, forMethodConfig, requiredMethodName, sourceType.getType(processingEnv), destinationType.getType(processingEnv), returnLastParamRequired);
    }

    private MethodCallApi findOrCreateOwnMethod(ProcessingEnvironment processingEnv, MethodConfigKey forMethodConfig, String requiredMethodName, TypeMirror sourceType, TypeMirror destinationType, boolean returnLastParamRequired) {

        // Create transform value
        TypeInfo inType = new TypeInfo(sourceType);
        TypeInfo retType = new TypeInfo(destinationType);

        List<TypeWithVariableInfo> subMethodParams = new LinkedList<>();
        subMethodParams.add(Constants.methodParamInfo_ctxForMethodId);
        subMethodParams.add(Constants.methodParamInfo_ctxForRunData);
        subMethodParams.add(new TypeWithVariableInfo("in", inType, null, false));
        subMethodParams.add(new TypeWithVariableInfo("out", retType, null, true));
        MethodApiKey transformApiKey = new MethodApiKey(retType, subMethodParams);

        // We search for method here, but if it doesnt exist, we create our own version
        MethodCallApi methodCallApi = ownerClassInfo.findMethodApiToCall(processingEnv, transformApiKey, forMethodConfig);
        if (methodCallApi != null) {
            if (returnLastParamRequired && methodCallApi.getMethodSyntax() != null) {
                methodCallApi.getMethodSyntax().setReturnLastParamRequired(true);
            }

            // Check type values
            ownerClassInfo.getFeatures().checkContextValuesInMethodInputs(methodCallApi.getMethodSyntax().getParams());
            return methodCallApi;
        }

        AbstractRowValueTransformator rowFieldGenerator = AbstractRowValueTransformator.findRowFieldGenerator(processingEnv, ownerClassInfo, sourceType, destinationType);
        if (rowFieldGenerator != null) {
            return MethodCallApi.createFrom(sourceType, destinationType, rowFieldGenerator);
        }

        // We create our own mapper for method
        String subMethodName = ownerClassInfo.findBestNewMethodName_transformFromTo(processingEnv, transformApiKey);
        MethodApiFullSyntax subMethodApiSyntax = new MethodApiFullSyntax(processingEnv, subMethodName, retType, subMethodParams, returnLastParamRequired);
        methodCallApi = ownerClassInfo.registerNewGeneratedMethod(findBestMethodGenerator(processingEnv, ownerClassInfo, subMethodApiSyntax, sourceType, destinationType));

        if (methodCallApi == null) throw new IllegalStateException("Unexpected situation !!!");


        return methodCallApi;
    }

    protected static AbstractMethodSourceInfo findBestMethodGenerator(ProcessingEnvironment processingEnv, MapperClassInfo ownerClassInfo, MethodApiFullSyntax subMethodApiSyntax, TypeMirror srcType, TypeMirror dstType) {

        // Check if its mapping collection onto collection
        if (srcType == null || dstType == null) return new EmptyMethodSourceInfo(ownerClassInfo, subMethodApiSyntax);

        TypeMirror[] types = new TypeMirror[]{
                TypeUtils.getBaseTypeWithoutParametrizedFields(srcType),
                TypeUtils.getBaseTypeWithoutParametrizedFields(dstType)
        };

        // Implemented Map
        if (TypeUtils.isSameTypes(processingEnv, Map.class, types)) {
            return new SimpleMethodApi_Map_SourceInfo(ownerClassInfo, subMethodApiSyntax);
        }

        // Implemented List
        if (TypeUtils.isArrayOrCollection(processingEnv, types)) {
            return new SimpleMethodApi_Collection_SourceInfo(ownerClassInfo, subMethodApiSyntax);
        }

        // Mapping simple structures ...
        AbstractRowValueTransformator rowFieldGenerator = AbstractRowValueTransformator.findRowFieldGenerator(processingEnv, ownerClassInfo, types[0], types[1]);
        if (rowFieldGenerator != null) {
            return new SimpleMethodApi_RowTransform_SourceInfo(ownerClassInfo, subMethodApiSyntax, rowFieldGenerator);
        }

        // Mapping expected types ...
        if (areEnums(processingEnv, types)) {
            return new SimpleMethodApi_Enum_SourceInfo(ownerClassInfo, subMethodApiSyntax);
        }

        // Default generator (copy object) ...
        return new SimpleMethodApi_CopyField_SourceInfo(ownerClassInfo, subMethodApiSyntax);
    }

    protected static boolean areEnums(ProcessingEnvironment processingEnv, TypeMirror... types) {
        for (TypeMirror tp : types) {
            if (!TypeUtils.isEnunType(processingEnv, tp)) return false;
        }
        return true;
    }

}
