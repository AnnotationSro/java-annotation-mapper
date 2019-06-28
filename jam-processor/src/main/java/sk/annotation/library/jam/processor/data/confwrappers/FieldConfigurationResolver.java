package sk.annotation.library.jam.processor.data.confwrappers;

import com.sun.tools.javac.code.Type;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import sk.annotation.library.jam.annotations.*;
import sk.annotation.library.mapper.jam.annotations.*;
import sk.annotation.library.jam.annotations.enums.ConfigErrorReporting;
import sk.annotation.library.jam.processor.utils.ElementUtils;
import sk.annotation.library.jam.processor.utils.TypeUtils;

import javax.annotation.processing.ProcessingEnvironment;
import java.util.*;
import java.util.regex.Pattern;

public class FieldConfigurationResolver {
	static public class FieldMapperWrapper {
		public final String a;
		public final String b;
		public final MapperConfWrapper fieldConfWrapper;
		public final boolean ignoreDirection;
		public final boolean directionSToD;
		public final String methodNameRequired;

		public FieldMapperWrapper(MapperConfWrapper fieldConfWrapper, String a, String b, boolean ignoreDirection, String methodDirection, boolean directionSToD) {
			this.a = a;
			this.b = b;
			this.fieldConfWrapper = fieldConfWrapper;
			this.ignoreDirection = ignoreDirection;
			this.methodNameRequired = methodDirection;
			this.directionSToD = directionSToD;
		}
	}
	static public class MapperConfWrapper {
		public final FieldMapping fieldConf;
		public final FieldMapperWrapper directionFromSToD;
		public final FieldMapperWrapper directionFromDToS;

		public MapperConfWrapper(FieldMapping fieldConf) {
			this.fieldConf= fieldConf;
			this.directionFromSToD = new FieldMapperWrapper(this, fieldConf.s(), fieldConf.d(), fieldConf.ignoreDirectionS2D(), fieldConf.methodNameS2D(), true);
			this.directionFromDToS = new FieldMapperWrapper(this, fieldConf.d(), fieldConf.s(), fieldConf.ignoreDirectionD2S(), fieldConf.methodNameD2S(), false);
		}
	}

	final private List<FieldMapperWrapper> customFieldMapping = new LinkedList<>();
	final private List<MapperFieldConfig> configurations;
	final private JamMapper jamMapper;

	public FieldConfigurationResolver (JamMapper jamMapper, List<MapperFieldConfig> configurations) {
		if (configurations == null) configurations = Collections.emptyList();

		this.jamMapper = jamMapper;
		this.configurations = configurations;
		if (configurations.isEmpty()) return;

		for (MapperFieldConfig mapperConf : configurations) {
			if (mapperConf == null) continue;

			// 1) Custom Fields !!!
			FieldMapping[] fields = mapperConf.fieldMapping();
			if (fields!=null) {
				for (FieldMapping field : fields) {
					MapperConfWrapper mapperConfWrapper = new MapperConfWrapper(field);
					registerFieldMapperWrapper(mapperConfWrapper.directionFromSToD);
					registerFieldMapperWrapper(mapperConfWrapper.directionFromDToS);
				}
			}

//			// 2) Ignored Fields !!!
//			FieldIgnore[] fieldIgnores = mapperConf.fieldIgnore();
//			if (fieldIgnores !=null) {
//				for (FieldIgnore fieldIgnore : fieldIgnores) {
//					if (fieldIgnore ==null) continue;
//					customFieldIgnores.add(fieldIgnore);
//				}
//			}
//
//			// 3) Generator config !!!
//			ConfigGenerator[] configs = mapperConf.config();
//			if (configs!=null) {
//				for (ConfigGenerator conf: configs) {
//					if (conf ==null) continue;
//					customConfigGenerator.add(conf);
//				}
//			}
//

		}
	}

	protected void registerFieldMapperWrapper(FieldMapperWrapper field) {
		if (field == null) return;

		customFieldMapping.add(field);
	}

	@Data
	static public class ResolvedTransformation {
		final private String key;
		public ResolvedTransformation(String key) {
			this.key = key;
		}

		List<FieldValueAccessData> pathFrom;
		List<FieldValueAccessData> pathTo;

