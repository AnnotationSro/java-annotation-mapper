package sk.annotation.library.mapper.fast.processor.data;

import org.apache.commons.lang.StringUtils;
import sk.annotation.library.mapper.fast.annotations.EnableCDI;
import sk.annotation.library.mapper.fast.annotations.EnableSpring;
import sk.annotation.library.mapper.fast.annotations.enums.IocScope;
import sk.annotation.library.mapper.fast.processor.Constants;

import javax.lang.model.element.TypeElement;

abstract public class IoCUtils {

	public static AnnotationsInfo getFieldAnnotationType(TypeElement element, String name) {
		if (element == null) return null;

		AnnotationsInfo val = new AnnotationsInfo();

		EnableSpring springSupport = element.getAnnotation(EnableSpring.class);
		if (springSupport !=null) {
			val.getOrAddAnnotation(Constants.annotationFieldSPRING).withStringValue(StringUtils.trimToNull(name));
		}

		EnableCDI cdiSupport = element.getAnnotation(EnableCDI.class);
		if (cdiSupport!=null) {
			val.getOrAddAnnotation(Constants.annotationFieldCDI).withStringValue(StringUtils.trimToNull(name));
		}

		return val;
	}


	static public AnnotationsInfo resolveMapperAnnotation(TypeElement element) {
		if (element == null) return null;

		AnnotationsInfo val = new AnnotationsInfo();

		resolveSpringMapperAnnotation(val, element.getAnnotation(EnableSpring.class));
		resolveCdiMapperAnnotation(val, element.getAnnotation(EnableCDI.class));
		return val;
	}



	static private void resolveSpringMapperAnnotation(AnnotationsInfo annotationsInfo, EnableSpring iocConfig) {
		if (iocConfig == null) return ;

		annotationsInfo.getOrAddAnnotation(Constants.springComponent).withStringValue(StringUtils.trimToNull(iocConfig.beanName()));

		IocScope scope = iocConfig.scope();
		if (scope != null) {
			switch (scope) {
				case DEFAULT:
					break;
				case REQUEST:
					annotationsInfo.getOrAddAnnotation(Constants.springContext).withStringValue("request");
					break;
				case SESSION:
					annotationsInfo.getOrAddAnnotation(Constants.springContext).withStringValue("session");
					break;
				case SINGLETON:
				case APPLICATION:
					annotationsInfo.getOrAddAnnotation(Constants.springContext).withStringValue("application");
					break;
			}
		}
	}

	static private void resolveCdiMapperAnnotation(AnnotationsInfo annotationsInfo, EnableCDI iocConfig) {
		if (iocConfig == null) return;

		annotationsInfo.getOrAddAnnotation(Constants.cdiComponent).withStringValue(StringUtils.trimToNull(iocConfig.beanName()));

		IocScope scope = iocConfig.scope();
		if (scope != null) {
			switch (scope) {
				case DEFAULT:
					break;
				case REQUEST:
					annotationsInfo.getOrAddAnnotation(Constants.cdiContextRequest);
					break;
				case SESSION:
					annotationsInfo.getOrAddAnnotation(Constants.cdiContextSession);
					break;
				case APPLICATION:
					annotationsInfo.getOrAddAnnotation(Constants.cdiContextApplication);
					break;
				case SINGLETON:
					annotationsInfo.getOrAddAnnotation(Constants.cdiContextSingleton);
					break;
			}
		}
	}

}
