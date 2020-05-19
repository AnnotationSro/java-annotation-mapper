package sk.annotation.library.jam.processor.utils.annotations;

import com.sun.tools.javac.code.Type;
import sk.annotation.library.jam.annotations.MapperConfig;
import sk.annotation.library.jam.annotations.enums.ApplyFieldStrategy;
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
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static sk.annotation.library.jam.processor.utils.annotations.AnnotationValueExtractUtil.*;


abstract public class AnnotationValueUtils {
    public static List<AnnotationMapperConfig> resolveMapperConfigData(ProcessingEnvironment processingEnv, Element method) {
        List<AnnotationMapperConfig> ret = new LinkedList<>();

        ElementUtils.findAllElementsWithAnnotationsInStructure(processingEnv, method, e -> {
            AnnotationMapperConfig annotationMapperConfig = resolveAnnotationMapperConfig(processingEnv, e);
            if (annotationMapperConfig != null) ret.add(annotationMapperConfig);
            return false;
        });

        return ret;
    }

    static public AnnotationMapperConfig resolveAnnotationMapperConfig(ProcessingEnvironment processingEnv, Element element) {
        if (element == null) return null;

        AnnotationMirror annotationMirror = findAnnotationMirror(processingEnv, element, MapperConfig.class);
        if (annotationMirror == null) return null;

        Map<String, AnnotationValue> valuesWithDefaults = getAnnotationValuesMapFromAnnotationMirror(processingEnv, annotationMirror);
        if (valuesWithDefaults == null || valuesWithDefaults.isEmpty()) return null;

        AnnotationMapperConfig annotationMapperConfig = new AnnotationMapperConfig();

        annotationMapperConfig.setType(TypeConfig.findTypeOrException(element));

        annotationMapperConfig.getImmutable().addAll(getAnnotationValue_ClassList(processingEnv, valuesWithDefaults.get("immutable")));
        annotationMapperConfig.getWithCustom().addAll(getAnnotationValue_ClassList(processingEnv, valuesWithDefaults.get("withCustom")));
        annotationMapperConfig.getApplyWhen().addAll(AnnotationValueExtractUtil.getAnnotationValue_enumList(processingEnv, valuesWithDefaults.get("applyWhen"), ApplyFieldStrategy.class));

        for (Map<String, AnnotationValue> configMap : AnnotationValueExtractUtil.getAnnotationValue_innerValueMapList(processingEnv, valuesWithDefaults.get("config"))) {

            List<String> fields = AnnotationValueExtractUtil.getAnnotationValue_constantList(processingEnv, configMap.get("field"));
            if (fields == null || fields.isEmpty()) continue;

            for (String field : fields) {
                AnnotationConfigGenerator config = new AnnotationConfigGenerator();
                annotationMapperConfig.getConfig().add(config);

                AnnotationFieldId fieldId = new AnnotationFieldId();
                config.setFieldId(fieldId);

                fieldId.setPackages(getAnnotationValue_constantList(processingEnv, configMap.get("fieldPackages")));
                fieldId.setTypes(getAnnotationValue_ClassList(processingEnv, configMap.get("fieldTypes")));
                fieldId.setValue(field);
                config.setMissingAsSource(AnnotationValueExtractUtil.getAnnotationValue_constant(processingEnv, configMap.get("missingAsSource")));
                config.setMissingAsDestination(AnnotationValueExtractUtil.getAnnotationValue_constant(processingEnv, configMap.get("missingAsDestination")));
            }
        }

        for (Map<String, AnnotationValue> fieldMappingMap : AnnotationValueExtractUtil.getAnnotationValue_innerValueMapList(processingEnv, valuesWithDefaults.get("fieldMapping"))) {
            resolveFieldMapping(annotationMapperConfig.getFieldMapping(), processingEnv, fieldMappingMap, annotationMirror, element);
        }
        for (Map<String, AnnotationValue> fieldIgnoreMap : AnnotationValueExtractUtil.getAnnotationValue_innerValueMapList(processingEnv, valuesWithDefaults.get("fieldIgnore"))) {
            resolveFieldIgnore(annotationMapperConfig.getFieldIgnore(), processingEnv, fieldIgnoreMap);
        }
        return annotationMapperConfig;
    }

