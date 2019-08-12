package sk.annotation.library.jam.processor.utils;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;

import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;

abstract public class LombokUtil {
	private LombokUtil() {}

	public static String findLombokPublicSetter(VariableElement field) {
		if (field == null) return null;

		String methodName = "set" + StringUtils.capitalize(field.getSimpleName().toString());

		Setter s = field.getAnnotation(Setter.class);
		if (s!=null) {
			if (s.value()== AccessLevel.PUBLIC) return methodName;
			return null;
		}

		s = field.getEnclosingElement().getAnnotation(Setter.class);
		if (s!=null) {
			if (s.value()== AccessLevel.PUBLIC) return methodName;
			return null;
		}

		if (field.getEnclosingElement().getAnnotation(Data.class)!=null) {
			return methodName;
		}

		return null;
	}
	public static String findLombokPublicGetter(VariableElement field) {
		if (field == null) return null;

		String prefix = TypeUtils.findType(field).getKind() == TypeKind.BOOLEAN ? "is" : "get";
		String methodName = prefix + StringUtils.capitalize(field.getSimpleName().toString());

		Getter s = field.getAnnotation(Getter.class);
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
		}

		return null;
	}


}
