package sk.annotation.library.jam.processor.data;

import sk.annotation.library.jam.processor.data.generator.method.AbstractMethodSourceInfo;
import sk.annotation.library.jam.processor.data.generator.row.AbstractRowValueTransformator;
import sk.annotation.library.jam.processor.data.mapi.MethodApiFullSyntax;
import sk.annotation.library.jam.processor.sourcewriter.SourceGeneratorContext;

import javax.lang.model.type.TypeMirror;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class MethodCallApi {

	private String pathToSyntax = null;
	private MethodApiFullSyntax methodSyntax = null;
	private AbstractMethodSourceInfo outGeneratedMethod = null;

	private AbstractRowValueTransformator rowFieldGenerator = null;
	private TypeMirror sourceType;
	private TypeMirror destinationType;

	public static MethodCallApi createFrom(TypeMirror sourceType, TypeMirror destinationType, AbstractRowValueTransformator rowFieldGenerator) {
		MethodCallApi ret = new MethodCallApi();
		ret.setRowFieldGenerator(rowFieldGenerator);
		ret.setSourceType(sourceType);
		ret.setDestinationType(destinationType);
		return ret;
	}
	public static MethodCallApi createFrom(String path, MethodApiFullSyntax methodSyntax, AbstractMethodSourceInfo generatedMethodSource) {
		if (path == null) path = "";
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


	static public TypeWithVariableInfo ctx_findVariable(TypeWithVariableInfo forVariable, List<TypeWithVariableInfo> values) {
		if (values == null) return null;
		for (TypeWithVariableInfo value : values) {
			if (ctx_equals(forVariable, value)) return value;
		}
		return null;
	}
	static public boolean ctx_equals(TypeWithVariableInfo value1, TypeWithVariableInfo value2) {
		if (value1 == null || value2 == null) return false;
		if (value1.getHasContextKey() == null || value2.getHasContextKey() == null) return false;
		if (!Objects.equals(value1.getHasContextKey(), value2.getHasContextKey())) return false;
		// should be check type
		return true;
	}

	public void genSourceForCallWithVariableParams(SourceGeneratorContext ctx, List<TypeWithVariableInfo> methodParams, List<TypeWithVariableInfo> otherVariables, AbstractMethodSourceInfo method) {
		List<String> newMethodParams = new LinkedList<>();
		List<TypeWithVariableInfo> newOtherVariables = new LinkedList<>();
		if (otherVariables!=null) newOtherVariables.addAll(otherVariables);
		if (methodParams!=null) {
			for (TypeWithVariableInfo methodParam : methodParams) {
				if (methodParam.getHasContextKey()==null) {
					newMethodParams.add(methodParam.getVariableName());
				}
				else {
					newOtherVariables.add(methodParam);
				}
			}
		}
		genSourceForCallWithStringParam(ctx, newMethodParams, newOtherVariables, method);
	}
	public void genSourceForCallWithStringParam(SourceGeneratorContext ctx, List<String> methodParams, 
			List<TypeWithVariableInfo> otherVariables, AbstractMethodSourceInfo method) {
		
		if (rowFieldGenerator != null) {
			ctx.pw.print(rowFieldGenerator.generateRowTransform(ctx, sourceType, destinationType, methodParams.get(0)));
			return;
		}

		ctx.pw.print(pathToSyntax);
		ctx.pw.print(methodSyntax.getName());
		ctx.pw.print("(");
		
		int i=0;
		boolean addSeparator = false;
		for (TypeWithVariableInfo param : methodSyntax.getParams()) {
			if (param.isMarkedAsReturn() && !methodSyntax.isGenerateReturnParamRequired()) {
				// kedze moze bezat bez returnType na vstupe, generujeme po svojom
				continue;
			}

			if (addSeparator) ctx.pw.print(", ");
			addSeparator = true;

			if (param.getHasContextKey()!=null) {
				TypeWithVariableInfo ctxParamName = ctx_findVariable(param, otherVariables);
				if (ctxParamName != null) {
					ctx.pw.print(ctxParamName.getVariableName());
				}
				else {
					param.genSourceForLoadContext(ctx, method, param.getVariableType());
				}
				continue;
			}

			if (i < methodParams.size()) {
				ctx.pw.print(methodParams.get(i++));
			}
			else {
				ctx.pw.print("null");
			}
		}

		ctx.pw.print(")");
	}

//	@Deprecated
//	public void genSourceForDelegatedCall(SourceGeneratorContext ctx, List<TypeWithVariableInfo> allParams, MethodApiFullSyntax parentMethodsFullSyntax) {
//		List<String> params = new LinkedList<>();
//		Map<String, TypeWithVariableInfo> ctxParam = new HashMap<>();
//
//		for (TypeWithVariableInfo param : allParams) {
//			if (param.getHasContextKey()!=null) {
//				ctxParam.put(param.getHasContextKey(), param);
//				continue;
//			}
//			params.add(param.getVariableName());
//		}
//
//		if (parentMethodsFullSyntax!=null && parentMethodsFullSyntax.getParams()!=null) {
//			for (TypeWithVariableInfo param : parentMethodsFullSyntax.getParams()) {
//				if (param.getHasContextKey()!=null) {
//					ctxParam.put(param.getHasContextKey(), param);
//				}
//			}
//		}
//
//		genSourceForCall(ctx, params, ctxParam);
//	}
//
//
//	@Deprecated
//	public void genSourceForCall(SourceGeneratorContext ctx, List<String> params, Map<String, TypeWithVariableInfo> ctxParam) {
//		ctx.pw.print(pathToSyntax);
//		ctx.pw.print(methodSyntax.getName());
//		ctx.pw.print("(");
//
//		int i=0;
//		boolean addSeparator = false;
//		for (TypeWithVariableInfo param : methodSyntax.getParams()) {
//			if (addSeparator) ctx.pw.print(", ");
//			addSeparator = true;
//
//			if (param.getHasContextKey()!=null) {
//				TypeWithVariableInfo ctxParamName = ctxParam.get(param.getHasContextKey());
//				if (ctxParamName != null) {
//					ctx.pw.print(ctxParamName.getVariableName());
//				}
//				else {
//					param.genSourceForLoadContext(ctx);
//				}
//				continue;
//			}
//
//			if (i < params.size()) {
//				ctx.pw.print(params.get(i++));
//			}
//			else {
//				ctx.pw.print("null");
//			}
//		}
//
//		ctx.pw.print(")");
//	}


	public String getPathToSyntax() {
		return pathToSyntax;
	}

	public void setPathToSyntax(String pathToSyntax) {
		this.pathToSyntax = pathToSyntax;
	}

	public MethodApiFullSyntax getMethodSyntax() {
		return methodSyntax;
	}

	public void setMethodSyntax(MethodApiFullSyntax methodSyntax) {
		this.methodSyntax = methodSyntax;
	}

	public AbstractMethodSourceInfo getOutGeneratedMethod() {
		return outGeneratedMethod;
	}

	public void setOutGeneratedMethod(AbstractMethodSourceInfo outGeneratedMethod) {
		this.outGeneratedMethod = outGeneratedMethod;
	}

	public AbstractRowValueTransformator getRowFieldGenerator() {
		return rowFieldGenerator;
	}

	public void setRowFieldGenerator(AbstractRowValueTransformator rowFieldGenerator) {
		this.rowFieldGenerator = rowFieldGenerator;
	}

	public TypeMirror getSourceType() {
		return sourceType;
	}

	public void setSourceType(TypeMirror sourceType) {
		this.sourceType = sourceType;
	}

	public TypeMirror getDestinationType() {
		return destinationType;
	}

	public void setDestinationType(TypeMirror destinationType) {
		this.destinationType = destinationType;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof MethodCallApi)) return false;
		MethodCallApi that = (MethodCallApi) o;
		return Objects.equals(pathToSyntax, that.pathToSyntax) && Objects.equals(methodSyntax, that.methodSyntax) && Objects.equals(outGeneratedMethod, that.outGeneratedMethod) && Objects.equals(rowFieldGenerator, that.rowFieldGenerator) && Objects.equals(sourceType, that.sourceType) && Objects.equals(destinationType, that.destinationType);
	}

	@Override
	public int hashCode() {
		return Objects.hash(pathToSyntax, methodSyntax, outGeneratedMethod, rowFieldGenerator, sourceType, destinationType);
	}
}
