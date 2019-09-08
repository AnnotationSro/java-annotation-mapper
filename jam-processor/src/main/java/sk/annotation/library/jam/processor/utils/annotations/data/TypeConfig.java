package sk.annotation.library.jam.processor.utils.annotations.data;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;

public enum TypeConfig {
	METHOD, CLASS, PACKAGE;


	public static TypeConfig findTypeOrException (Element element) {
		if (element instanceof ExecutableElement) {
			return TypeConfig.METHOD;
		}
		if (element instanceof TypeElement) {
			return TypeConfig.CLASS;
		}
		if (element instanceof PackageElement) {
			return TypeConfig.PACKAGE;
		}

		throw new IllegalStateException("UnknownElement");
	}
}
