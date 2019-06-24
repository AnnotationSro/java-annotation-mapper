package sk.annotation.library.mapper.jam.processor.data.methodgenerator;

import org.apache.commons.lang3.StringUtils;
import sk.annotation.library.mapper.jam.annotations.MapperFieldConfig;
import sk.annotation.library.mapper.jam.processor.Constants;
import sk.annotation.library.mapper.jam.processor.data.MapperClassInfo;
import sk.annotation.library.mapper.jam.processor.data.MethodCallApi;
import sk.annotation.library.mapper.jam.processor.data.TypeWithVariableInfo;
import sk.annotation.library.mapper.jam.processor.data.keys.MethodConfigKey;
import sk.annotation.library.mapper.jam.processor.data.mapi.MethodApiFullSyntax;
import sk.annotation.library.mapper.jam.processor.sourcewriter.ImportsTypeDefinitions;
import sk.annotation.library.mapper.jam.processor.sourcewriter.SourceGeneratorContext;
import sk.annotation.library.mapper.jam.processor.utils.NameUtils;
import sk.annotation.library.mapper.jam.utils.MapperRunCtxData;
import sk.annotation.library.mapper.jam.utils.MapperRunCtxDataHolder;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
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
    public void registerImports(SourceGeneratorContext ctx, ImportsTypeDefinitions imports) {
        super.registerImports(ctx, imports);
        Constants.typeMapperRunCtxDataHolder.registerImports(ctx, imports);
    }

    public void analyzeAndGenerateDependMethods(ProcessingEnvironment processingEnv) {
        if (!requiredMethods.isEmpty()) return;
        if (methodApiFullSyntax.getReturnType() == null) return; // body will be empty

        ////////////////////////////////////////////////////////
        // 1)  Create MethodConfigKey
        methodConfigKey = new MethodConfigKey(ownerClassInfo.topMethodsRegistrator.registerTopMethod(method));
        MapperFieldConfig methodConfig = method.getAnnotation(MapperFieldConfig.class);
        if (methodConfig != null) {
            methodConfigKey.getConfigurations().add(methodConfig);
            methodConfigKey.setWithCustomConfig(true);
        }
        if (!ownerClassInfo.getClassAndPackageConfigurations().isEmpty()) {
            methodConfigKey.getConfigurations().addAll(ownerClassInfo.getClassAndPackageConfigurations());
        }

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
            MethodCallApi methodCallApi = findOrCreateOwnMethod(processingEnv, null, inputParams.get(i).getVariableType(), varRet.getVariableType());

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
    //	@Override
//	protected void generateBody(ProcessingEnvironment processingEnv, MethodConfigKey methodConfigKey, BodyGenerator body) {
//		/*
//		* ObjRet transf(Obj1 o1, Obj2 o2, Obj3 o3)  =>
//			BODY:
//
//			// Wrap methods and collect informations
//			return doTransform(
//					null,
//					// Init context
//					ctxData -> {
//						ctxData.putContextValue("ctx", ctx);
//						ctxData.putContextValue("newValue", ctx2);
//					},
//					() -> methodSimpleO1_implemented_bypass(i1, o1)
//			);
//		* */
//
//		body.add("\n// Wrap methods and collect informations");
//		body.add("\nreturn " + MapperUtil.class.getSimpleName() + ".doTransform(");
//
//		// register method context
//		body.add("\n\t\"" + StringEscapeUtils.escapeJava(methodConfigKey.getForTopMethod()) + "\",");
//
//		// register requiredContextValues
//		List<MethodParamInfo> subMethodParams = new LinkedList<>();
//		for (MethodParamInfo ctxParam : methodApiFullSyntax.getParams()) {
//			if (ctxParam.getHasContextKey()!=null) {
//				subMethodParams.add(ctxParam);
//			}
//		}
//		if (subMethodParams.isEmpty()) {
//			body.add("\n\tnull,");
//		}
//		else {
//			body.add("\n\t() -> {");
//			for (MethodParamInfo subMethodParam : subMethodParams) {
//				body.add("\n\t\t", subMethodParam.genSourceForPutContext(subMethodParam.getFieldName()), ");");
//			}
//			body.add("\t},");
//		}
//
//
//		// callMethods
//		MethodCallApi methodCallApi = ownerClassInfo.findMethodApiToCall(subMethodApiSyntax.getApiKey());
//		body.add("\n\t() -> ");
//		body.add(methodCallApi.genSourceForDelegatedCall("ctx", methodApiFullSyntax, "null"));
//		body.add(");");
//	}

    @Override
    protected void writeSourceCodeBody(SourceGeneratorContext ctx) {
		/*
		* ObjRet transf(Obj1 i1, Obj2 i2, ..., @Ret Obj3 ret)  =>
			BODY:

			* // Null values (checked in parent !!!)
			* if (i1 == null && i2 == null) return ret;
			*
			* // Is context ready
			* CtxData ctx = CtxUtil.data.get();
			* boolean manageCtx = ctx == null;
			* try {
			* 	if (manageCtx) {
			* 		ctx = new CtxData();
			* 		CtxUtil.data.save(ctx);
			* 	}
			*
			* 	if (ret == null) ret = new Obj3();
			* 	ret = methodSimpleO1_implemented_bypass(i1, ret);
			* 	ret = methodSimpleO1_implemented_bypass(i2, ret);
			* 	...
			*
			* 	return ret;
			* }
			* finish {
			* 	if (manageCtx) CtxUtil.data.remove();
			* }
		* */


        // Init context ...
        List<TypeWithVariableInfo> bodyVariableNames = new LinkedList<>();
        for (TypeWithVariableInfo param : methodApiFullSyntax.getParams()) {
            usedNames.add(param.getVariableName());
            bodyVariableNames.add(param);
        }

        if (methodConfigKey!=null) {
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
            ctx.pw.print("\n\t" + MapperRunCtxDataHolder.class.getSimpleName() + ".data.set("+varCtxVariable.getVariableName()+");");
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


//
//		/*
//		* ObjRet transf(Obj1 o1, Obj2 o2, Obj3 o3)  =>
//			BODY:
//
//			// Wrap methods and collect informations
//			return doTransform(
//					null,
//					// Init context
//					ctxData -> {
//						ctxData.putContextValue("ctx", ctx);
//						ctxData.putContextValue("newValue", ctx2);
//					},
//					() -> methodSimpleO1_implemented_bypass(i1, o1)
//			);
//		* */
//
//		ctx.pw.print("\nreturn " + MapperUtil.class.getSimpleName() + ".doTransform(");
//
//		// register method context
//		ctx.pw.print("\n\t" + methodConfigKey.getForTopMethod() + "  /* method configuration id */,");
//
//		// register requiredContextValues
//		List<TypeWithVariableInfo> ctxParams = new LinkedList<>();
//		for (TypeWithVariableInfo ctxParam : methodApiFullSyntax.getParams()) {
//			if (ctxParam.getHasContextKey()!=null) {
//				ctxParams.add(ctxParam);
//			}
//		}
//		if (ctxParams.isEmpty()) {
//			ctx.pw.print("\n\tnull,");
//		}
//		else {
//			ctx.pw.print("\n\t() -> {");
//			for (TypeWithVariableInfo subMethodParam : ctxParams) {
//				ctx.pw.print("\n\t\t");
//				subMethodParam.genSourceForPutContext(ctx, subMethodParam.getVariableName());
//				ctx.pw.print(");");
//			}
//			ctx.pw.print("\t},");
//		}
//
//
//		// callMethods
//		ctx.pw.print("\n\t() -> {");
//		ctx.pw.levelSpaceUp();
//		ctx.pw.levelSpaceUp();
//		if (!methodApiFullSyntax.isReturnLastParam()) {
//			ctx.pw.print("\n");
//			varRet.writeSourceCode(ctx, true, false);
//			ctx.pw.print(" = null;");
//		}
//
//		int i=0;
//		List<TypeWithVariableInfo> inputParams = methodApiFullSyntax.getRequiredParams();
//		for (MethodCallApi methodCallApi : requiredMethods) {
//			// 3a) Create methodApiKey
//			List<TypeWithVariableInfo> paramsForApi = new LinkedList<>();
//			paramsForApi.add(inputParams.get(i++));
//			paramsForApi.add(varRet);
//			ctx.pw.print("\n");
//			methodCallApi.genSourceForCallWithVariableParams(ctx, paramsForApi, methodApiFullSyntax.getParams());
//			ctx.pw.print(";");
//		}
//
//		ctx.pw.print("\nreturn ");
//		ctx.pw.print(varRet.getVariableName());
//		ctx.pw.print(";");
//		ctx.pw.levelSpaceDown();
//		ctx.pw.print("\n}");
//		ctx.pw.levelSpaceDown();
//		ctx.pw.print("\n);");
    }//*/
}
