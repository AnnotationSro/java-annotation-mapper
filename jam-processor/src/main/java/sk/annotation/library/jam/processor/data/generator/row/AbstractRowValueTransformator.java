package sk.annotation.library.jam.processor.data.generator.row;

import sk.annotation.library.jam.processor.data.confwrappers.FieldMappingData;
import sk.annotation.library.jam.processor.sourcewriter.SourceGeneratorContext;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.type.TypeMirror;

abstract public class AbstractRowValueTransformator {

	static AbstractRowValueTransformator[] rowFieldGenerators = new AbstractRowValueTransformator[] {
		NoRowValueTransformator.instance,
		DateRowValueTransformator.instance
	};

	abstract boolean accept(ProcessingEnvironment processingEnv, TypeMirror source, TypeMirror destination);
	abstract public String generateRowTransform(SourceGeneratorContext ctx, TypeMirror source, TypeMirror destination, String varValue);

	public static AbstractRowValueTransformator findRowFieldGenerator(ProcessingEnvironment processingEnv, TypeMirror sourceType, TypeMirror destinationType) {

		for (AbstractRowValueTransformator rowFieldGenerator : rowFieldGenerators) {
			try {
				if (rowFieldGenerator.accept(processingEnv, sourceType, destinationType)) return rowFieldGenerator;
			}
			catch (Exception e) {/*not important*/}
		}

		return null;

	}
}
