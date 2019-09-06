package sk.annotation.library.jam.processor.utils.annotations;

import com.sun.tools.javac.code.Type;
import sk.annotation.library.jam.annotations.Mapper;
import sk.annotation.library.jam.annotations.MapperConfig;
import sk.annotation.library.jam.annotations.MapperFieldConfig;
import sk.annotation.library.jam.annotations.enums.IgnoreType;
import sk.annotation.library.jam.processor.data.MapperClassInfo;
import sk.annotation.library.jam.processor.utils.ElementUtils;
import sk.annotation.library.jam.processor.utils.TypeUtils;
import sk.annotation.library.jam.processor.utils.annotations.data.AnnotationConfigGenerator;
import sk.annotation.library.jam.processor.utils.annotations.data.AnnotationMapperConfig;
import sk.annotation.library.jam.processor.utils.annotations.data.fields.AnnotationFieldId;
import sk.annotation.library.jam.processor.utils.annotations.data.fields.AnnotationFieldIgnore;
import sk.annotation.library.jam.processor.utils.annotations.data.fields.AnnotationFieldMapping;
import sk.annotation.library.jam.processor.utils.annotations.data.fields.AnnotationMapperFieldConfig;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static sk.annotation.library.jam.processor.utils.annotations.AnnotationValueExtractUtil.getAnnotationValue_ClassList;
import static sk.annotation.library.jam.processor.utils.annotations.AnnotationValueExtractUtil.getAnnotationValues;


abstract public class AnnotationValueUtils {
	static public List<AnnotationMapperFieldConfig> resolveMapperFieldConfigData(ProcessingEnvironment processingEnv, ExecutableElement method) {
		List<AnnotationMapperFieldConfig> retValues = new LinkedList<>();

		if (method == null) return retValues;

		AnnotationMapperFieldConfig dt = resolveAnnotationMapperFieldConfig(processingEnv, method);
		if (dt != null) retValues.add(dt);

		ElementUtils.findAllElementsWithAnnotationsInStructure(processingEnv, method.getEnclosingElement(), e -> {
			AnnotationMapperFieldConfig dt1 = resolveAnnotationMapperFieldConfig(processingEnv, e);
			if (dt1 != null) retValues.add(dt1);
			return false;
		});

		return retValues;
	}

	public static List<Type> findWithCustomClasses(ProcessingEnvironment processingEnv, Element element) {
		Map<String, AnnotationValue> annotationValueMap = getAnnotationValues(processingEnv, element, Mapper.class);
		if (annotationValueMap == null) return Collections.emptyList();
		return getAnnotationValue_ClassList(processingEnv, annotationValueMap.get("withCustom"));
	}


	static private AnnotationMapperFieldConfig resolveAnnotationMapperFieldConfig(ProcessingEnvironment processingEnv, Element element) {
		if (element == null) return null;

		Map<String, AnnotationValue> valuesWithDefaults = getAnnotationValues(processingEnv, element, MapperFieldConfig.class);
		if (valuesWithDefaults == null || valuesWithDefaults.isEmpty()) return null;

		AnnotationMapperFieldConfig mapperFieldConfigData = new AnnotationMapperFieldConfig();

		for (Map<String, AnnotationValue> fieldMappingMap : AnnotationValueExtractUtil.getAnnotationValue_innerValueMapList(processingEnv, valuesWithDefaults.get("fieldMapping"))) {
			AnnotationFieldMapping fieldMappingData = new AnnotationFieldMapping();

			AnnotationFieldId d = new AnnotationFieldId();
			d.setTypes(getAnnotationValue_ClassList(processingEnv, fieldMappingMap.get("dstObj")));
			d.setValue(AnnotationValueExtractUtil.getAnnotationValue_constant(processingEnv, fieldMappingMap.get("d")));
			fieldMappingData.setD(d);

			AnnotationFieldId s = new AnnotationFieldId();
			s.setTypes(getAnnotationValue_ClassList(processingEnv, fieldMappingMap.get("srcObj")));
			s.setValue(AnnotationValueExtractUtil.getAnnotationValue_constant(processingEnv, fieldMappingMap.get("s")));
			fieldMappingData.setS(s);

			fieldMappingData.setMethodNameS2D(AnnotationValueExtractUtil.getAnnotationValue_constant(processingEnv, fieldMappingMap.get("methodNameS2D")));
			fieldMappingData.setMethodNameD2S(AnnotationValueExtractUtil.getAnnotationValue_constant(processingEnv, fieldMappingMap.get("methodNameD2S")));
			fieldMappingData.setIgnoreDirectionS2D(AnnotationValueExtractUtil.getAnnotationValue_constant(processingEnv, fieldMappingMap.get("ignoreDirectionS2D")));
			fieldMappingData.setIgnoreDirectionD2S(AnnotationValueExtractUtil.getAnnotationValue_constant(processingEnv, fieldMappingMap.get("ignoreDirectionD2S")));

			mapperFieldConfigData.getFieldMapping().add(fieldMappingData);
		}
		for (Map<String, AnnotationValue> fieldIgnoreMap : AnnotationValueExtractUtil.getAnnotationValue_innerValueMapList(processingEnv, valuesWithDefaults.get("fieldIgnore"))) {
			AnnotationFieldIgnore fieldIgnoreData = new AnnotationFieldIgnore();
			fieldIgnoreData.setTypes(getAnnotationValue_ClassList(processingEnv, fieldIgnoreMap.get("types")));
			fieldIgnoreData.setIgnored(AnnotationValueExtractUtil.getAnnotationValue_enum(processingEnv, fieldIgnoreMap.get("ignored"), IgnoreType.class));
			fieldIgnoreData.setValue(AnnotationValueExtractUtil.getAnnotationValue_constant(processingEnv, fieldIgnoreMap.get("value")));
			mapperFieldConfigData.getFieldIgnore().add(fieldIgnoreData);
		}

		return mapperFieldConfigData;
	}


	public static List<AnnotationMapperConfig> getMapperConfig(ProcessingEnvironment processingEnv, MapperClassInfo ownerClassInfo) {
		List<AnnotationMapperConfig> ret = new LinkedList<>();

		ElementUtils.findAllElementsWithAnnotationsInStructure(processingEnv, ownerClassInfo.getParentElement(), e -> {
			Map<String, AnnotationValue> annotationValuesMap = getAnnotationValues(processingEnv, e, MapperConfig.class);
			if (annotationValuesMap == null || annotationValuesMap.isEmpty()) return false;

			AnnotationMapperConfig val = new AnnotationMapperConfig();
			ret.add(val);

			val.setImmutable(getAnnotationValue_ClassList(processingEnv, annotationValuesMap.get("immutable")));
			for (Map<String, AnnotationValue> configMap : AnnotationValueExtractUtil.getAnnotationValue_innerValueMapList(processingEnv, annotationValuesMap.get("config"))) {

				AnnotationConfigGenerator config = new AnnotationConfigGenerator();
				val.getConfig().add(config);

				AnnotationFieldId fieldId = new AnnotationFieldId();
				config.setFieldId(fieldId);

				fieldId.setTypes(getAnnotationValue_ClassList(processingEnv, configMap.get("fieldType")));
				fieldId.setValue(AnnotationValueExtractUtil.getAnnotationValue_constant(processingEnv, configMap.get("field")));
				config.setMissingAsSource(AnnotationValueExtractUtil.getAnnotationValue_constant(processingEnv, configMap.get("missingAsSource")));
				config.setMissingAsDestination(AnnotationValueExtractUtil.getAnnotationValue_constant(processingEnv, configMap.get("missingAsDestination")));
			}

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

