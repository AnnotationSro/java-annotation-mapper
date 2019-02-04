package sk.annotation.library.mapper.fast.processor.utils;

import com.sun.tools.javac.code.Attribute;
import com.sun.tools.javac.code.Type;
import sk.annotation.library.mapper.fast.annotations.FastMapper;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

abstract public class AnnotationValueUtils {



	public static List<Type> findWithCustomClasses(ProcessingEnvironment processingEnv, Element element) {
		return findAnotationTypeValues(processingEnv, element, FastMapper.class, "withCustom()");
	}
	public static List<Type> findAnotationTypeValues(ProcessingEnvironment processingEnv, Element element, Class<?> annotationClass, String valueMethodName) {
		List<Type> values = new LinkedList<>();

		Type typeFastMapperAnnotation = TypeUtils.convertToType(processingEnv, annotationClass);

		List<? extends AnnotationMirror> annotationMirrors = element.getAnnotationMirrors();
		if (annotationMirrors!=null) {
			for (AnnotationMirror annotationMirror : annotationMirrors) {
				if (!processingEnv.getTypeUtils().isSameType(annotationMirror.getAnnotationType(), typeFastMapperAnnotation)) continue;

				Map<? extends ExecutableElement, ? extends AnnotationValue> valuesWithDefaults = processingEnv.getElementUtils().getElementValuesWithDefaults(annotationMirror);

				for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> e : valuesWithDefaults.entrySet()) {
					if (!valueMethodName.equalsIgnoreCase(e.getKey().toString())) continue;

					Object val = e.getValue().getValue();
					if (val instanceof List) {
						for (Object o : (List) val) {
							if (o  instanceof Attribute.Class) {
								Attribute.Class ac = (Attribute.Class) o;
								values.add(ac.getValue());
								continue;
							}
						}
					}
					else if (val instanceof Attribute.Class) {
						Attribute.Class ac = (Attribute.Class) val;
						values.add(ac.getValue());
						continue;
					}
					else if (val instanceof Type) {
						values.add((Type)val);
					}
					else {
						throw new IllegalStateException("Unknown type value");
					}
				}

			}
		}
		return values;
	}
}
