package sk.annotation.library.mapper.jam.processor.data;

import lombok.Getter;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import sk.annotation.library.mapper.jam.annotations.Context;
import sk.annotation.library.mapper.jam.annotations.Return;
import sk.annotation.library.mapper.jam.processor.Constants;
import sk.annotation.library.mapper.jam.processor.data.methodgenerator.AbstractMethodSourceInfo;
import sk.annotation.library.mapper.jam.processor.sourcewriter.ImportsTypeDefinitions;
import sk.annotation.library.mapper.jam.processor.sourcewriter.SourceGenerator;
import sk.annotation.library.mapper.jam.processor.sourcewriter.SourceGeneratorContext;
import sk.annotation.library.mapper.jam.processor.sourcewriter.SourceRegisterImports;
import sk.annotation.library.mapper.jam.processor.utils.MsgConstants;
import sk.annotation.library.mapper.jam.processor.utils.TypeUtils;
import sk.annotation.library.mapper.jam.utils.MapperRunCtxDataHolder;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import java.util.LinkedHashSet;
import java.util.Set;

public class TypeWithVariableInfo implements SourceGenerator, SourceRegisterImports {


	final private String name;
	final private TypeInfo type;
	final AnnotationsInfo annotations = new AnnotationsInfo();
	final Set<Modifier> modifiers = new LinkedHashSet<>();
	boolean inlineMode = true;

	@Getter
	final private String hasContextKey; // if contains any @Context("name")
	@Getter
	final private boolean markedAsReturn; // if contains any @Return


	public TypeWithVariableInfo withAnnotations(AnnotationsInfo injections) {
		this.annotations.mergeValues(injections);
		return this;
	}

	public TypeWithVariableInfo(TypeInfo type) {
		this(generateDefaultNameFrom(type), type);
	}
	public TypeWithVariableInfo(String name, TypeInfo type) {
		this(name, type, null, false);
	}
	public TypeWithVariableInfo(TypeInfo type, String hasContextKey, boolean markedAsReturn) {
		this(generateDefaultNameFrom(type), type, hasContextKey, markedAsReturn);
	}
	public TypeWithVariableInfo(String name, TypeWithVariableInfo copyFrom) {
		this(name == null ? copyFrom.getVariableName() : name, copyFrom.getVariableType(), copyFrom.getHasContextKey(), copyFrom.isMarkedAsReturn());
	}
	public TypeWithVariableInfo(String name, TypeInfo type, String hasContextKey, boolean markedAsReturn) {
		this.name = name;
		this.type = type;



		// context values
		hasContextKey = StringUtils.trimToNull(hasContextKey);
		if (markedAsReturn && hasContextKey!=null) {
			throw new IllegalStateException(MsgConstants.errorMethodParamReturnAndContext);
		}

		this.hasContextKey = hasContextKey;
		this.markedAsReturn = markedAsReturn;

		AnnotationsInfo annotationsInfo = new AnnotationsInfo();
		if (markedAsReturn) {
			annotationsInfo.getOrAddAnnotation(Constants.annotationReturn);
		}
		if (this.hasContextKey!=null) {
			annotationsInfo.getOrAddAnnotation(Constants.annotationContext).withStringValue(hasContextKey);
		}
		this.withAnnotations(annotationsInfo);
	}

	static public TypeWithVariableInfo analyze(VariableElement variableElement) {
		String hasContextKey = null;

		Context ctx = variableElement.getAnnotation(Context.class);
		if (ctx!=null) {
			hasContextKey = ctx.value();
		}
		boolean markedAsReturn = variableElement.getAnnotation(Return.class)!=null;

		return new TypeWithVariableInfo(variableElement.getSimpleName().toString(), new TypeInfo(TypeUtils.findType(variableElement)),hasContextKey,markedAsReturn);
	}


