package sk.annotation.library.jam.processor.data.confwrappers;

import com.sun.tools.javac.code.Type;
import sk.annotation.library.jam.annotations.Mapper;
import sk.annotation.library.jam.annotations.enums.ApplyFieldStrategy;
import sk.annotation.library.jam.annotations.enums.ConfigErrorReporting;
import sk.annotation.library.jam.annotations.enums.IgnoreType;
import sk.annotation.library.jam.processor.data.MapperClassInfo;
import sk.annotation.library.jam.processor.data.keys.MethodConfigKey;
import sk.annotation.library.jam.processor.utils.ElementUtils;
import sk.annotation.library.jam.processor.utils.TypeUtils;
import sk.annotation.library.jam.processor.utils.annotations.AnnotationValueUtils;
import sk.annotation.library.jam.processor.utils.annotations.data.AnnotationConfigGenerator;
import sk.annotation.library.jam.processor.utils.annotations.data.AnnotationMapperConfig;
import sk.annotation.library.jam.processor.utils.annotations.data.fields.AnnotationFieldId;
import sk.annotation.library.jam.processor.utils.annotations.data.fields.AnnotationFieldIgnore;
import sk.annotation.library.jam.processor.utils.annotations.data.fields.AnnotationFieldMapping;
import sk.annotation.library.jam.processor.utils.commons.ObjectUtils;
import sk.annotation.library.jam.processor.utils.commons.StringUtils;

import javax.annotation.processing.ProcessingEnvironment;
import java.util.*;
import java.util.regex.Pattern;

public class FieldConfigurationResolver {
    static public class FieldMapperWrapper {
        public final AnnotationFieldId a;
        public final AnnotationFieldId b;
        public final MapperConfWrapper fieldConfWrapper;
        public final boolean ignoreDirection;
        public final boolean directionSToD;
        public final String methodNameRequired;

        public FieldMapperWrapper(MapperConfWrapper fieldConfWrapper, AnnotationFieldId a, AnnotationFieldId b, boolean ignoreDirection, String methodDirection, boolean directionSToD) {
            this.a = a;
            this.b = b;
            this.fieldConfWrapper = fieldConfWrapper;
            this.ignoreDirection = ignoreDirection;
            this.methodNameRequired = methodDirection;
            this.directionSToD = directionSToD;
        }
    }

    static public class MapperConfWrapper {
        public final AnnotationFieldMapping fieldConf;
        public final FieldMapperWrapper directionFromSToD;
        public final FieldMapperWrapper directionFromDToS;

        public MapperConfWrapper(AnnotationFieldMapping fieldConf) {
            this.fieldConf = fieldConf;

            this.directionFromSToD = new FieldMapperWrapper(this, fieldConf.getS(), fieldConf.getD(), fieldConf.isIgnoreDirectionS2D(), fieldConf.getMethodNameS2D(), true);
            this.directionFromDToS = new FieldMapperWrapper(this, fieldConf.getD(), fieldConf.getS(), fieldConf.isIgnoreDirectionD2S(), fieldConf.getMethodNameD2S(), false);
        }
    }

    final private MapperClassInfo ownerClassInfo;
    final private MethodConfigKey forMethodConfig;
    final private List<FieldMapperWrapper> customFieldMapping = new LinkedList<>();
    final private Mapper jamMapper;

    final private List<AnnotationMapperConfig> mapperConfigs;

    public FieldConfigurationResolver(ProcessingEnvironment processingEnv, MapperClassInfo ownerClassInfo, MethodConfigKey forMethodConfig) {
        if (ownerClassInfo == null || forMethodConfig == null) throw new IllegalStateException();
        this.forMethodConfig = forMethodConfig;
        this.ownerClassInfo = ownerClassInfo;
        this.jamMapper = ownerClassInfo.getJamMapperConfig();

        mapperConfigs = AnnotationValueUtils.resolveMapperConfigData(processingEnv, forMethodConfig.getMethod());

        for (AnnotationMapperConfig mapperConf : mapperConfigs) {
            if (mapperConf == null) continue;

            // 1) Custom Fields !!!
            for (AnnotationFieldMapping fieldMappingData : mapperConf.getFieldMapping()) {
                MapperConfWrapper mapperConfWrapper = new MapperConfWrapper(fieldMappingData);
                registerFieldMapperWrapper(mapperConfWrapper.directionFromSToD);
                registerFieldMapperWrapper(mapperConfWrapper.directionFromDToS);
            }
        }
    }

