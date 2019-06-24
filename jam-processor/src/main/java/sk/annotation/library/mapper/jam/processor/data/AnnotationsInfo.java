package sk.annotation.library.mapper.jam.processor.data;

import sk.annotation.library.mapper.jam.processor.sourcewriter.ImportsTypeDefinitions;
import sk.annotation.library.mapper.jam.processor.sourcewriter.SourceGenerator;
import sk.annotation.library.mapper.jam.processor.sourcewriter.SourceGeneratorContext;
import sk.annotation.library.mapper.jam.processor.sourcewriter.SourceRegisterImports;

import java.util.LinkedHashMap;
import java.util.Map;

public class AnnotationsInfo implements SourceGenerator, SourceRegisterImports {
	final private Map<TypeInfo, AnnotationValues> annotationData = new LinkedHashMap<>();
	private boolean inline = false;

	public AnnotationValues getOrAddAnnotation(TypeInfo annotationType) {
		return annotationData.computeIfAbsent(annotationType, a -> new AnnotationValues());
	}

	public AnnotationsInfo withAnnotation(TypeInfo annotationType) {
		getOrAddAnnotation(annotationType);
		return this;
	}


	public AnnotationsInfo mergeValues(AnnotationsInfo annotationsInfo) {
		if (annotationsInfo == null) return this;

		for (Map.Entry<TypeInfo, AnnotationValues> e : annotationsInfo.annotationData.entrySet()) {
			AnnotationValues valuesSaved = getOrAddAnnotation(e.getKey());
			valuesSaved.mergeValues(e.getValue());
		}
		return this;
	}

	public void setInline(boolean inline) {
		this.inline = inline;
	}

	@Override
	public void writeSourceCode(SourceGeneratorContext ctx) {
		if (annotationData.isEmpty()) return;
		boolean addSpace = false;
		for (Map.Entry<TypeInfo, AnnotationValues> e : annotationData.entrySet()) {
			if (!inline) ctx.pw.print("\n");
			else {
				if (addSpace) ctx.pw.print(" ");
				addSpace = true;
			}
			ctx.pw.print("@");
			e.getKey().writeSourceCode(ctx);

			// Values ...
			e.getValue().writeSourceCode(ctx);
			if (inline) ctx.pw.print(" ");
		}
	}

	@Override
	public void registerImports(SourceGeneratorContext ctx, ImportsTypeDefinitions imports) {
		annotationData.keySet().forEach(a -> a.registerImports(ctx,imports));
	}
}
