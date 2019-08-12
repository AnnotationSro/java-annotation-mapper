package sk.annotation.library.jam.processor.data.generator.method;

import org.apache.commons.lang3.StringUtils;
import sk.annotation.library.jam.annotations.MapperFieldConfig;
import sk.annotation.library.jam.processor.Constants;
import sk.annotation.library.jam.processor.data.MapperClassInfo;
import sk.annotation.library.jam.processor.data.MethodCallApi;
import sk.annotation.library.jam.processor.data.TypeWithVariableInfo;
import sk.annotation.library.jam.processor.data.generator.row.AbstractRowValueTransformator;
import sk.annotation.library.jam.processor.data.keys.MethodConfigKey;
import sk.annotation.library.jam.processor.data.mapi.MethodApiFullSyntax;
import sk.annotation.library.jam.processor.sourcewriter.ImportsTypeDefinitions;
import sk.annotation.library.jam.processor.sourcewriter.SourceGeneratorContext;
import sk.annotation.library.jam.processor.utils.ElementUtils;
import sk.annotation.library.jam.processor.utils.NameUtils;
import sk.annotation.library.jam.utils.MapperRunCtxData;
import sk.annotation.library.jam.utils.MapperRunCtxDataHolder;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/*
 Used for declaration input values, so the most important things can be wrapped
 each input method is remaked as follows:

 declared public method => mirror method, that is actually fully created

 public Obj transf(Obj in);
   => public Obj _transf(Obj in, @Return Obj ret) {....}
   implementation is :


 public Obj transf(Obj in, @Return Obj ret);
   => public Obj _transf(Obj in, @Return Obj ret) {....}


* */
public class DeclaredMethodSourceInfo extends AbstractMethodSourceInfo {

    protected List<MethodCallApi> requiredMethods = new LinkedList<>();
    protected ExecutableElement method;
    protected MethodConfigKey methodConfigKey;

    public DeclaredMethodSourceInfo(MapperClassInfo ownerClassInfo, MethodApiFullSyntax methodApiParams, ExecutableElement method) {
        super(ownerClassInfo, methodApiParams);
        this.method = method;
        this.methodApiFullSyntax.getModifiers().addAll(method.getModifiers());
        super.methodApiFullSyntax.getAnnotations().getOrAddAnnotation(Constants.annotationOverride);
    }

    @Override
    public void registerImports(ProcessingEnvironment processingEnv, ImportsTypeDefinitions imports) {
        super.registerImports(processingEnv, imports);
        Constants.typeMapperRunCtxDataHolder.registerImports(processingEnv, imports);
    }

    public void analyzeAndGenerateDependMethods(ProcessingEnvironment processingEnv) {
        if (!requiredMethods.isEmpty()) return;
        if (methodApiFullSyntax.getReturnType() == null) return; // body will be empty

        ////////////////////////////////////////////////////////
        // 1)  Create MethodConfigKey
        methodConfigKey = new MethodConfigKey(ownerClassInfo.topMethodsRegistrator.registerTopMethod(method, ownerClassInfo));
        MapperFieldConfig methodConfig = method.getAnnotation(MapperFieldConfig.class);
        if (methodConfig != null) {
            methodConfigKey.getConfigurations().add(methodConfig);
            methodConfigKey.setWithCustomConfig(true);
            ownerClassInfo.getFeatures().setEnableMethodContext(true);
        }
        methodConfigKey.getConfigurations().addAll(ElementUtils.findAllAnnotationsInStructure(processingEnv, ownerClassInfo.getParentElement(), MapperFieldConfig.class));

        ////////////////////////////////////////////////////////
        // 2)  analyze neccessary fields + return Type
        List<TypeWithVariableInfo> inputParams = methodApiFullSyntax.getRequiredParams();
        varRet = null;
        int numInputParams = inputParams.size();
        if (methodApiFullSyntax.isReturnLastParam()) {
            numInputParams--;
            varRet = inputParams.get(numInputParams);
        } else {
            String variableName = NameUtils.findBestName(methodApiFullSyntax.getParams(), "ret");
            TypeWithVariableInfo variable = new TypeWithVariableInfo(variableName, methodApiFullSyntax.getReturnType());
            varRet = new TypeWithVariableInfo(variable.getVariableName(), variable.getVariableType(), null, true);
        }

        ////////////////////////////////////////////////////////
        // 3) Create neccessary transformation methods for analyzed input parameters
        for (int i = 0; i < numInputParams; i++) {

            // 3a) Create methodApiKey
            MethodCallApi methodCallApi = findOrCreateOwnMethod(processingEnv, null, inputParams.get(i).getVariableType(), varRet.getVariableType(), this.methodApiFullSyntax.isReturnLastParamRequired() || i>0);

            if (methodCallApi == null) {
                throw new IllegalStateException("test");
            }

            // If method still doesnt exist, its wrong !!
            requiredMethods.add(methodCallApi);

            // If method is generated by us, run analysis with own settings
            if (methodCallApi.getOutGeneratedMethod() != null) {
                methodCallApi.getOutGeneratedMethod().analyzeAndGenerateDependMethods(processingEnv, methodConfigKey);
            }
        }


        // Update own body
        analyzeAndGenerateDependMethods(processingEnv, methodConfigKey);
    }