    protected void registerFieldMapperWrapper(FieldMapperWrapper field) {
        if (field == null) return;

        customFieldMapping.add(field);
    }

    static public class ResolvedTransformation {
        final private String key;

        public ResolvedTransformation(String key) {
            this.key = key;
        }

        List<FieldValueAccessData> pathFrom;
        List<FieldValueAccessData> pathTo;

        public List<FieldMappingData> fieldMappingData = new LinkedList<>();

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ResolvedTransformation that = (ResolvedTransformation) o;
            return Objects.equals(key, that.key) && Objects.equals(pathFrom, that.pathFrom) && Objects.equals(pathTo, that.pathTo) && Objects.equals(fieldMappingData, that.fieldMappingData);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key, pathFrom, pathTo, fieldMappingData);
        }

        public String getKey() {
            return key;
        }

        public List<FieldValueAccessData> getPathFrom() {
            return pathFrom;
        }

        public void setPathFrom(List<FieldValueAccessData> pathFrom) {
            this.pathFrom = pathFrom;
        }

        public List<FieldValueAccessData> getPathTo() {
            return pathTo;
        }

        public void setPathTo(List<FieldValueAccessData> pathTo) {
            this.pathTo = pathTo;
        }

        public List<FieldMappingData> getFieldMappingData() {
            return fieldMappingData;
        }

        public void setFieldMappingData(List<FieldMappingData> fieldMappingData) {
            this.fieldMappingData = fieldMappingData;
        }
    }
	/*
	Obj1.d = Obj2.d
	Obj1.s = Obj2.s
	Obj1.firstname = Obj2.user.firstname
	Obj1.surname = Obj2.user.surname
	Obj1.position = Obj2.lastWork.position


	=> for method Obj1=>Obj2 are detected followed ResolvedTransformation-s:
	1. ResolvedTransformation (typeFrom=Obj1.class, typeTo=Obj2.class, pathFrom=null, pathTo=null,
		fieldMappingData=[d=d, s=s, *=*]}
	2. ResolvedTransformation (typeFrom=Obj1.class, typeTo=Obj2.User.class, pathFrom=null, pathTo=FieldValueAccessData(getUser(), setUser()),
		fieldMappingData=[firstname=firstname, surname=surname, *=*]}
	3. ResolvedTransformation (typeFrom=Obj1.class, typeTo=Obj2.Position.class, pathFrom=null, pathTo=FieldValueAccessData(getLastWork(), setLastWork()),
		fieldMappingData=[position=position, *=*]}
	*/

    public List<ResolvedTransformation> findTransformationGroups(ProcessingEnvironment processingEnv, Type typeFrom, Type typeTo) {
        List<ResolvedTransformation> retValues = new LinkedList<>();

        Set<String> resolvedCustomFields = new HashSet<>();

        Map<String, FieldValueAccessData> srcFields = ElementUtils.findAllAccesableFields(processingEnv, typeFrom);
        Map<String, FieldValueAccessData> dstFields = ElementUtils.findAllAccesableFields(processingEnv, typeTo);
        boolean sameTypes = TypeUtils.isSameType(processingEnv, typeFrom, typeTo);
        Set<String> unusedSourceFields = new HashSet<>(srcFields.keySet());
        Set<String> unusedDestinationFields = new HashSet<>(dstFields.keySet());

        // Find custom fields
        for (FieldMapperWrapper customField : customFieldMapping) {
            if (!customField.a.isTypeAcceptable(processingEnv, typeFrom)) continue; // ignore not appliable customFieldMapping
            if (!customField.b.isTypeAcceptable(processingEnv, typeTo)) continue; // ignore not appliable customFieldMapping

            if (sameTypes && !customField.directionSToD) continue;

            // try find
            List<FieldValueAccessData> srcFieldPathList = findFieldPath(processingEnv, typeFrom, srcFields, customField.a);
            String srcPathKey = createKeyForFieldPath(srcFieldPathList);
            if (srcFieldPathList == null || srcFieldPathList.isEmpty() || StringUtils.isEmpty(srcPathKey)) continue;
            String srcName = srcFieldPathList.get(0).getFieldName();
            unusedSourceFields.remove(srcName);

            List<FieldValueAccessData> dstFieldPathList = findFieldPath(processingEnv, typeTo, dstFields, customField.b);
            String dstPathKey = createKeyForFieldPath(dstFieldPathList);
            if (dstFieldPathList == null || dstFieldPathList.isEmpty() || StringUtils.isEmpty(dstPathKey)) continue;
            String dstName = dstFieldPathList.get(0).getFieldName();
            unusedDestinationFields.remove(dstName);

            String key = createKeyConfig(srcFieldPathList, dstFieldPathList, true);
            if (resolvedCustomFields.contains(key)) continue;
            resolvedCustomFields.add(key);

            // if it should be ignored, it needs to be checked
            if (customField.ignoreDirection) continue;
            if (isIgnoredPath(processingEnv, typeFrom, srcFieldPathList, false)) continue;
            if (isIgnoredPath(processingEnv, typeTo, dstFieldPathList, true)) continue;
//            if (isIgnoredKey(srcFieldConfigMap, srcPathKey)) continue;
//            if (isIgnoredKey(dstFieldConfigMap, dstPathKey)) continue;

            // Removing from unused properties
            if (sameTypes && !StringUtils.equals(srcName, dstName)) {
                unusedSourceFields.remove(dstName);
            }

            ResolvedTransformation group = findResolvedTransformation(retValues, srcFieldPathList, dstFieldPathList);

            FieldMappingData mappingData = new FieldMappingData();
            mappingData.setSrc(srcFieldPathList.get(srcFieldPathList.size() - 1));
            mappingData.setDst(dstFieldPathList.get(dstFieldPathList.size() - 1));
            mappingData.setMethodNameRequired(customField.methodNameRequired);
            if (!mappingData.isWithoutProblemOrNotIgnored()) {
                mappingData.setSrcConfigErrorReportingLevel(resolveReportPriority(processingEnv, typeFrom, srcName, true));
                mappingData.setDstConfigErrorReportingLevel(resolveReportPriority(processingEnv, typeTo, dstName, false));
            }
            mappingData.setFieldStrategy(findFieldStrategyForMapping(processingEnv, typeFrom, srcFieldPathList, typeTo, dstFieldPathList));
            group.fieldMappingData.add(mappingData);
        }

        // nakonci uz iba default values
        TreeSet<String> orderedUnusedKeys = new TreeSet<>();
        orderedUnusedKeys.addAll(unusedSourceFields);
        orderedUnusedKeys.addAll(unusedDestinationFields);
        if (!orderedUnusedKeys.isEmpty()) {
            ResolvedTransformation group = findResolvedTransformation(retValues, Collections.singletonList(null), Collections.singletonList(null));
            for (String orderedUnusedKey : orderedUnusedKeys) {
                // add to fieldMappingData
                FieldMappingData mappingData = new FieldMappingData();
//                mappingData.setSrcIgnored(isIgnoredKey(srcFieldConfigMap, orderedUnusedKey));
//                mappingData.setDstIgnored(isIgnoredKey(dstFieldConfigMap, orderedUnusedKey));
                mappingData.setSrc(srcFields.get(orderedUnusedKey));
                mappingData.setDst(dstFields.get(orderedUnusedKey));

                List<FieldValueAccessData> srcFieldPathList = Collections.singletonList(mappingData.getSrc());
                List<FieldValueAccessData> dstFieldPathList = Collections.singletonList(mappingData.getDst());
                mappingData.setSrcIgnored(isIgnoredPath(processingEnv, typeFrom, srcFieldPathList, false));
                mappingData.setDstIgnored(isIgnoredPath(processingEnv, typeTo, dstFieldPathList, true));
                if (!mappingData.isWithoutProblemOrNotIgnored()) {
                    mappingData.setSrcConfigErrorReportingLevel(resolveReportPriority(processingEnv, typeFrom, orderedUnusedKey, true));
                    mappingData.setDstConfigErrorReportingLevel(resolveReportPriority(processingEnv, typeTo, orderedUnusedKey, false));
                }
                mappingData.setFieldStrategy(findFieldStrategyForMapping(processingEnv, typeFrom, srcFieldPathList, typeTo, dstFieldPathList));
                group.fieldMappingData.add(mappingData);
            }
        }


        // Sort configuration ...
        Collections.sort(retValues, resolvedTransformationComparator);
        for (ResolvedTransformation retValue : retValues) {
            Collections.sort(retValue.fieldMappingData, fieldMappingDataComparator);
        }


        return retValues;
    }

    private static Comparator<ResolvedTransformation> resolvedTransformationComparator = new Comparator<ResolvedTransformation>() {
        private String getCmpKey(ResolvedTransformation o1) {
            return o1.key + " ";
        }

        @Override
        public int compare(ResolvedTransformation o1, ResolvedTransformation o2) {
            return getCmpKey(o1).compareTo(getCmpKey(o2));
        }
    };
    private static Comparator<FieldMappingData> fieldMappingDataComparator = new Comparator<FieldMappingData>() {
        private void update(StringBuilder sb, FieldValueAccessData o1) {
            if (o1 == null) sb.append("???");
            else sb.append(o1.getFieldName());
        }

        private String getKey(FieldMappingData o1) {
            StringBuilder sb = new StringBuilder();
            update(sb, ObjectUtils.firstNonNull(o1.getDst(), o1.getSrc()));
            sb.append(" = ");
            update(sb, ObjectUtils.firstNonNull(o1.getSrc(), o1.getDst()));
            return sb.toString();
        }

        @Override
        public int compare(FieldMappingData o1, FieldMappingData o2) {
            return getKey(o1).compareTo(getKey(o2));
        }
    };


    private AnnotationConfigGenerator findConfigGenerator(ProcessingEnvironment processingEnv, Type type, String key) {
        if (type == null || key == null) return null;
        Map<String, FieldValueAccessData> fields = ElementUtils.findAllAccesableFields(processingEnv, type);

        // 1) Try find Fields or ClassName
        for (AnnotationMapperConfig mapperConf : mapperConfigs) {
            if (mapperConf == null) continue;

            for (AnnotationConfigGenerator confGenerator : mapperConf.getConfig()) {
                // Field configuration
                List<FieldValueAccessData> fieldPath = findFieldPath(processingEnv, type, fields, confGenerator.getFieldId(), false);
                if (fieldPath != null && !fieldPath.isEmpty()) {
                    if (fieldPath.size() != 1) continue;

                    // ClassName or FullCannonicalName matches
                    if (fieldPath.get(0) == null) return confGenerator;

                    // If field matches too
                    if (StringUtils.equals(key, fieldPath.get(0).getFieldName())) return confGenerator;

                    continue;
                }
            }
        }

//        // 1) Try starts with package
//        String fullName = ElementUtils.getQualifiedName(type.asElement());
//        AnnotationConfigGenerator bestMatch = null;
//        for (AnnotationMapperConfig mapperConf : mapperConfigs) {
//            if (mapperConf == null) continue;
//
//            for (AnnotationConfigGenerator confGenerator : mapperConf.getConfig()) {
//                // Default - ignore in first time
//                if (!StringUtils.isEmpty(confGenerator.getFieldId().getValue()) && !StringUtils.startsWith(fullName, confGenerator.getFieldId().getValue()))
//                    continue;
//
//                if (bestMatch == null || bestMatch.getFieldId().getValue().length() < confGenerator.getFieldId().getValue().length()) {
//                    bestMatch = confGenerator;
//                }
//            }
//        }
        return null;
    }

    private ConfigErrorReporting resolveReportPriority(ProcessingEnvironment processingEnv, Type type, String key, boolean asSrc) {
        if (type == null || key == null) return findDefautlReport();

        AnnotationConfigGenerator confGenerator = findConfigGenerator(processingEnv, type, key);
        if (confGenerator != null) {
            if (asSrc && confGenerator.getMissingAsSource() != null) return confGenerator.getMissingAsSource();
            if (!asSrc && confGenerator.getMissingAsDestination() != null) return confGenerator.getMissingAsDestination();
            return findDefautlReport();
        }


        return findDefautlReport();
    }

    private ConfigErrorReporting findDefautlReport() {
        if (jamMapper != null && jamMapper.defaultErrorConfig() != null) return jamMapper.defaultErrorConfig();
        return ConfigErrorReporting.WARNINGS_ONLY;
    }

    private boolean isIgnoredPath(ProcessingEnvironment processingEnv, Type type, List<FieldValueAccessData> srcFieldPathList, boolean forUpdate) {
        if (srcFieldPathList == null || srcFieldPathList.isEmpty() || srcFieldPathList.get(0) == null) return true;

        for (AnnotationMapperConfig fieldConfig : mapperConfigs) {
            for (AnnotationFieldIgnore fieldIgnore : fieldConfig.getFieldIgnore()) {
                if (!fieldIgnore.isTypeAcceptable(processingEnv, type)) continue;
                if (!StringUtils.equals(fieldIgnore.getValue(), srcFieldPathList.get(0).getFieldName())) continue;

                if (fieldIgnore.getIgnored() == IgnoreType.IGNORE_ALL) return true;
                if (forUpdate) return fieldIgnore.getIgnored() == IgnoreType.IGNORE_WRITE;
                return fieldIgnore.getIgnored() == IgnoreType.IGNORE_READ;
            }
        }

        return false;
    }

    private ApplyFieldStrategy findFieldStrategyForMapping(ProcessingEnvironment processingEnv, Type srcType, List<FieldValueAccessData> srcFieldPathList, Type dstType, List<FieldValueAccessData> dstFieldPathList) {
        if (srcFieldPathList == null || srcFieldPathList.isEmpty() || srcFieldPathList.get(0) == null) return ApplyFieldStrategy.ALWAYS;
        if (dstFieldPathList == null || dstFieldPathList.isEmpty() || dstFieldPathList.get(0) == null) return ApplyFieldStrategy.ALWAYS;

        List<ApplyFieldStrategy> values = null;
        for (AnnotationMapperConfig fieldConfig : mapperConfigs) {

            // Finding from field mapping
            for (AnnotationFieldMapping fieldMapping : fieldConfig.getFieldMapping()) {
                if (!fieldMapping.isIgnoreDirectionS2D() && fieldMapping.getS().isAcceptableTypeAndName(processingEnv, srcType, srcFieldPathList)
                        && fieldMapping.getD().isAcceptableTypeAndName(processingEnv, dstType, dstFieldPathList)) {
                    values = fieldMapping.getApplyWhenS2D();
                    if (values != null && !values.isEmpty()) break;
                } else if (!fieldMapping.isIgnoreDirectionD2S() && fieldMapping.getD().isAcceptableTypeAndName(processingEnv, srcType, srcFieldPathList)
                        && fieldMapping.getS().isAcceptableTypeAndName(processingEnv, dstType, dstFieldPathList)) {
                    values = fieldMapping.getApplyWhenD2S();
                    if (values != null && !values.isEmpty()) break;
                }
            }

            if (values != null && !values.isEmpty()) break;

            // Mapping from methodOrMapperConfig
            if (fieldConfig.getApplyWhen() != null && !fieldConfig.getApplyWhen().isEmpty()) {
                values = fieldConfig.getApplyWhen();
                break;
            }
        }

        if (values != null && !values.isEmpty()) {
            if (values.contains(ApplyFieldStrategy.ALWAYS)) return ApplyFieldStrategy.ALWAYS;
            if (values.contains(ApplyFieldStrategy.OLDVALUE_IS_NULL)) return ApplyFieldStrategy.OLDVALUE_IS_NULL;
            if (values.contains(ApplyFieldStrategy.NEWVALUE_IS_NOT_NULL)) return ApplyFieldStrategy.NEWVALUE_IS_NOT_NULL;
        }

        return ApplyFieldStrategy.ALWAYS;
    }


    static private String createKeyForFieldPath(List<FieldValueAccessData> fields) {
        if (fields == null || fields.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (FieldValueAccessData field : fields) {
            if (sb.length() > 0) sb.append(".");
            if (field != null) {
                sb.append(field.getFieldName());
            }
        }
        return sb.toString();
    }


    static private ResolvedTransformation findResolvedTransformation(List<ResolvedTransformation> retValues, List<FieldValueAccessData> fieldPathListA, List<FieldValueAccessData> fieldPathListB) {
        String keyOfResolvedTransformation = createKeyConfig(fieldPathListA, fieldPathListB, false);

        for (ResolvedTransformation retValue : retValues) {
            if (StringUtils.equals(retValue.key, keyOfResolvedTransformation)) return retValue;
        }

        // Create new type
        ResolvedTransformation ret = new ResolvedTransformation(keyOfResolvedTransformation);
        ret.pathFrom = copyParentPathList(fieldPathListA);
        ret.pathTo = copyParentPathList(fieldPathListB);
        retValues.add(ret);
        return ret;
    }


    /*
    Value variants for fieldNameInConfiguration:
      "fieldName";										// Example of simple common fieldName
      "Object1.fieldName";								// fieldName with ObjectOwner (without full path)
      "Object1.NestedObject.fieldName";					// fieldName with ObjectOwner for nestedObject (without full path)
      "org.data.Object1.fieldName";						// fieldName with ObjectOwner (with package)
      "org.data.Object1.NestedObject.fieldName";		// fieldName with ObjectOwner for nestedObject (with package)

      "fieldName.subObject";										// Example of composite fieldName
      "Object1.fieldName.subObject";								// composite fieldName with ObjectOwner (without full path)
      "Object1.NestedObject.fieldName.subObject";					// composite fieldName with ObjectOwner for nestedObject (without full path)
      "org.data.Object1.fieldName.subObject";						// composite fieldName with ObjectOwner (with package)
      "org.data.Object1.NestedObject.fieldName.subObject";			// composite fieldName with ObjectOwner for nestedObject (with package)

    */
    static List<FieldValueAccessData> findFieldPath(ProcessingEnvironment processingEnv, Type type, Map<String, FieldValueAccessData> fieldsMap, AnnotationFieldId fieldNameInConfiguration) {
        return findFieldPath(processingEnv, type, fieldsMap, fieldNameInConfiguration, true);
    }

    static List<FieldValueAccessData> findFieldPath(ProcessingEnvironment processingEnv, Type type, Map<String, FieldValueAccessData> fieldsMap, AnnotationFieldId fieldId, boolean fieldNameRequired) {
        // check type is accepatble
        if (!fieldId.isTypeAcceptable(processingEnv, type)) return null;

        // Try Prefix for fullClassName
        String fieldNameInConfiguration = fieldId.getValue();
        if (fieldNameInConfiguration == null) return null;
        if (fieldNameRequired && StringUtils.isEmpty(fieldNameInConfiguration)) return null;

        if (fieldsMap.containsKey(fieldNameInConfiguration))
            return Collections.singletonList(fieldsMap.get(fieldNameInConfiguration));

        String[] splitedFieldName = fieldNameInConfiguration.split(Pattern.quote("."));

        List<FieldValueAccessData> ret = new ArrayList<>(splitedFieldName.length);
        for (int i = 0; i < splitedFieldName.length; i++) {
            String simpleFieldName = splitedFieldName[i];
            if (fieldNameRequired && StringUtils.isEmpty(simpleFieldName)) return null;

            FieldValueAccessData fieldValueAccessData = fieldsMap.get(simpleFieldName);
            if (fieldNameRequired && fieldValueAccessData == null) return null;
            ret.add(fieldValueAccessData);

            if (fieldValueAccessData != null && i < splitedFieldName.length - 1) {
                if (fieldValueAccessData.getTypeOfGetter() instanceof Type) {
                    type = (Type) fieldValueAccessData.getTypeOfGetter();
                    fieldsMap = ElementUtils.findAllAccesableFields(processingEnv, type);
                    continue;
                }
                if (fieldValueAccessData.getTypeOfSetter() instanceof Type) {
                    type = (Type) fieldValueAccessData.getTypeOfSetter();
                    fieldsMap = ElementUtils.findAllAccesableFields(processingEnv, type);
                    continue;
                }
                return null;
            }
        }
        return ret;
    }

//    static String removeTypeFromFieldConfiguration(Type type, String fieldNameInConfiguration) {
//        // 1) Try to remove type.getCannonicalName() from fieldDefinitionName
//        String fullClassName = ElementUtils.getQualifiedName(type.asElement());
//        if (StringUtils.startsWith(fieldNameInConfiguration, fullClassName)) {
//            return StringUtils.substring(fieldNameInConfiguration, fullClassName.length() + 1);
//        }
//
//        // 2) Try to remove ObjectName (without packageName)
//        String packageName = TypeUtils.findPackageName(type);
//        if (StringUtils.isNotEmpty(packageName)) {
//            String fieldNameInConfigurationWithPackageName = packageName + "." + fieldNameInConfiguration;
//            if (StringUtils.startsWith(fieldNameInConfigurationWithPackageName, fullClassName)) {
//                return StringUtils.substring(fieldNameInConfigurationWithPackageName, fullClassName.length() + 1);
//            }
//        }
//
//        return fieldNameInConfiguration;
//    }

    static String createKeyConfig(List<FieldValueAccessData> src, List<FieldValueAccessData> dst, boolean addLastFieldName) {
        StringBuilder sb = new StringBuilder();
        appendKeyConfig(sb, src, addLastFieldName, "");
        appendKeyConfig(sb, dst, addLastFieldName, "=");
        return sb.toString();
    }

    static void appendKeyConfig(StringBuilder sb, List<FieldValueAccessData> src, boolean addLastFieldName, String separator) {
        if (src != null && !src.isEmpty()) {
            int maxSize = src.size();
            if (!addLastFieldName) maxSize--;
            for (int i = 0; i < maxSize; i++) {
                FieldValueAccessData fieldValueAccessData = src.get(i);
                sb.append(separator);
                if (fieldValueAccessData != null) sb.append(fieldValueAccessData.getField());
                separator = ".";
            }
        }
        if (!addLastFieldName) sb.append(separator).append("*");

    }

    static List<FieldValueAccessData> copyParentPathList(List<FieldValueAccessData> vals) {
        if (vals == null || vals.size() < 2) return null;
        List<FieldValueAccessData> ret = new ArrayList<>(vals.size() - 1);
        for (int i = 0; i < vals.size() - 1; i++) {
            ret.add(vals.get(i));
        }
        return ret;
    }
}


//	public MapperConfWrapper findFieldConfiguration(FieldValueAccessData sourceField) {
//
//	}

//	private void registerNewMapperConfWrapper(FieldMapping[] fields, int level) {
//		if (fields==null) return;
//	}
//	private void registerConfWrapper(FieldMapperWrapper fieldMapperWrapper) {
//		if (fieldMapperWrapper == null) return;
//
//		// find exists direction (the same d,s)
//
//		for (FieldMapperWrapper customField : customFieldMapping) {
//
//			// check type
//			if (!StringUtils.equals(customField.d, fieldMapperWrapper.d)) continue;
//			if (!StringUtils.equals(customField.s, fieldMapperWrapper.s)) continue;
//
//			// if found => cannot add next fieldConfiguration (this configuration has worst priority)
//			return;
//		}
//
//		customFieldMapping.add(fieldMapperWrapper);
//	}

