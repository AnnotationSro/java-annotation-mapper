package sk.annotation.library.mapper.fast.processor.data;

import lombok.Getter;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import sk.annotation.library.mapper.fast.annotations.Context;
import sk.annotation.library.mapper.fast.annotations.Return;
import sk.annotation.library.mapper.fast.processor.sourcewriter.SourceGeneratorContext;
import sk.annotation.library.mapper.fast.processor.utils.AnnotationConstants;
import sk.annotation.library.mapper.fast.processor.utils.MsgConstants;
import sk.annotation.library.mapper.fast.processor.utils.TypeUtils;

import javax.lang.model.element.VariableElement;

@Getter
public class MethodParamInfo {
	private TypeWithVariableInfo variable;
	private String hasContextKey; // if contains any @Context("name")
	private boolean markedAsReturn; // if contains any @Return


	static public MethodParamInfo analyze(VariableElement variableElement) {
		TypeWithVariableInfo variable = new TypeWithVariableInfo(variableElement.getSimpleName().toString(), new TypeInfo(TypeUtils.findType(variableElement)));
		String hasContextKey = null;

		Context ctx = variableElement.getAnnotation(Context.class);
		if (ctx!=null) {
			hasContextKey = ctx.value();
		}
		boolean markedAsReturn = variableElement.getAnnotation(Return.class)!=null;

		return new MethodParamInfo(variable,hasContextKey,markedAsReturn);
	}

	public MethodParamInfo(TypeWithVariableInfo variable, String hasContextKey, boolean markedAsReturn) {
		hasContextKey = StringUtils.trimToNull(hasContextKey);
		if (markedAsReturn && hasContextKey!=null) {
			throw new IllegalStateException(MsgConstants.errorMethodParamReturnAndContext);
		}

		this.variable = new TypeWithVariableInfo(variable.getName(), variable.getType());
		this.hasContextKey = hasContextKey;
		this.markedAsReturn = markedAsReturn;

		AnnotationsInfo annotationsInfo = new AnnotationsInfo();
		if (markedAsReturn) {
			annotationsInfo.getOrAddAnnotation(AnnotationConstants.annotationReturn);
		}
		if (this.hasContextKey!=null) {
			annotationsInfo.getOrAddAnnotation(AnnotationConstants.annotationContext).withStringValue(hasContextKey);
		}
		this.variable.withAnnotations(annotationsInfo);
	}

	public void genSourceForPutContext(SourceGeneratorContext ctx, String sourceValue) {
		ctx.pw.print("MapperUtil.putContextValue(\"");
		ctx.pw.print(StringEscapeUtils.escapeJava(hasContextKey));
		ctx.pw.print("\", ");
		ctx.pw.print(sourceValue);
		ctx.pw.print(")");
	}
	public void genSourceForLoadContext(SourceGeneratorContext ctx) {
		// MapperUtil.getContextValue("[hasContextKey]")
		ctx.pw.print("MapperUtil.getContextValue(\"");
		ctx.pw.print(StringEscapeUtils.escapeJava(hasContextKey));
		ctx.pw.print("\")");
	}
}