	private static String generateDefaultNameFrom(TypeInfo type) {
		StringBuilder sb = new StringBuilder();
		sb.append("tmp"+System.currentTimeMillis());
//		if (!type.getParameterTypes().isEmpty()) {
//			sb.append("_");
//			sb.append(StringUtils.replace(""+type.hashCode(), "-","_"));
//		}
		return sb.toString();
	}

	public String getVariableName() {
		return name;
	}

	public TypeInfo getVariableType() {
		return type;
	}

	@Override
	public void writeSourceCode(SourceGeneratorContext ctx) {
		writeSourceCode(ctx, this.inlineMode, true);
	}
	public void writeSourceCode(SourceGeneratorContext ctx, boolean inlineMode, boolean writeAnnotation) {
		if (writeAnnotation) {
			annotations.setInline(inlineMode);
			annotations.writeSourceCode(ctx);
			annotations.setInline(this.inlineMode);
			if (!inlineMode) {
				ctx.pw.print("\n");
				if (modifiers.contains(Modifier.PROTECTED))
					ctx.pw.print("protected ");
			}
		}
		type.writeSourceCode(ctx);
		ctx.pw.print(" ");
		ctx.pw.print(name);
	}

	@Override
	public void registerImports(SourceGeneratorContext ctx, ImportsTypeDefinitions imports) {
		annotations.registerImports(ctx, imports);
		type.registerImports(ctx, imports);
	}

	public void genSourceForPutContext(SourceGeneratorContext ctx, String sourceValue, AbstractMethodSourceInfo method) {
		if (method.getOwnerClassInfo().getFeatures().isDisabledToUseMapperRunCtxData()) return;

		// contextValueIsAvailable
		if (method.getVarCtxVariable()!=null) {
			ctx.pw.print(method.getVarCtxVariable().getVariableName());
			ctx.pw.print(".putContextValue(\"");
			ctx.pw.print(StringEscapeUtils.escapeJava(hasContextKey));
			ctx.pw.print("\", ");
			ctx.pw.print(sourceValue);
			ctx.pw.print(")");
			return;
		}

		if (!method.getOwnerClassInfo().getFeatures().isDisabled_SHARED_THREAD_CONTEXT()) {
			ctx.pw.print(MapperRunCtxDataHolder.class.getSimpleName());
			ctx.pw.print(".data.get()");
			ctx.pw.print(".putContextValue(\"");
			ctx.pw.print(StringEscapeUtils.escapeJava(hasContextKey));
			ctx.pw.print("\", ");
			ctx.pw.print(sourceValue);
			ctx.pw.print(")");
			return;
		}
	}

	public void genSourceForLoadContext(SourceGeneratorContext ctx, AbstractMethodSourceInfo method, TypeInfo typeOfParam) {
		if (method.getOwnerClassInfo().getFeatures().isDisabledToUseMapperRunCtxData()) {
			String value = TypeUtils.createNullValue(typeOfParam.getType(ctx.processingEnv));
			ctx.pw.print(value);
			return;
		}

		// contextValueIsAvailable
		if (method.getVarCtxVariable()!=null) {
			ctx.pw.print(method.getVarCtxVariable().getVariableName());
			ctx.pw.print(".getContextValue(\"");
			ctx.pw.print(StringEscapeUtils.escapeJava(hasContextKey));
			ctx.pw.print("\")");
			return;
		}

		if (!method.getOwnerClassInfo().getFeatures().isDisabled_SHARED_THREAD_CONTEXT()) {
			ctx.pw.print(MapperRunCtxDataHolder.class.getSimpleName());
			ctx.pw.print(".data.get()");
			ctx.pw.print(".getContextValue(\"");
			ctx.pw.print(StringEscapeUtils.escapeJava(hasContextKey));
			ctx.pw.print("\")");
			return;
		}

		// default value
		String value = TypeUtils.createNullValue(typeOfParam.getType(ctx.processingEnv));
		ctx.pw.print(value);
	}
}
