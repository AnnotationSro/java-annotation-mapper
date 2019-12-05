package sk.annotation.library.jam.processor.utils;

import com.sun.tools.javac.code.Type;
import sk.annotation.library.jam.annotations.JamVisibility;
import sk.annotation.library.jam.annotations.enums.MapperVisibility;
import sk.annotation.library.jam.processor.data.TypeInfo;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.util.ElementFilter;
import java.util.List;

abstract public class ApiUtil {
    static public boolean canImplementMethod(boolean parentTypeAsAbstractClass, ExecutableElement executableElement) {
        if (parentTypeAsAbstractClass) {
            if (!executableElement.getModifiers().contains(Modifier.ABSTRACT)) return false;

            // Maybe it will be generated, but probably some other way
            if (executableElement.getParameters() == null || executableElement.getParameters().isEmpty()) return false;
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

        JamVisibility visibility = element.getAnnotation(JamVisibility.class);
        if (visibility != null) {
            if (visibility.value() == MapperVisibility.IGNORED) return true;
            if (visibility.value() == MapperVisibility.THIS_MAPPER && !elementIsMy) return true;
        }

        // INTERCEPTORS are defaulty visible for THIS_MAPPER only
        else if (!elementIsMy && element instanceof ExecutableElement) {
            TypeInfo returnType = TypeInfo.analyzeReturnType(((ExecutableElement)element).getReturnType());
            if (returnType == null) return true;
        }

//		if (element.getAnnotation(IgnoredByJamMapper.class) != null) return true;

        // Private methods or fields can not be used
        if (element.getModifiers().contains(Modifier.STATIC)) return true;  // static fields are default ignored
        if (element.getModifiers().contains(Modifier.PRIVATE)) return true;
        // Abstract metody sa nemozu pouzivat priamo - vyuzivat sa mozu iba extendovane sub implementacie
        if (element.getModifiers().contains(Modifier.ABSTRACT) && elementIsMy) return true;

        // If its not public, it can not be used
        if (!elementIsMy && !element.getModifiers().contains(Modifier.PUBLIC)) return true;

        return false;
    }

    static public List<ExecutableElement> readElementApi(ProcessingEnvironment processingEnv, Type type) {
        if (type == null) return null;
        if (type.getKind().isPrimitive()) return null;
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
