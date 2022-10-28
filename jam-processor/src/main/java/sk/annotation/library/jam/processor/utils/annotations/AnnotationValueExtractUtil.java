package sk.annotation.library.jam.processor.utils.annotations;

import sk.annotation.library.jam.processor.utils.ElementUtils;
import sk.annotation.library.jam.processor.utils.TypeUtils;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.function.Function;

abstract public class AnnotationValueExtractUtil {




	static List<Map<String, AnnotationValue>> getAnnotationValue_innerValueMapList(ProcessingEnvironment processingEnv, AnnotationValue value) {
		return getAnnotationValue_common(processingEnv, value, val -> {
			if (val instanceof AnnotationMirror) {
				Map<String, AnnotationValue> ret = new HashMap<>();
				AnnotationMirror obj = (AnnotationMirror) val;
				if (obj.getElementValues() == null) return ret;
//				Map<Symbol.MethodSymbol, Attribute> map = obj.getElementValues();
//				for (Map.Entry<Symbol.MethodSymbol, Attribute> entry : map.entrySet()) {
//					ret.put(entry.getKey().getSimpleName().toString(), entry.getValue());
//				}
				Map<? extends ExecutableElement, ? extends AnnotationValue> map = processingEnv.getElementUtils().getElementValuesWithDefaults(obj);
				for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : map.entrySet()) {
					ret.put(getAnnotationMethodName(entry.getKey()), entry.getValue());
				}
				return ret;
			}
			throw new IllegalStateException("Unknown type value");
		});
	}

	private static String getAnnotationMethodName(ExecutableElement method) {
		String v = method.getSimpleName().toString();
		//v = v.replace("()","");
		return v;
	}

	static List<DeclaredType> getAnnotationValue_ClassList(ProcessingEnvironment processingEnv, AnnotationValue value) {
		if (value == null) {
			throw new IllegalStateException("Annotation value cannot be null!");
		}
		List<DeclaredType> ret = getAnnotationValue_common(processingEnv, value, val -> {
			// Compilation errors
			if ("<error>".contains(val.toString())) {
				return null;
			}
			if (val instanceof DeclaredType) {
				return (DeclaredType) val;
			}
			if (val instanceof AnnotationValue) {
				return (DeclaredType) ((AnnotationValue)val).getValue();
			}
//			if (val instanceof Type) {
//				return (Type) val;
//			}
//			if (val instanceof Attribute.Class) {
//				return ((Attribute.Class)val).getValue();
//			}

//			if (val instanceof Attribute.Error) {
//				return null;
//			}
			throw new IllegalStateException("Unknown type value " + val.toString());
		});

		if (ret == null) {
			throw new IllegalStateException("Annotation value cannot be null!");
		}
		return ret;
	}
	static <T> List<T> getAnnotationValue_constantList(ProcessingEnvironment processingEnv, AnnotationValue value) {
		if (value == null) {
			throw new IllegalStateException("Annotation value cannot be null!");
		}
		List<T> ret = getAnnotationValue_common(processingEnv, value, val -> {
			if (val instanceof AnnotationValue) {
				AnnotationValue constant = (AnnotationValue)val;
				return (T) constant.getValue();
			}
			return (T) val;
		});
		if (ret == null) {
			throw new IllegalStateException("Annotation value cannot be null!");
		}
		return ret;
	}
	static <T> T getAnnotationValue_constant(ProcessingEnvironment processingEnv, AnnotationValue value) {
		if (value == null) return null;
		return getAnnotationValue_common(processingEnv, value, val -> {
			if (val instanceof AnnotationValue) {
				AnnotationValue constant = (AnnotationValue)val;
				return (T) constant.getValue();
			}
			return (T) val;
		}).get(0);
	}

	static <T extends Enum> T getAnnotationValue_enum(ProcessingEnvironment processingEnv, AnnotationValue value, Class<T> cls) {
		if (value == null) return null;
		return getAnnotationValue_enumList(processingEnv, value, cls).get(0);
	}

	static <T extends Enum> List<T> getAnnotationValue_enumList(ProcessingEnvironment processingEnv, AnnotationValue value, Class<T> cls) {
		if (value == null) return null;
		return getAnnotationValue_common(processingEnv, value, val -> {
			if (val instanceof VariableElement) {
				VariableElement symbol = (VariableElement)val;
				String enumName = symbol.getSimpleName().toString();
				for (T e : cls.getEnumConstants()) {
					if (e.name().equals(enumName)) {
						return e;
					}
				}
				return null;
			}
			if (val instanceof AnnotationValue) {
				AnnotationValue s = (AnnotationValue) val;
				String enumName = s.getValue().toString();


//				Attribute.Enum symbol = (Attribute.Enum)val;
//				String enumName = symbol.value.getSimpleName().toString();
				for (T e : cls.getEnumConstants()) {
					if (e.name().equals(enumName)) {
						return e;
					}
				}
				return null;
			}
			return (T) val;
		});
	}

	private static <T> List<T> getAnnotationValue_common(ProcessingEnvironment processingEnv, AnnotationValue value, Function<Object, T> acceptType) {

		List<T> values = new LinkedList<>();
		if (value == null) return values;

		Object val = value.getValue();
		if (val == null) return values;

		if (val instanceof List) {
			for (Object o : (List) val) {
				T t = acceptType.apply(o);
				if (t!=null) values.add(t);
			}
			return values;
		}
		if (val.getClass().isArray()) {
			for (Object o : (Object[]) val) {
				T t = acceptType.apply(o);
				if (t!=null) values.add(t);
			}
			return values;
		}

		T t = acceptType.apply(val);
		if (t!=null) values.add(t);

		return values;
	}


	public static <T extends Annotation> AnnotationMirror findAnnotationMirror(ProcessingEnvironment processingEnv, Element element, Class<T> cls) {
		if (element == null || element.getAnnotationMirrors()==null || element.getAnnotationMirrors().isEmpty()) return null;

		TypeMirror typeMapperFieldConfig = TypeUtils.convertToTypeMirror(processingEnv, cls);

		for (AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
			if (!TypeUtils.isSameType(processingEnv,annotationMirror.getAnnotationType(), typeMapperFieldConfig))
				continue;
			return annotationMirror;
		}
		return null;
	}

	static <T extends Annotation> Map<String, AnnotationValue> getAnnotationValues(ProcessingEnvironment processingEnv, Element element, Class<T> cls) {
		AnnotationMirror annotationMirror = findAnnotationMirror(processingEnv, element, cls);
		if (annotationMirror == null) return null;
		return getAnnotationValuesMapFromAnnotationMirror(processingEnv, annotationMirror);
	}

	public static Map<String, AnnotationValue> getAnnotationValuesMapFromAnnotationMirror(ProcessingEnvironment processingEnv, AnnotationMirror annotationMirror) {
		if (annotationMirror == null) return null;
		Map<String, AnnotationValue> ret = new LinkedHashMap<>();
		for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : processingEnv.getElementUtils().getElementValuesWithDefaults(annotationMirror).entrySet()) {
			AnnotationValue v = entry.getValue();
			ret.put(getAnnotationMethodName(entry.getKey()), v);
		}
		return ret;
	}

	public static <T extends Annotation> Map<String, AnnotationValue> getAnnotationValues(ProcessingEnvironment processingEnv, Element element, String clsName) {
		if (element == null || element.getAnnotationMirrors()==null || element.getAnnotationMirrors().isEmpty()) return null;

		for (AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
			if (!clsName.equals(ElementUtils.getQualifiedName(annotationMirror.getAnnotationType().asElement()))){
				continue;
			}

			Map<String, AnnotationValue> ret = getAnnotationValuesMapFromAnnotationMirror(processingEnv, annotationMirror);
			return ret;

		}
		return null;
	}

}