		public List<FieldMappingData> fieldMappingData = new LinkedList<>();
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
		boolean sameTypes = processingEnv.getTypeUtils().isSameType(typeFrom, typeTo);
		Set<String> unusedSourceFields = new HashSet<>(srcFields.keySet());
		Set<String> unusedDestinationFields = new HashSet<>(dstFields.keySet());

		Map<String, FieldIgnore> srcFieldConfigMap = resolveFieldConfigsMap(processingEnv, typeFrom, srcFields);
		Map<String, FieldIgnore> dstFieldConfigMap = resolveFieldConfigsMap(processingEnv, typeTo, dstFields);

		// Find custom fields
		for (FieldMapperWrapper customField : customFieldMapping) {

			if (sameTypes && !customField.directionSToD) continue;

			// try find
			List<FieldValueAccessData> srcFieldPathList = findFieldPath(processingEnv, typeFrom, srcFields, customField.a);
			String srcPathKey = createKeyForFieldPath(srcFieldPathList);
			if (srcFieldPathList == null || srcFieldPathList.isEmpty() || srcPathKey==null) continue;

			List<FieldValueAccessData> dstFieldPathList = findFieldPath(processingEnv, typeTo, dstFields, customField.b);
			String dstPathKey = createKeyForFieldPath(dstFieldPathList);
			if (dstFieldPathList == null || dstFieldPathList.isEmpty() || dstPathKey==null) continue;

			String key = createKeyConfig(srcFieldPathList, dstFieldPathList, true);
			if (resolvedCustomFields.contains(key)) continue;
			resolvedCustomFields.add(key);

			// if it should be ignored, it needs to be checked
			if (customField.ignoreDirection) continue;
			if (isIgnoredKey(srcFieldConfigMap, srcPathKey)) continue;
			if (isIgnoredKey(dstFieldConfigMap, dstPathKey)) continue;

			// Removing from unused properties
			String srcName = srcFieldPathList.get(0).getFieldName();
			String dstName = dstFieldPathList.get(0).getFieldName();
			unusedSourceFields.remove(srcName);
			unusedDestinationFields.remove(dstName);
			if (sameTypes && !StringUtils.equals(srcName, dstName)) {
				unusedSourceFields.remove(dstName);
			}

			ResolvedTransformation group = findResolvedTransformation(retValues, srcFieldPathList, dstFieldPathList);

			FieldMappingData mappingData = new FieldMappingData();
			mappingData.setSrc(srcFieldPathList.get(srcFieldPathList.size()-1));
			mappingData.setDst(dstFieldPathList.get(dstFieldPathList.size()-1));
			mappingData.setMethodNameRequired(customField.methodNameRequired);
			if (!mappingData.isWithoutProblemOrNotIgnored()) {
				mappingData.setSrcConfigErrorReportingLevel(resolveReportPriority(processingEnv, typeFrom, srcName, true));
				mappingData.setDstConfigErrorReportingLevel(resolveReportPriority(processingEnv, typeTo, dstName, false));
			}
			group.fieldMappingData.add(mappingData);
		}

