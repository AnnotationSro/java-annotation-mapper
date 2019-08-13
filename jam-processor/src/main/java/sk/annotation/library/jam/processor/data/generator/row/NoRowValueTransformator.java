package sk.annotation.library.jam.processor.data.generator.row;

import sk.annotation.library.jam.processor.data.MapperClassInfo;
import sk.annotation.library.jam.processor.utils.annotations.AnnotationValueUtils;
import sk.annotation.library.jam.processor.sourcewriter.SourceGeneratorContext;
import sk.annotation.library.jam.processor.utils.TypeUtils;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.type.TypeMirror;

public class NoRowValueTransformator extends AbstractRowValueTransformator {
	public static final NoRowValueTransformator instance = new NoRowValueTransformator();


	@Override
	boolean accept(ProcessingEnvironment processingEnv, MapperClassInfo ownerClassInfo, TypeMirror source, TypeMirror destination) {
		if (!processingEnv.getTypeUtils().isSameType(source, destination)) return false;

		if (TypeUtils.isKnownImmutableType(processingEnv, source)) return true;

		// Check configuration immutable type
		if (AnnotationValueUtils.isConfiguredAsImmutableType(processingEnv, ownerClassInfo, destination)) return true;

		return false;
	}

	@Override
	public String generateRowTransform(SourceGeneratorContext ctx, TypeMirror source, TypeMirror destination, String varValue) {
		return varValue;
	}
}
