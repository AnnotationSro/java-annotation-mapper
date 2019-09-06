package sk.annotation.library.jam.processor.utils.annotations;

import com.sun.tools.javac.code.Attribute;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import sk.annotation.library.jam.processor.utils.ElementUtils;
import sk.annotation.library.jam.processor.utils.TypeUtils;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.function.Function;

abstract public class AnnotationValueExtractUtil {




	static List<Map<String, AnnotationValue>> getAnnotationValue_innerValueMapList(ProcessingEnvironment processingEnv, AnnotationValue value) {
		return getAnnotationValue_common(processingEnv, value, val -> {
			if (val instanceof Attribute.Compound) {
				Map<String, AnnotationValue> ret = new HashMap<>();
				Attribute.Compound obj = (Attribute.Compound) val;
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

	static List<Type> getAnnotationValue_ClassList(ProcessingEnvironment processingEnv, AnnotationValue value) {
		return getAnnotationValue_common(processingEnv, value, val -> {
			if (val instanceof Attribute.Class) {
				return ((Attribute.Class)val).getValue();
			}
			if (val instanceof Type) {
				return (Type) val;
			}
			throw new IllegalStateException("Unknown type value");
		});
	}
	static <T> T getAnnotationValue_constant(ProcessingEnvironment processingEnv, AnnotationValue value) {
		if (value == null) return null;
		return getAnnotationValue_common(processingEnv, value, val -> {
			if (val instanceof Attribute.Constant) {
				Attribute.Constant constant = (Attribute.Constant)val;
				return (T) constant.getValue();
			}
			return (T) val;
		}).get(0);
	}

	static <T extends Enum> T getAnnotationValue_enum(ProcessingEnvironment processingEnv, AnnotationValue value, Class<T> cls) {
		if (value == null) return null;
		return getAnnotationValue_common(processingEnv, value, val -> {
			if (val instanceof Symbol.VarSymbol) {
				Symbol.VarSymbol symbol = (Symbol.VarSymbol)val;
				String enumName = symbol.getSimpleName().toString();
				for (T e : cls.getEnumConstants()) {
					if (e.name().equals(enumName)) {
						return e;
					}
				}
				return null;
			}
			return (T) val;
		}).get(0);
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


	static <T extends Annotation> Map<String, AnnotationValue> getAnnotationValues(ProcessingEnvironment processingEnv, Element element, Class<T> cls) {
		if (element == null || element.getAnnotationMirrors()==null || element.getAnnotationMirrors().isEmpty()) return null;

		Type typeMapperFieldConfig = TypeUtils.convertToType(processingEnv, cls);

		for (AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
			if (!processingEnv.getTypeUtils().isSameType(annotationMirror.getAnnotationType(), typeMapperFieldConfig))
				continue;

			Map<String, AnnotationValue> ret = new LinkedHashMap<>();
			for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : processingEnv.getElementUtils().getElementValuesWithDefaults(annotationMirror).entrySet()) {
				AnnotationValue v = entry.getValue();
				ret.put(getAnnotationMethodName(entry.getKey()), v);
			}
			return ret;

		}
		return null;
	}
	public static <T extends Annotation> Map<String, AnnotationValue> getAnnotationValues(ProcessingEnvironment processingEnv, Element element, String clsName) {
		if (element == null || element.getAnnotationMirrors()==null || element.getAnnotationMirrors().isEmpty()) return null;

		for (AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
			if (!clsName.equals(ElementUtils.getQualifiedName(annotationMirror.getAnnotationType().asElement()))){
				continue;
			}

			Map<String, AnnotationValue> ret = new LinkedHashMap<>();
			for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : processingEnv.getElementUtils().getElementValuesWithDefaults(annotationMirror).entrySet()) {
				AnnotationValue v = entry.getValue();
				ret.put(getAnnotationMethodName(entry.getKey()), v);
			}
			return ret;

		}
		return null;
	}

}

