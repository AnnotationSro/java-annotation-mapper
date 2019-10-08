package sk.annotation.library.jam.processor.data;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import sk.annotation.library.jam.annotations.EnableCDI;
import sk.annotation.library.jam.annotations.EnableSpring;
import sk.annotation.library.jam.annotations.enums.IocScope;
import sk.annotation.library.jam.processor.Constants;
import sk.annotation.library.jam.processor.utils.annotations.AnnotationValueExtractUtil;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

abstract public class IoCUtils {

    public static AnnotationsInfo getFieldAnnotationType(ProcessingEnvironment processingEnv, TypeElement element, String name) {
        if (element == null) return null;

        AnnotationsInfo val = new AnnotationsInfo();

        EnableSpring springSupport = element.getAnnotation(EnableSpring.class);
        if (springSupport != null) {
            val.getOrAddAnnotation(new TypeInfo(processingEnv, Constants.annotationFieldSPRING)).withStringValue(StringUtils.trimToNull(name));
        }

        EnableCDI cdiSupport = element.getAnnotation(EnableCDI.class);
        if (cdiSupport != null) {
            val.getOrAddAnnotation(new TypeInfo(processingEnv, Constants.annotationFieldCDI)).withStringValue(StringUtils.trimToNull(name));
        }

        return val;
    }


    static public AnnotationsInfo resolveMapperAnnotation(ProcessingEnvironment processingEnv, TypeElement element) {
        if (element == null) return null;

        AnnotationsInfo val = new AnnotationsInfo();

        try {
            resolveSpringMapperAnnotation(processingEnv, val, element.getAnnotation(EnableSpring.class));
        } catch (RuntimeException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, ExceptionUtils.getFullStackTrace(e), element, AnnotationValueExtractUtil.findAnnotationMirror(processingEnv, element, EnableSpring.class));
            throw e;
        }
        try {
            resolveCdiMapperAnnotation(processingEnv, val, element.getAnnotation(EnableCDI.class));
        } catch (RuntimeException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, ExceptionUtils.getFullStackTrace(e), element, AnnotationValueExtractUtil.findAnnotationMirror(processingEnv, element, EnableCDI.class));
            throw e;
        }
        return val;
    }


    static private void resolveSpringMapperAnnotation(ProcessingEnvironment processingEnv, AnnotationsInfo annotationsInfo, EnableSpring iocConfig) {
        if (iocConfig == null) return;

        annotationsInfo.getOrAddAnnotation(new TypeInfo(processingEnv, Constants.springComponent)).withStringValue(StringUtils.trimToNull(iocConfig.beanName()));

        IocScope scope = iocConfig.scope();
        if (scope != null) {
            switch (scope) {
                case DEFAULT:
                    break;
                case REQUEST:
                    annotationsInfo.getOrAddAnnotation(new TypeInfo(processingEnv, Constants.springContext)).withStringValue("request");
                    break;
                case SESSION:
                    annotationsInfo.getOrAddAnnotation(new TypeInfo(processingEnv, Constants.springContext)).withStringValue("session");
                    break;
                case SINGLETON:
                case APPLICATION:
                    annotationsInfo.getOrAddAnnotation(new TypeInfo(processingEnv, Constants.springContext)).withStringValue("application");
                    break;
            }
        }
    }

    static private void resolveCdiMapperAnnotation(ProcessingEnvironment processingEnv, AnnotationsInfo annotationsInfo, EnableCDI iocConfig) {
        if (iocConfig == null) return;

        annotationsInfo.getOrAddAnnotation(new TypeInfo(processingEnv, Constants.cdiComponent)).withStringValue(StringUtils.trimToNull(iocConfig.beanName()));

        IocScope scope = iocConfig.scope();
        if (scope != null) {
            switch (scope) {
                case DEFAULT:
                    break;
                case REQUEST:
                    annotationsInfo.getOrAddAnnotation(new TypeInfo(processingEnv, Constants.cdiContextRequest));
                    break;
                case SESSION:
                    annotationsInfo.getOrAddAnnotation(new TypeInfo(processingEnv, Constants.cdiContextSession));
                    break;
                case APPLICATION:
                    annotationsInfo.getOrAddAnnotation(new TypeInfo(processingEnv, Constants.cdiContextApplication));
                    break;
                case SINGLETON:
                    annotationsInfo.getOrAddAnnotation(new TypeInfo(processingEnv, Constants.cdiContextSingleton));
                    break;
            }
        }
    }

}