    @Override
    protected void analyzeAndGenerateDependMethods(ProcessingEnvironment processingEnv, MethodConfigKey forMethodConfig) {
        // nothing :)
    }
    protected void writeSourceCodeBodyReturn(SourceGeneratorContext ctx) {
        // nothing :)
    }

    @Override
    public boolean writeSourceCode(SourceGeneratorContext ctx) {
        if (this.unwrapModeEnabled) return false;
        return super.writeSourceCode(ctx);
    }

    @Override
    protected void writeSourceCodeBody(SourceGeneratorContext ctx) {

        // Init context ...
        List<TypeWithVariableInfo> bodyVariableNames = new LinkedList<>();
        for (TypeWithVariableInfo param : methodApiFullSyntax.getParams()) {
            usedNames.add(param.getVariableName());
            bodyVariableNames.add(param);
        }

        if (methodConfigKey != null && ownerClassInfo.getFeatures().isEnableMethodContext()) {
            bodyVariableNames.add(new TypeWithVariableInfo(methodConfigKey.getForTopMethod(), Constants.methodParamInfo_ctxForMethodId));
        }

        if (varCtxVariable == null && !ownerClassInfo.getFeatures().isDisabledToUseMapperRunCtxData()) {
            String bestName = NameUtils.findBestNameAndUpdateSet(usedNames, Constants.methodParamInfo_ctxForRunData.getVariableName());
            varCtxVariable = new TypeWithVariableInfo(bestName, Constants.methodParamInfo_ctxForRunData);
            bodyVariableNames.add(varCtxVariable);

            ctx.pw.print("\n");
            varCtxVariable.writeSourceCode(ctx, true, false);
            ctx.pw.print(" = ");
            if (!ownerClassInfo.getFeatures().isDisabled_SHARED_THREAD_CONTEXT()) {
                ctx.pw.print(MapperRunCtxDataHolder.class.getSimpleName());
                ctx.pw.print(".data.get();");
            } else {
                ctx.pw.print("new ");
                ctx.pw.print(MapperRunCtxData.class.getSimpleName());
                ctx.pw.print("();");
            }
        }
        String mngCtx = null;
        if (!ownerClassInfo.getFeatures().isDisabled_SHARED_THREAD_CONTEXT()) {
            mngCtx = NameUtils.findBestNameAndUpdateSet(this.usedNames, "mng" + StringUtils.capitalize(varCtxVariable.getVariableName()));
            ctx.pw.print("\nboolean " + mngCtx + " = " + varCtxVariable.getVariableName() + "==null;");
            ctx.pw.print("\n\ntry {");
            ctx.pw.levelSpaceUp();
            ctx.pw.print("\nif (" + mngCtx + ") {\n\t");
            ctx.pw.print(varCtxVariable.getVariableName());
            ctx.pw.print(" = ");
            ctx.pw.print("new " + MapperRunCtxData.class.getSimpleName() + "();");
            ctx.pw.print("\n\t" + MapperRunCtxDataHolder.class.getSimpleName() + ".data.set(" + varCtxVariable.getVariableName() + ");");
            ctx.pw.print("\n}");
        }

        // register requiredContextValues
        if (!ownerClassInfo.getFeatures().isDisabled_CONTEXT_VALUES()) {
            for (TypeWithVariableInfo ctxParam : methodApiFullSyntax.getParams()) {
                if (ctxParam.getHasContextKey() != null) {
//				if (Constants.reservedNameForMethodId.equals(ctxParam.getHasContextKey())) continue;
//                if (ownerClassInfo.isDisabledFeature(MapperFeature.PERSISTED_DATA_IN_LOCAL_THREAD)) {
                    if (MethodCallApi.ctx_equals(Constants.methodParamInfo_ctxForRunData, ctxParam)) continue;
//                }
                    ctx.pw.printNewLine();
                    ctxParam.genSourceForPutContext(ctx, ctxParam.getVariableName(), this);
                    ctx.pw.print(";");
                }
            }
            ctx.pw.printNewLine();
        }

        // Call transformation methods ...
        if (!methodApiFullSyntax.isReturnLastParam()) {
            ctx.pw.print("\n");
            varRet.writeSourceCode(ctx, true, false);
            ctx.pw.print(" = null;");
        }

        int i = 0;
        List<TypeWithVariableInfo> inputParams = methodApiFullSyntax.getRequiredParams();
        for (MethodCallApi methodCallApi : requiredMethods) {
            // 3a) Create methodApiKey
            List<TypeWithVariableInfo> paramsForApi = new LinkedList<>();
            paramsForApi.add(inputParams.get(i++));
            paramsForApi.add(varRet);
            ctx.pw.print("\n");
            ctx.pw.print(varRet.getVariableName());
            ctx.pw.print(" = ");
            methodCallApi.genSourceForCallWithVariableParams(ctx, paramsForApi, bodyVariableNames, this);
            ctx.pw.print(";");
        }

        ctx.pw.print("\nreturn " + varRet.getVariableName() + ";");

        // clear context ...
        if (mngCtx != null && !ownerClassInfo.getFeatures().isDisabled_SHARED_THREAD_CONTEXT()) {
            ctx.pw.levelSpaceDown();
            ctx.pw.print("\n}");
            ctx.pw.print("\nfinally {");
            ctx.pw.print("\n\tif (" + mngCtx + ") " + MapperRunCtxDataHolder.class.getSimpleName() + ".data.remove();");
            ctx.pw.print("\n}\n");
        }
    }//*/


