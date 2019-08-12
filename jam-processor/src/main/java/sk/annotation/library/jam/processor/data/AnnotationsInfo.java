package sk.annotation.library.jam.processor.data;

import sk.annotation.library.jam.processor.sourcewriter.ImportsTypeDefinitions;
import sk.annotation.library.jam.processor.sourcewriter.SourceGenerator;
import sk.annotation.library.jam.processor.sourcewriter.SourceGeneratorContext;
import sk.annotation.library.jam.processor.sourcewriter.SourceRegisterImports;

import javax.annotation.processing.ProcessingEnvironment;
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
	public boolean writeSourceCode(SourceGeneratorContext ctx) {
		if (annotationData.isEmpty()) return false;
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

		return true;
	}

	@Override
	public void registerImports(ProcessingEnvironment processingEnv, ImportsTypeDefinitions imports) {
		annotationData.keySet().forEach(a -> a.registerImports(processingEnv,imports));
	}
}