    private static void resolveFieldIgnore(List<AnnotationFieldIgnore> ret, ProcessingEnvironment processingEnv, Map<String, AnnotationValue> fieldIgnoreMap) {
        List<String> ignores = AnnotationValueExtractUtil.getAnnotationValue_constantList(processingEnv, fieldIgnoreMap.get("value"));
        if (ignores != null) {
            for (String ignore : ignores) {
                AnnotationFieldIgnore fieldIgnoreData = new AnnotationFieldIgnore();
                fieldIgnoreData.setPackages(getAnnotationValue_constantList(processingEnv, fieldIgnoreMap.get("packages")));
                fieldIgnoreData.setTypes(getAnnotationValue_ClassList(processingEnv, fieldIgnoreMap.get("types")));
                fieldIgnoreData.setIgnored(AnnotationValueExtractUtil.getAnnotationValue_enum(processingEnv, fieldIgnoreMap.get("ignored"), IgnoreType.class));
                fieldIgnoreData.setValue(ignore);
                ret.add(fieldIgnoreData);
            }
        }
    }

    private static void resolveFieldMapping(List<AnnotationFieldMapping> ret, ProcessingEnvironment processingEnv, Map<String, AnnotationValue> fieldMappingMap, AnnotationMirror annotationMirror, Element element) {

        List<String> sValues = AnnotationValueExtractUtil.getAnnotationValue_constantList(processingEnv, fieldMappingMap.get("s"));
        List<String> dValues = AnnotationValueExtractUtil.getAnnotationValue_constantList(processingEnv, fieldMappingMap.get("d"));
        if (sValues == null) sValues = Collections.emptyList();
        if (dValues == null) dValues = Collections.emptyList();

        int size = Math.min(sValues.size(), dValues.size());
        if (sValues.size() != dValues.size()) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "Bad @FieldConfiguration(s="+sValues+".length="+sValues.size()+", d="+dValues+".length="+dValues.size()+") - where s and d are different sizes", element, annotationMirror, fieldMappingMap.get("s"));
        }

        for (int i=0; i<size; i++) {
            AnnotationFieldMapping fieldMappingData = new AnnotationFieldMapping();

            AnnotationFieldId s = new AnnotationFieldId();
            s.setPackages(getAnnotationValue_constantList(processingEnv, fieldMappingMap.get("sPackages")));
            s.setTypes(getAnnotationValue_ClassList(processingEnv, fieldMappingMap.get("sTypes")));
            s.setValue(sValues.get(i));
            fieldMappingData.setS(s);

            AnnotationFieldId d = new AnnotationFieldId();
            d.setPackages(getAnnotationValue_constantList(processingEnv, fieldMappingMap.get("dPackages")));
            d.setTypes(getAnnotationValue_ClassList(processingEnv, fieldMappingMap.get("dTypes")));
            d.setValue(dValues.get(i));
            fieldMappingData.setD(d);

            fieldMappingData.setMethodNameS2D(AnnotationValueExtractUtil.getAnnotationValue_constant(processingEnv, fieldMappingMap.get("methodNameS2D")));
            fieldMappingData.setMethodNameD2S(AnnotationValueExtractUtil.getAnnotationValue_constant(processingEnv, fieldMappingMap.get("methodNameD2S")));
            fieldMappingData.setIgnoreDirectionS2D(AnnotationValueExtractUtil.getAnnotationValue_constant(processingEnv, fieldMappingMap.get("ignoreDirectionS2D")));
            fieldMappingData.setIgnoreDirectionD2S(AnnotationValueExtractUtil.getAnnotationValue_constant(processingEnv, fieldMappingMap.get("ignoreDirectionD2S")));

            fieldMappingData.getApplyWhenS2D().addAll(AnnotationValueExtractUtil.getAnnotationValue_enumList(processingEnv, fieldMappingMap.get("applyWhenS2D"), ApplyFieldStrategy.class));
            fieldMappingData.getApplyWhenD2S().addAll(AnnotationValueExtractUtil.getAnnotationValue_enumList(processingEnv, fieldMappingMap.get("applyWhenD2S"), ApplyFieldStrategy.class));

            ret.add(fieldMappingData);
        }
    }


    public static boolean isConfiguredAsImmutableType(ProcessingEnvironment processingEnv, MapperClassInfo ownerClassInfo, TypeMirror type) {
        for (AnnotationMapperConfig annotationMapperConfig : resolveMapperConfigData(processingEnv, ownerClassInfo.parentElement)) {
            for (Type anotationTypeValue : annotationMapperConfig.getImmutable()) {
                if (TypeUtils.isSame(processingEnv, type, anotationTypeValue)) return true;
            }
        }

        return false;
    }
}