		// nakonci uz iba default values
		TreeSet<String> orderedUnusedKeys = new TreeSet<>();
		orderedUnusedKeys.addAll(unusedSourceFields);
		orderedUnusedKeys.addAll(unusedDestinationFields);
		if (!orderedUnusedKeys.isEmpty()) {
			ResolvedTransformation group = findResolvedTransformation(retValues, Collections.singletonList(null), Collections.singletonList(null));
			for (String orderedUnusedKey : orderedUnusedKeys) {
				FieldMappingData mappingData = new FieldMappingData();
				mappingData.setSrcIgnored(isIgnoredKey(srcFieldConfigMap, orderedUnusedKey));
				mappingData.setDstIgnored(isIgnoredKey(dstFieldConfigMap, orderedUnusedKey));
				mappingData.setSrc(srcFields.get(orderedUnusedKey));
				mappingData.setDst(dstFields.get(orderedUnusedKey));
				if (!mappingData.isWithoutProblemOrNotIgnored()) {
					mappingData.setSrcConfigErrorReportingLevel(resolveReportPriority(processingEnv, typeFrom, orderedUnusedKey, true));
					mappingData.setDstConfigErrorReportingLevel(resolveReportPriority(processingEnv, typeTo, orderedUnusedKey, false));
				}
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


	private ConfigGenerator findConfigGenerator(ProcessingEnvironment processingEnv, Type type, String key) {
		if (type == null || key == null) return null;
		Map<String, FieldValueAccessData> fields = ElementUtils.findAllAccesableFields(processingEnv, type);

		// 1) Try find Fields or ClassName
		for (MapperFieldConfig mapperConf : configurations) {
			if (mapperConf == null) continue;

			for (ConfigGenerator confGenerator : mapperConf.config()) {
				// Default - ignore in first time
				if (StringUtils.isEmpty(confGenerator.beanOrField())) continue;

				// Field configuration
				List<FieldValueAccessData> fieldPath = findFieldPath(processingEnv, type, fields, confGenerator.beanOrField(), false);
				if (fieldPath!=null && !fieldPath.isEmpty()) {
					if (fieldPath.size()!=1) continue;

					// ClassName or FullCannonicalName matches
					if (fieldPath.get(0) == null) return confGenerator;

					// If field matches too
					if (StringUtils.equals(key, fieldPath.get(0).getFieldName())) return confGenerator;

					continue;
				}
			}
		}

		// 1) Try starts with package
		String fullName = ElementUtils.getQualifiedName(type.asElement());
		ConfigGenerator bestMatch = null;
		for (MapperFieldConfig mapperConf : configurations) {
			if (mapperConf == null) continue;

			for (ConfigGenerator confGenerator : mapperConf.config()) {
				// Default - ignore in first time
				if (!StringUtils.isEmpty(confGenerator.beanOrField()) && !StringUtils.startsWith(fullName, confGenerator.beanOrField())) continue;

				if (bestMatch == null || bestMatch.beanOrField().length() < confGenerator.beanOrField().length()) {
					bestMatch = confGenerator;
				}
			}
		}
		return bestMatch;
	}


	private ConfigErrorReporting resolveReportPriority(ProcessingEnvironment processingEnv, Type type, String key, boolean asSrc) {
		if (type == null || key == null) return findDefautlReport();

		ConfigGenerator confGenerator = findConfigGenerator(processingEnv, type, key);
		if (confGenerator !=null) {
			if (asSrc && confGenerator.missingAsSource()!=null) return confGenerator.missingAsSource();
			if (!asSrc && confGenerator.missingAsDestination()!=null) return confGenerator.missingAsDestination();
			return findDefautlReport();
		}


		return findDefautlReport();
	}
	private ConfigErrorReporting findDefautlReport() {
		if (jamMapper!=null && jamMapper.defaultErrorConfig()!=null) return jamMapper.defaultErrorConfig();
		return ConfigErrorReporting.WARNINGS_ONLY;
	}

	private boolean isIgnoredKey(Map<String, FieldIgnore> srcFieldConfigMap, String key) {
		if (key == null) return true;
		if (srcFieldConfigMap.containsKey(key)) {
			return srcFieldConfigMap.get(key).ignored();
		}
		return false;
	}


	private Map<String, FieldIgnore> resolveFieldConfigsMap(ProcessingEnvironment processingEnv, Type type, Map<String, FieldValueAccessData> fieldsMap) {
		Map<String, FieldIgnore> ret = new HashMap<>();

		// Resolve Ignored Fields
		for (MapperFieldConfig mapperConf : configurations) {
			if (mapperConf == null) continue;
			if (mapperConf.fieldIgnore() == null) continue;

			for (FieldIgnore fieldIgnore : mapperConf.fieldIgnore()) {
				List<FieldValueAccessData> ignoredFieldPathList = findFieldPath(processingEnv, type, fieldsMap, fieldIgnore.value());
				if (ignoredFieldPathList == null || ignoredFieldPathList.isEmpty()) continue;

				String pathKey = createKeyForFieldPath(ignoredFieldPathList);
				if (pathKey == null || pathKey.length() == 0) continue;

				if (ret.containsKey(pathKey)) continue;
				ret.put(pathKey, fieldIgnore);
			}
		}

		return ret;
	}
	static private String createKeyForFieldPath(List<FieldValueAccessData> fields) {
		if (fields == null || fields.isEmpty()) return "";
		StringBuilder sb = new StringBuilder();
		for (FieldValueAccessData field : fields) {
			if (sb.length()>0) sb.append(".");
			if (field!=null) {
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
	static List<FieldValueAccessData> findFieldPath(ProcessingEnvironment processingEnv, Type type, Map<String, FieldValueAccessData> fieldsMap, String fieldNameInConfiguration) {
		return findFieldPath(processingEnv, type, fieldsMap, fieldNameInConfiguration, true);
	}
	static List<FieldValueAccessData> findFieldPath(ProcessingEnvironment processingEnv, Type type, Map<String, FieldValueAccessData> fieldsMap, String fieldNameInConfiguration, boolean fieldNameRequired) {
		if (fieldsMap.containsKey(fieldNameInConfiguration)) return Collections.singletonList(fieldsMap.get(fieldNameInConfiguration));


		// Try Prefix for fullClassName
		fieldNameInConfiguration = removeTypeFromFieldConfiguration(type, fieldNameInConfiguration);
		if (fieldNameInConfiguration == null) return null;
		if (fieldNameRequired && StringUtils.isEmpty(fieldNameInConfiguration)) return null;

		String[] splitedFieldName = fieldNameInConfiguration.split(Pattern.quote("."));

		List<FieldValueAccessData> ret = new ArrayList<>(splitedFieldName.length);
		for (int i=0; i<splitedFieldName.length; i++) {
			String simpleFieldName = splitedFieldName[i];
			if (fieldNameRequired && StringUtils.isEmpty(simpleFieldName)) return null;

			FieldValueAccessData fieldValueAccessData = fieldsMap.get(simpleFieldName);
			if (fieldNameRequired && fieldValueAccessData == null) return null;
			ret.add(fieldValueAccessData);

			if (fieldValueAccessData != null && i<splitedFieldName.length-1) {
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

	static String removeTypeFromFieldConfiguration(Type type, String fieldNameInConfiguration) {
		// 1) Try to remove type.getCannonicalName() from fieldDefinitionName
		String fullClassName = ElementUtils.getQualifiedName(type.asElement());
		if (StringUtils.startsWith(fieldNameInConfiguration, fullClassName)) {
			return StringUtils.substring(fieldNameInConfiguration, fullClassName.length() + 1);
		}

		// 2) Try to remove ObjectName (without packageName)
		String packageName = TypeUtils.findPackageName(type);
		if (StringUtils.isNotEmpty(packageName)) {
			String fieldNameInConfigurationWithPackageName = packageName + "." + fieldNameInConfiguration;
			if (StringUtils.startsWith(fieldNameInConfigurationWithPackageName, fullClassName)) {
				return StringUtils.substring(fieldNameInConfigurationWithPackageName, fullClassName.length() + 1);
			}
		}

		return fieldNameInConfiguration;
	}

	static String createKeyConfig(List<FieldValueAccessData> src, List<FieldValueAccessData> dst, boolean addLastFieldName) {
		StringBuilder sb = new StringBuilder();
		appendKeyConfig(sb, src, addLastFieldName, "");
		appendKeyConfig(sb, dst, addLastFieldName, "=");
		return sb.toString();
	}
	static void appendKeyConfig(StringBuilder sb, List<FieldValueAccessData> src, boolean addLastFieldName, String separator) {
		if (src!=null && !src.isEmpty()) {
			int maxSize = src.size();
			if (!addLastFieldName) maxSize--;
			for (int i = 0; i < maxSize; i++) {
				FieldValueAccessData fieldValueAccessData = src.get(i);
				sb.append(separator);
				if (fieldValueAccessData!=null) sb.append(fieldValueAccessData.getField());
				separator = ".";
			}
		}
		if (!addLastFieldName) sb.append(separator).append("*");

	}
	static List<FieldValueAccessData> copyParentPathList(List<FieldValueAccessData> vals) {
		if (vals == null || vals.size()<2) return null;
		List<FieldValueAccessData> ret = new ArrayList<>(vals.size()-1);
		for (int i = 0; i < vals.size()-1; i++) {
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
