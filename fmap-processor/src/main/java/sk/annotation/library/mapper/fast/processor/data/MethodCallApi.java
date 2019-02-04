package sk.annotation.library.mapper.fast.processor.data;

import lombok.Data;
import sk.annotation.library.mapper.fast.processor.data.mapi.MethodApiFullSyntax;
import sk.annotation.library.mapper.fast.processor.data.methodgenerator.AbstractMethodSourceInfo;
import sk.annotation.library.mapper.fast.processor.sourcewriter.SourceGeneratorContext;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Data
public class MethodCallApi {

	private String pathToSyntax;
	private MethodApiFullSyntax methodSyntax;
	private AbstractMethodSourceInfo outGeneratedMethod;

	public static MethodCallApi createFrom(String path, MethodApiFullSyntax methodSyntax, AbstractMethodSourceInfo generatedMethodSource) {
		MethodCallApi ret = new MethodCallApi();
		ret.setPathToSyntax(path);
		ret.setMethodSyntax(methodSyntax);
		ret.setOutGeneratedMethod(generatedMethodSource);
		return ret;
	}
	public static MethodCallApi createFrom(AbstractMethodSourceInfo source) {
		MethodCallApi ret = new MethodCallApi();
		ret.setOutGeneratedMethod(source);
		ret.setPathToSyntax("");
		ret.setMethodSyntax(source.getMethodApiFullSyntax());
		return ret;
	}


	public void genSourceForDelegatedCall(SourceGeneratorContext ctx, List<MethodParamInfo> allParams, MethodApiFullSyntax parentMethodsFullSyntax) {
		List<String> params = new LinkedList<>();
		Map<String, MethodParamInfo> ctxParam = new HashMap<>();

		for (MethodParamInfo param : allParams) {
			if (param.getHasContextKey()!=null) {
				ctxParam.put(param.getHasContextKey(), param);
				continue;
			}
			params.add(param.getVariable().getName());
		}

		if (parentMethodsFullSyntax!=null && parentMethodsFullSyntax.getParams()!=null) {
			for (MethodParamInfo param : parentMethodsFullSyntax.getParams()) {
				if (param.getHasContextKey()!=null) {
					ctxParam.put(param.getHasContextKey(), param);
				}
			}
		}

		genSourceForCall(ctx, params, ctxParam);
	}
	public void genSourceForCall(SourceGeneratorContext ctx, List<String> params, Map<String, MethodParamInfo> ctxParam) {
		ctx.pw.print(pathToSyntax);
		ctx.pw.print(methodSyntax.getName());
		ctx.pw.print("(");

		int i=0;
		boolean addSeparator = false;
		for (MethodParamInfo param : methodSyntax.getParams()) {
			if (addSeparator) ctx.pw.print(", ");
			addSeparator = true;

			if (param.getHasContextKey()!=null) {
				MethodParamInfo ctxParamName = ctxParam.get(param.getHasContextKey());
				if (ctxParamName != null) {
					ctx.pw.print(ctxParamName.getVariable().getName());
				}
				else {
					param.genSourceForLoadContext(ctx);
				}
				continue;
			}

			if (i < params.size()) {
				ctx.pw.print(params.get(i++));
			}
			else {
				ctx.pw.print("null");
			}
		}

		ctx.pw.print(")");
	}
}
