package sk.annotation.library.jam.processor.utils.annotations;

import com.sun.tools.javac.code.Type;
import sk.annotation.library.jam.annotations.Mapper;
import sk.annotation.library.jam.annotations.MapperConfig;
import sk.annotation.library.jam.annotations.enums.IgnoreType;
import sk.annotation.library.jam.processor.data.MapperClassInfo;
import sk.annotation.library.jam.processor.utils.ElementUtils;
import sk.annotation.library.jam.processor.utils.TypeUtils;
import sk.annotation.library.jam.processor.utils.annotations.data.AnnotationConfigGenerator;
import sk.annotation.library.jam.processor.utils.annotations.data.AnnotationMapperConfig;
import sk.annotation.library.jam.processor.utils.annotations.data.TypeConfig;
import sk.annotation.library.jam.processor.utils.annotations.data.fields.AnnotationFieldId;
import sk.annotation.library.jam.processor.utils.annotations.data.fields.AnnotationFieldIgnore;
import sk.annotation.library.jam.processor.utils.annotations.data.fields.AnnotationFieldMapping;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static sk.annotation.library.jam.processor.utils.annotations.AnnotationValueExtractUtil.*;


abstract public class AnnotationValueUtils {

	public static List<AnnotationMapperConfig> resolveMapperConfigData(ProcessingEnvironment processingEnv, ExecutableElement method) {
		List<AnnotationMapperConfig> ret = new LinkedList<>();

		ElementUtils.findAllElementsWithAnnotationsInStructure(processingEnv, method, e -> {
			AnnotationMapperConfig annotationMapperConfig = resolveAnnotationMapperConfig(processingEnv, e);
			if (annotationMapperConfig != null) ret.add(annotationMapperConfig);
			return false;
		});

		return ret;
	}

	public static List<Type> findWithCustomClasses(ProcessingEnvironment processingEnv, Element element) {
		Map<String, AnnotationValue> annotationValueMap = getAnnotationValues(processingEnv, element, Mapper.class);
		if (annotationValueMap == null) return Collections.emptyList();
		return getAnnotationValue_ClassList(processingEnv, annotationValueMap.get("withCustom"));
	}

	static private AnnotationMapperConfig resolveAnnotationMapperConfig(ProcessingEnvironment processingEnv, Element element) {
		if (element == null) return null;

		Map<String, AnnotationValue> valuesWithDefaults = getAnnotationValues(processingEnv, element, MapperConfig.class);
		if (valuesWithDefaults == null || valuesWithDefaults.isEmpty()) return null;

		AnnotationMapperConfig annotationMapperConfig = new AnnotationMapperConfig();

		annotationMapperConfig.setType(TypeConfig.findTypeOrException(element));

		annotationMapperConfig.getImmutable().addAll(getAnnotationValue_ClassList(processingEnv, valuesWithDefaults.get("immutable")));

		for (Map<String, AnnotationValue> configMap : AnnotationValueExtractUtil.getAnnotationValue_innerValueMapList(processingEnv, valuesWithDefaults.get("config"))) {

			AnnotationConfigGenerator config = new AnnotationConfigGenerator();
			annotationMapperConfig.getConfig().add(config);

			AnnotationFieldId fieldId = new AnnotationFieldId();
			config.setFieldId(fieldId);

			fieldId.setPackages(getAnnotationValue_constantList(processingEnv, configMap.get("fieldPackages")));
			fieldId.setTypes(getAnnotationValue_ClassList(processingEnv, configMap.get("fieldTypes")));
			fieldId.setValue(AnnotationValueExtractUtil.getAnnotationValue_constant(processingEnv, configMap.get("field")));
			config.setMissingAsSource(AnnotationValueExtractUtil.getAnnotationValue_constant(processingEnv, configMap.get("missingAsSource")));
			config.setMissingAsDestination(AnnotationValueExtractUtil.getAnnotationValue_constant(processingEnv, configMap.get("missingAsDestination")));
		}

		for (Map<String, AnnotationValue> fieldMappingMap : AnnotationValueExtractUtil.getAnnotationValue_innerValueMapList(processingEnv, valuesWithDefaults.get("fieldMapping"))) {
			annotationMapperConfig.getFieldMapping().add(resolveFieldMapping(processingEnv, fieldMappingMap));
		}
		for (Map<String, AnnotationValue> fieldIgnoreMap : AnnotationValueExtractUtil.getAnnotationValue_innerValueMapList(processingEnv, valuesWithDefaults.get("fieldIgnore"))) {
			annotationMapperConfig.getFieldIgnore().add(resolveFieldIgnore(processingEnv, fieldIgnoreMap));
		}
		return annotationMapperConfig;
	}

