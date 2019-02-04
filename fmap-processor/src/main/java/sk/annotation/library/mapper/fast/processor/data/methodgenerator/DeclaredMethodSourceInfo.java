package sk.annotation.library.mapper.fast.processor.data.methodgenerator;

import sk.annotation.library.mapper.fast.annotations.MapperFieldConfig;
import sk.annotation.library.mapper.fast.processor.data.*;
import sk.annotation.library.mapper.fast.processor.data.keys.MethodConfigKey;
import sk.annotation.library.mapper.fast.processor.data.mapi.MethodApiFullSyntax;
import sk.annotation.library.mapper.fast.processor.sourcewriter.ImportsTypeDefinitions;
import sk.annotation.library.mapper.fast.processor.sourcewriter.SourceGeneratorContext;
import sk.annotation.library.mapper.fast.processor.utils.AnnotationConstants;
import sk.annotation.library.mapper.fast.processor.utils.NameUtils;
import sk.annotation.library.mapper.fast.utils.context.MapperUtil;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import java.util.LinkedList;
import java.util.List;

public class DeclaredMethodSourceInfo extends AbstractMethodSourceInfo {

	protected List<MethodCallApi> requiredMethods = new LinkedList<>();
	protected ExecutableElement method;
	protected MethodConfigKey methodConfigKey;

	static private final TypeInfo typeMapperUtil = new TypeInfo(MapperUtil.class);

	protected MethodParamInfo lastParameter = null;

	public DeclaredMethodSourceInfo(MapperClassInfo ownerClassInfo, MethodApiFullSyntax methodApiParams, ExecutableElement method) {
		super(ownerClassInfo, methodApiParams);
		this.method = method;
		this.methodApiFullSyntax.getModifiers().addAll(method.getModifiers());
		super.methodApiFullSyntax.getAnnotations().getOrAddAnnotation(AnnotationConstants.annotationOverride);
	}

	@Override
	public void registerImports(SourceGeneratorContext ctx, ImportsTypeDefinitions imports) {
		super.registerImports(ctx, imports);
		typeMapperUtil.registerImports(ctx, imports);
	}

	public void analyzeAndGenerateDependMethods(ProcessingEnvironment processingEnv) {
		if (!requiredMethods.isEmpty()) return;
		if (methodApiFullSyntax.getReturnType()==null) return; // bude prazdne body

		////////////////////////////////////////////////////////
		// 1)  Vytvorime MethodConfigKey
		methodConfigKey = new MethodConfigKey(method.toString());
		MapperFieldConfig methodConfig = method.getAnnotation(MapperFieldConfig.class);
		if (methodConfig!=null) {
			methodConfigKey.getConfigurations().add(methodConfig);
			methodConfigKey.setWithCustomConfig(true);
		}
		if (!ownerClassInfo.getClassAndPackageConfigurations().isEmpty()) {
			methodConfigKey.getConfigurations().addAll(ownerClassInfo.getClassAndPackageConfigurations());
		}


		////////////////////////////////////////////////////////
		// 2)  Analyzujeme nevyhnutne fieldy + return Type
		List<MethodParamInfo> inputParams = methodApiFullSyntax.getRequiredParams();
		lastParameter = null;
		int numInputParams = inputParams.size();
		if (methodApiFullSyntax.isReturnLastParam()) {
			numInputParams--;
			lastParameter = inputParams.get(numInputParams);
		}
		else {
			String variableName = NameUtils.findBestName(methodApiFullSyntax.getParams(), "ret");
			TypeWithVariableInfo variable = new TypeWithVariableInfo(variableName, methodApiFullSyntax.getReturnType());
			lastParameter = new MethodParamInfo(variable, null, true);
		}

		////////////////////////////////////////////////////////
		// 3) Vytvorime potrebne transformacne methody pre analyzovane vstupne parametre !!!
		for (int i=0; i<numInputParams; i++) {

			// 3a) Vytvorime methodApiKey
			MethodCallApi methodCallApi = findOrCreateOwnMethod(processingEnv, null, inputParams.get(i).getVariable().getType(), lastParameter.getVariable().getType());

			// Ak stale neexistuje metoda, uz je zle !!!
			requiredMethods.add(methodCallApi);

			// Ak metoda je nami generovana, spustime analyzu s vlastnymi nastaveniami !!!
			if (methodCallApi.getOutGeneratedMethod()!=null) {
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
//				body.add("\n\t\t", subMethodParam.genSourceForPutContext(subMethodParam.getVariable().getFieldName()), ");");
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
		* ObjRet transf(Obj1 o1, Obj2 o2, Obj3 o3)  =>
			BODY:

			// Wrap methods and collect informations
			return doTransform(
					null,
					// Init context
					ctxData -> {
						ctxData.putContextValue("ctx", ctx);
						ctxData.putContextValue("newValue", ctx2);
					},
					() -> methodSimpleO1_implemented_bypass(i1, o1)
			);
		* */

		ctx.pw.print("\nreturn " + MapperUtil.class.getSimpleName() + ".doTransform(");

		// register method context
		ctx.pw.print("\n\t" + methodConfigKey.getForTopMethod().hashCode() + "  /* method configuration id */,");

		// register requiredContextValues
		List<MethodParamInfo> ctxParams = new LinkedList<>();
		for (MethodParamInfo ctxParam : methodApiFullSyntax.getParams()) {
			if (ctxParam.getHasContextKey()!=null) {
				ctxParams.add(ctxParam);
			}
		}
		if (ctxParams.isEmpty()) {
			ctx.pw.print("\n\tnull,");
		}
		else {
			ctx.pw.print("\n\t() -> {");
			for (MethodParamInfo subMethodParam : ctxParams) {
				ctx.pw.print("\n\t\t");
				subMethodParam.genSourceForPutContext(ctx, subMethodParam.getVariable().getName());
				ctx.pw.print(");");
			}
			ctx.pw.print("\t},");
		}


		// callMethods
		ctx.pw.print("\n\t() -> {");
		ctx.pw.levelSpaceUp();
		ctx.pw.levelSpaceUp();
		if (!methodApiFullSyntax.isReturnLastParam()) {
			ctx.pw.print("\n");
			lastParameter.getVariable().writeSourceCode(ctx, true, false);
			ctx.pw.print(" = null;");
		}

		String retParamName = lastParameter.getVariable().getName();
		int i=0;
		List<MethodParamInfo> inputParams = methodApiFullSyntax.getRequiredParams();
		for (MethodCallApi methodCallApi : requiredMethods) {
			// 3a) Vytvorime methodApiKey
			List<MethodParamInfo> paramsForApi = new LinkedList<>();
			paramsForApi.add(inputParams.get(i++));
			paramsForApi.add(lastParameter);
			ctx.pw.print("\n");
			methodCallApi.genSourceForDelegatedCall(ctx, paramsForApi, methodApiFullSyntax);
			ctx.pw.print(";");
		}

		ctx.pw.print("\nreturn ");
		ctx.pw.print(lastParameter.getVariable().getName());
		ctx.pw.print(";");
		ctx.pw.levelSpaceDown();
		ctx.pw.print("\n}");
		ctx.pw.levelSpaceDown();
		ctx.pw.print("\n);");
	}//*/
}
