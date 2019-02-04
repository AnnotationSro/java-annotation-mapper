package sk.annotation.library.mapper.fast.processor.data;

import sk.annotation.library.mapper.fast.processor.sourcewriter.ImportsTypeDefinitions;
import sk.annotation.library.mapper.fast.processor.sourcewriter.SourceGenerator;
import sk.annotation.library.mapper.fast.processor.sourcewriter.SourceGeneratorContext;
import sk.annotation.library.mapper.fast.processor.sourcewriter.SourceRegisterImports;

import javax.lang.model.element.Modifier;
import java.util.LinkedHashSet;
import java.util.Set;

public class TypeWithVariableInfo implements SourceGenerator, SourceRegisterImports {
	final private String name;
	final private TypeInfo type;
	final AnnotationsInfo annotations = new AnnotationsInfo();
	final Set<Modifier> modifiers = new LinkedHashSet<>();
	boolean inlineMode = true;

	public TypeWithVariableInfo withAnnotations(AnnotationsInfo injections) {
		this.annotations.mergeValues(injections);
		return this;
	}

	public TypeWithVariableInfo(TypeInfo type) {
		this(generateDefaultNameFrom(type), type);
	}
	public TypeWithVariableInfo(String name, TypeInfo type) {
		this.name = name;
		this.type = type;
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

	public String getName() {
		return name;
	}

	public TypeInfo getType() {
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
}
