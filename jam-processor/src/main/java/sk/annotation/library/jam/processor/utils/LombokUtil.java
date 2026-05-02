package sk.annotation.library.jam.processor.utils;

import sk.annotation.library.jam.processor.utils.annotations.AnnotationValueExtractUtil;
import sk.annotation.library.jam.processor.utils.commons.StringUtils;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import java.util.Map;

abstract public class LombokUtil {
	private LombokUtil() {
	}

	private final static String lombokAnnotationDATA = "lombok.Data";
	private final static String lombokAnnotationSetter = "lombok.Setter";
	private final static String lombokAnnotationGetter = "lombok.Getter";

	private static boolean isAccessLevelPublic(AnnotationValue value) {
		if (value == null) return false;
		if ("lombok.AccessLevel.PUBLIC".equals(String.valueOf(value))) return true;
		if (String.valueOf(value).toUpperCase().contains("PUBLIC")) return true;
		return false;
	}

	public static String findLombokPublicSetter(ProcessingEnvironment processingEnv, VariableElement field) {
		if (field == null) return null;

		String methodName = "set" + StringUtils.capitalize(field.getSimpleName().toString());

		Map<String, AnnotationValue> s = AnnotationValueExtractUtil.getAnnotationValues(processingEnv, field, lombokAnnotationSetter);
		if (s != null) {
			if (isAccessLevelPublic(s.get("value"))) return methodName;
			return null;
		}

		s = AnnotationValueExtractUtil.getAnnotationValues(processingEnv, field.getEnclosingElement(), lombokAnnotationSetter);
		if (s != null) {
			if (isAccessLevelPublic(s.get("value"))) return methodName;
			return null;
		}

		if (AnnotationValueExtractUtil.getAnnotationValues(processingEnv, field.getEnclosingElement(), lombokAnnotationSetter)!=null) {
			return methodName;
		}

		return null;
	}

	public static String findLombokPublicGetter(ProcessingEnvironment processingEnv, VariableElement field) {
		if (field == null) return null;

		String prefix = field.asType().getKind() == TypeKind.BOOLEAN ? "is" : "get";
		String methodName = prefix + StringUtils.capitalize(field.getSimpleName().toString());

		Map<String, AnnotationValue> s = AnnotationValueExtractUtil.getAnnotationValues(processingEnv, field, lombokAnnotationGetter);
		if (s != null) {
			if (isAccessLevelPublic(s.get("value"))) return methodName;
			return null;
		}

		s = AnnotationValueExtractUtil.getAnnotationValues(processingEnv, field.getEnclosingElement(), lombokAnnotationGetter);
		if (s != null) {
			if (isAccessLevelPublic(s.get("value"))) return methodName;
			return null;
		}

		if (AnnotationValueExtractUtil.getAnnotationValues(processingEnv, field.getEnclosingElement(), lombokAnnotationGetter)!=null) {
			return methodName;
		}
		/*Getter s = field.getAnnotation(Getter.class);
		if (s!=null) {
			if (s.value()== AccessLevel.PUBLIC) return methodName;
			return null;
		}

		s = field.getEnclosingElement().getAnnotation(Getter.class);
		if (s!=null) {
			if (s.value()== AccessLevel.PUBLIC) return methodName;
			return null;
		}

		if (field.getEnclosingElement().getAnnotation(Data.class)!=null) {
			return methodName;
		}*/

		return null;
	}


}
