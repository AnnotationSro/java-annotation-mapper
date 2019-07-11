package sk.annotation.library.jam.processor.data.confwrappers;

import com.sun.tools.javac.code.Type;
import sk.annotation.library.jam.annotations.MapperConfig;
import sk.annotation.library.jam.processor.data.MapperClassInfo;
import sk.annotation.library.jam.processor.utils.AnnotationValueUtils;
import sk.annotation.library.jam.processor.utils.ElementUtils;
import sk.annotation.library.jam.processor.utils.TypeUtils;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import java.util.List;

abstract public class MapperConfigurationResolver {
    public static List<MapperConfig> getMapperConfig(ProcessingEnvironment processingEnv, MapperClassInfo ownerClassInfo) {
        return ElementUtils.findAllAnnotationsInStructure(processingEnv, ownerClassInfo.getParentElement(), MapperConfig.class);
    }

    public static boolean isConfiguredAsImmutableType(ProcessingEnvironment processingEnv, MapperClassInfo ownerClassInfo, TypeMirror type) {
        List<Element> allElementsWithAnnotationsInStructure = ElementUtils.findAllElementsWithAnnotationsInStructure(processingEnv, ownerClassInfo.getParentElement(), MapperConfig.class);
        Type typeJamMapperAnnotation = TypeUtils.convertToType(processingEnv, MapperConfig.class);

        for (Element element : allElementsWithAnnotationsInStructure) {
            List<Type> anotationTypeValues = AnnotationValueUtils.findAnotationTypeValues(processingEnv, element, typeJamMapperAnnotation, "immutable()");
            for (Type anotationTypeValue : anotationTypeValues) {
                if (TypeUtils.isSame(processingEnv, type, anotationTypeValue)) return true;
            }
        }

        return false;
    }
}