    private boolean unwrapModeEnabled = false;

    private boolean canUnwrapMethod(ProcessingEnvironment processingEnv) {
        if (requiredMethods.size() != 1) return false;

        MethodCallApi methodCallApi = requiredMethods.get(0);
        if (methodCallApi.getOutGeneratedMethod() instanceof SimpleMethodApi_RowTransform_SourceInfo) {
            return true;
        }

        if (!ownerClassInfo.getFeatures().isDisabled_SHARED_THREAD_CONTEXT()) return false;
        if (!ownerClassInfo.getFeatures().isDisabled_CYCLIC_MAPPING()) return false;
        if (!ownerClassInfo.getFeatures().isDisabled_CONTEXT_VALUES()) return false;

        if (StringUtils.isNotEmpty(methodCallApi.getPathToSyntax())) return false;

//        if (!Objects.equals(methodCallApi.getMethodSyntax().getApiKey(), this.methodApiFullSyntax.getApiKey()))
//            return;//ApiKey is the same

        if (this.methodApiFullSyntax.isReturnLastParamRequired() != methodCallApi.getMethodSyntax().isReturnLastParamRequired())
            return false;

        // unwrap =>
        return true;
    }

    public void tryUnwrapMethods(ProcessingEnvironment processingEnv) {
        if (!canUnwrapMethod(processingEnv)) return;

        this.unwrapModeEnabled = true;
        MethodCallApi methodCallApi = requiredMethods.get(0);
        // unwrap =>
        methodCallApi.getMethodSyntax().setName(methodApiFullSyntax.getName());
        methodCallApi.getMethodSyntax().getAnnotations().mergeValues(methodApiFullSyntax.getAnnotations());
        methodCallApi.getMethodSyntax().getModifiers().clear();
        methodCallApi.getMethodSyntax().getModifiers().addAll(methodApiFullSyntax.getModifiers());
        methodCallApi.getMethodSyntax().getParams().clear();
        methodCallApi.getMethodSyntax().getParams().addAll(methodApiFullSyntax.getParams());
//        methodCallApi.getMethodSyntax().getParams().addAll(methodCallApi.getOutGeneratedMethod().getMethodApiFullSyntax().getParams());
        if (methodCallApi.getMethodSyntax().isReturnLastParamRequired() != methodApiFullSyntax.isReturnLastParamRequired()) {
            if (!methodCallApi.getMethodSyntax().isReturnLastParamRequired()) {
                Iterator<TypeWithVariableInfo> iterator = methodCallApi.getMethodSyntax().getParams().iterator();
                while (iterator.hasNext()) {
                    TypeWithVariableInfo next = iterator.next();
                    if (next.isMarkedAsReturn()) {
                        iterator.remove();
                        methodCallApi.getMethodSyntax().setReturnLastParam(false);
                        break;
                    }
                }
            }
        }
    }
}
