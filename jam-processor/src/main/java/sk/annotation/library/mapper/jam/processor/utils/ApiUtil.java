package sk.annotation.library.mapper.jam.processor.utils;

import com.sun.tools.javac.code.Type;
import sk.annotation.library.mapper.jam.annotations.IgnoredByJamMapper;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.util.ElementFilter;
import java.util.List;

abstract public class ApiUtil {
	static public boolean canImplementMethod(boolean parentTypeAsAbstractClass, ExecutableElement executableElement) {
		if (parentTypeAsAbstractClass) {
			if (!executableElement.getModifiers().contains(Modifier.ABSTRACT)) return false;

			// Maybe it will be generated, but probably some other way
			if (executableElement.getParameters()==null || executableElement.getParameters().isEmpty()) return false;
			if (executableElement.getReturnType() == null) return false; // not sure if works
			// TODO: Check default constructor !!!
			return true;
		}

		// INTERFACE:

		// Default methods cannot be overwritten !!!
		if (executableElement.getModifiers().contains(Modifier.DEFAULT)) return false;
		return true;
	}

	static public boolean ignoreUsing(boolean elementIsMy, Element element) {
		if (element == null) return true;

		if (element.getAnnotation(IgnoredByJamMapper.class) != null) return true;

		// Private methods or fields can not be used
		if (element.getModifiers().contains(Modifier.PRIVATE)) return true;

		// If its not public, it can not be used
		if (!elementIsMy && !element.getModifiers().contains(Modifier.PUBLIC)) return true;

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
