package sk.annotation.library.mapper.fast.processor.utils;

import com.sun.tools.javac.code.Type;
import sk.annotation.library.mapper.fast.annotations.IgnoredByFastMapper;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import java.util.Collections;
import java.util.List;

abstract public class ApiUtil {
	static public boolean canImplementMethod(boolean parentTypeAsAbstractClass, ExecutableElement executableElement) {
		if (parentTypeAsAbstractClass) {
			if (!executableElement.getModifiers().contains(Modifier.ABSTRACT)) return false;

			// Mozno to vygenerujeme, ale nebudeme to asi riesit takto
			if (executableElement.getParameters()==null || executableElement.getParameters().isEmpty()) return false;
			if (executableElement.getReturnType() == null) return false; // neviem, ci zafunguje
			// TODO: Check default constructor !!!
			return true;
		}

		// INTERFACE:

		// Default metody nemozeme prepisovat !!!
		if (executableElement.getModifiers().contains(Modifier.DEFAULT)) return false;
		return true;
	}

	static public boolean ignoreUsing(boolean elementIsMy, Element element) {
		if (element == null) return false;

		if (element.getAnnotation(IgnoredByFastMapper.class) != null) return false;

		// Privatne metody alebo fieldy neviem nic pouzivat
		if (element.getModifiers().contains(Modifier.PRIVATE)) return false;

		// Ak nie je public, tiez neviem pouzivat
		if (!elementIsMy && element.getModifiers().contains(Modifier.PUBLIC)) return true;

		return false;
	}

	static public List<ExecutableElement> readElementApi(ProcessingEnvironment processingEnv, Type type) {
		List<? extends Element> allMembers = ElementUtils.findAllAcceptedMember(processingEnv, (TypeElement) type.asElement());
		return ElementFilter.methodsIn(allMembers);
	}

	static public boolean canImplementMapper(TypeElement typeElement) {
		if (typeElement.getKind() == ElementKind.CLASS) {
			if (!typeElement.getModifiers().contains(Modifier.ABSTRACT)) return false;

			if (typeElement.getModifiers().contains(Modifier.FINAL)) return false;

			return true;
		}

		if (typeElement.getKind() == ElementKind.INTERFACE) {
			return true;
		}


		return false;
	}
}