	private static AnnotationFieldIgnore resolveFieldIgnore(ProcessingEnvironment processingEnv, Map<String, AnnotationValue> fieldIgnoreMap) {
		AnnotationFieldIgnore fieldIgnoreData = new AnnotationFieldIgnore();
		fieldIgnoreData.setPackages(getAnnotationValue_constantList(processingEnv, fieldIgnoreMap.get("packages")));
		fieldIgnoreData.setTypes(getAnnotationValue_ClassList(processingEnv, fieldIgnoreMap.get("types")));
		fieldIgnoreData.setIgnored(AnnotationValueExtractUtil.getAnnotationValue_enum(processingEnv, fieldIgnoreMap.get("ignored"), IgnoreType.class));
		fieldIgnoreData.setValue(AnnotationValueExtractUtil.getAnnotationValue_constant(processingEnv, fieldIgnoreMap.get("value")));
		return fieldIgnoreData;
	}

	private static AnnotationFieldMapping resolveFieldMapping(ProcessingEnvironment processingEnv, Map<String, AnnotationValue> fieldMappingMap) {
		AnnotationFieldMapping fieldMappingData = new AnnotationFieldMapping();

		AnnotationFieldId s = new AnnotationFieldId();
		s.setPackages(getAnnotationValue_constantList(processingEnv, fieldMappingMap.get("sPackages")));
		s.setTypes(getAnnotationValue_ClassList(processingEnv, fieldMappingMap.get("sTypes")));
		s.setValue(AnnotationValueExtractUtil.getAnnotationValue_constant(processingEnv, fieldMappingMap.get("s")));
		fieldMappingData.setS(s);

		AnnotationFieldId d = new AnnotationFieldId();
		d.setPackages(getAnnotationValue_constantList(processingEnv, fieldMappingMap.get("dPackages")));
		d.setTypes(getAnnotationValue_ClassList(processingEnv, fieldMappingMap.get("dTypes")));
		d.setValue(AnnotationValueExtractUtil.getAnnotationValue_constant(processingEnv, fieldMappingMap.get("d")));
		fieldMappingData.setD(d);

		fieldMappingData.setMethodNameS2D(AnnotationValueExtractUtil.getAnnotationValue_constant(processingEnv, fieldMappingMap.get("methodNameS2D")));
		fieldMappingData.setMethodNameD2S(AnnotationValueExtractUtil.getAnnotationValue_constant(processingEnv, fieldMappingMap.get("methodNameD2S")));
		fieldMappingData.setIgnoreDirectionS2D(AnnotationValueExtractUtil.getAnnotationValue_constant(processingEnv, fieldMappingMap.get("ignoreDirectionS2D")));
		fieldMappingData.setIgnoreDirectionD2S(AnnotationValueExtractUtil.getAnnotationValue_constant(processingEnv, fieldMappingMap.get("ignoreDirectionD2S")));

		return fieldMappingData;
	}


	public static List<AnnotationMapperConfig> getMapperConfig(ProcessingEnvironment processingEnv, MapperClassInfo ownerClassInfo) {
		List<AnnotationMapperConfig> ret = new LinkedList<>();

		ElementUtils.findAllElementsWithAnnotationsInStructure(processingEnv, ownerClassInfo.getParentElement(), e -> {
			AnnotationMapperConfig annotationMapperConfig = resolveAnnotationMapperConfig(processingEnv, e);
			if (annotationMapperConfig != null) ret.add(annotationMapperConfig);
			return false;
		});

		return ret;
	}

	public static boolean isConfiguredAsImmutableType(ProcessingEnvironment processingEnv, MapperClassInfo ownerClassInfo, TypeMirror type) {
		for (AnnotationMapperConfig annotationMapperConfig : getMapperConfig(processingEnv, ownerClassInfo)) {
			for (Type anotationTypeValue : annotationMapperConfig.getImmutable()) {
				if (TypeUtils.isSame(processingEnv, type, anotationTypeValue)) return true;
			}
		}

		return false;
	}
}

