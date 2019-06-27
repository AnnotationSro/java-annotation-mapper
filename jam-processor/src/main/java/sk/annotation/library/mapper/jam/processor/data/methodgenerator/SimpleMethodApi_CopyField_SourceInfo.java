package sk.annotation.library.mapper.jam.processor.data.methodgenerator;

import com.sun.tools.javac.code.Type;
import org.apache.commons.lang.StringUtils;
import sk.annotation.library.mapper.jam.processor.data.*;
import sk.annotation.library.mapper.jam.processor.data.confwrappers.FieldConfigurationResolver;
import sk.annotation.library.mapper.jam.processor.data.confwrappers.FieldMappingData;
import sk.annotation.library.mapper.jam.processor.data.confwrappers.FieldValueAccessData;
import sk.annotation.library.mapper.jam.processor.data.constructors.TypeConstructorInfo;
import sk.annotation.library.mapper.jam.processor.data.keys.MethodConfigKey;
import sk.annotation.library.mapper.jam.processor.data.mapi.MethodApiFullSyntax;
import sk.annotation.library.mapper.jam.processor.data.mapi.MethodApiKey;
import sk.annotation.library.mapper.jam.processor.sourcewriter.SourceGeneratorContext;
import sk.annotation.library.mapper.jam.processor.utils.NameUtils;
import sk.annotation.library.mapper.jam.processor.utils.TypeUtils;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import java.util.*;

public class SimpleMethodApi_CopyField_SourceInfo extends EmptyMethodSourceInfo {

	public SimpleMethodApi_CopyField_SourceInfo(MapperClassInfo ownerClassInfo, MethodApiFullSyntax methodApiParams) {
		super(ownerClassInfo, methodApiParams);
	}

	//	protected Map<MethodConfigKey, List<FieldMappingData>> analyzedDataMap = new HashMap<>();
	protected Map<MethodConfigKey, List<FieldConfigurationResolver.ResolvedTransformation>> analyzedDataMap = new HashMap<>();
	protected SortedSet<String> allKeys = new TreeSet<>();

	@Override
	protected void analyzeAndGenerateDependMethods(ProcessingEnvironment processingEnv, MethodConfigKey forMethodConfig) {
		if (this.analyzedDataMap.containsKey(forMethodConfig)) {
			return;
		}

		// If its already default
		if (!forMethodConfig.isWithCustomConfig()) {
			for (MethodConfigKey methodConfigKey : this.analyzedDataMap.keySet()) {
				if (!methodConfigKey.isWithCustomConfig()) return;
			}
		}



		// remember Source / Destination
		List<TypeWithVariableInfo> requiredParams = methodApiFullSyntax.getRequiredParams();
		Type typeFrom = (Type) requiredParams.get(0).getVariableType().getType(null);
		Type typeTo = (Type) requiredParams.get(1).getVariableType().getType(null);

		/////////////////////////
		// 1) Collect all information ...

		FieldConfigurationResolver resolver = new FieldConfigurationResolver(ownerClassInfo.getJamMapperConfig(), forMethodConfig.getConfigurations());
		List<FieldConfigurationResolver.ResolvedTransformation> transformGroups = resolver.findTransformationGroups(processingEnv, typeFrom, typeTo);
		this.analyzedDataMap.put(forMethodConfig, transformGroups);

		/////////////////////////
		// 2) Check missing transformation methods ...
		for (FieldConfigurationResolver.ResolvedTransformation group : transformGroups) {
			registerInports(group.getPathFrom());
			registerInports(group.getPathTo());


			// Check transformation types
			for (FieldMappingData fieldMapping : group.fieldMappingData) {
				// check for same type and if its primitive type
				if (canDoWithoutTransform(processingEnv, fieldMapping)) {
					continue;
				}

				if (!fieldMapping.isWithoutProblemOrNotIgnored()) continue;

				TypeMirror sourceType = fieldMapping.getSrc().getTypeOfGetter();
				TypeMirror destinationType = fieldMapping.getDst().getTypeOfSetter();


				// TODO: Complete this later
				if (StringUtils.isNotEmpty(fieldMapping.getMethodNameRequired())) {
					processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "@FieldMapping: methodNameS2D or methodNameD2S are not supported yet (this cofiguration is ignored).");
				}


				// Create or call method !!!


				fieldMapping.setMethodCallApi(findOrCreateOwnMethod(processingEnv, fieldMapping.getMethodNameRequired(), sourceType, destinationType));

				if (fieldMapping.getMethodCallApi() != null && fieldMapping.getMethodCallApi().getOutGeneratedMethod() != null && canAccept(fieldMapping, forMethodConfig)) {
					fieldMapping.getMethodCallApi().getOutGeneratedMethod().analyzeAndGenerateDependMethods(processingEnv, forMethodConfig);
				}

			}
		}
	}
	protected void registerInports(List<FieldValueAccessData> path) {
		if (path == null || path.isEmpty()) return;
		path.forEach(sourcesForImports::add);
	}

	@Override
	protected void writeSourceCodeBody(SourceGeneratorContext ctx) {
		List<TypeWithVariableInfo> requiredParams = methodApiFullSyntax.getRequiredParams();
		TypeWithVariableInfo varSrc = requiredParams.get(0);
		String inputVarSrcName = varSrc.getVariableName();
		String inputVarDstName = varRet.getVariableName();
		this.usedNames.add(inputVarDstName);

		// Instance

		writeSourceInstanceCacheLoad(ctx, varSrc, varRet);

		ctx.pw.print("if (" + varRet.getVariableName()+" == null) { \n\t");
		ctx.pw.print(varRet.getVariableName());
		ctx.pw.print(" = ");
		writeConstructor(ctx, varRet);
		ctx.pw.print(";\n}");


		ctx.pw.print("\n// Copy Fields ");

//		ctx.pw.print("\nreturn MapperUtil.doTransform(");
//		//ctx.pw.print("\n\tnew Object[] {DataType2.class, value1},");
//		writeInputs(ctx);
//		ctx.pw.print("\n\t");
//		ctx.pw.print(requiredParams.get(1).getVariableName());
//		ctx.pw.print(",");
//		//ctx.pw.print("\n\tDataType2::new,");
//		writeConstructor(ctx);
//		ctx.pw.print("\n\t(" + inputVarDstName + ") -> {");
//
//		ctx.pw.levelSpaceUp();
//		ctx.pw.levelSpaceUp();


		String methodContextValue = null;
		if (analyzedDataAreDifferents()) {
			methodContextValue = NameUtils.findBestNameAndUpdateSet(this.usedNames, "methodConfig");

			ctx.pw.print("\nint ");
			ctx.pw.print(methodContextValue);
			ctx.pw.print(" = MapperUtil.getCurrentMethodContext();");
		}

		/////////////////////////
		// 2) Resolve dependend transformations ...
		Map<String, String> cacheOfFoundPaths = new HashMap<>();
		List<MethodConfigKey> methodConfigKeyList = new ArrayList<>(analyzedDataMap.keySet());
		Collections.sort(methodConfigKeyList, new Comparator<MethodConfigKey>() {
			private String getStringKey(MethodConfigKey o1) {
				StringBuilder sb = new StringBuilder();
				sb.append(o1.isWithCustomConfig() ? "a" : "z");
				sb.append(o1.getForTopMethod());
				return sb.toString();
			}

			@Override
			public int compare(MethodConfigKey o1, MethodConfigKey o2) {
				return getStringKey(o1).compareTo(getStringKey(o2));
			}
		});
//		for (Map.Entry<MethodConfigKey, List<FieldConfigurationResolver.ResolvedTransformation>> e : analyzedDataMap.entrySet()) {
		boolean genElse = false;
		for (MethodConfigKey methodConfigKey : methodConfigKeyList) {
			List<FieldConfigurationResolver.ResolvedTransformation> groups = analyzedDataMap.get(methodConfigKey);

			if (groups.isEmpty()) continue;

			if (methodContextValue != null) cacheOfFoundPaths.clear();

			if (methodContextValue != null) {
				if (methodConfigKey.isWithCustomConfig()) {
					ctx.pw.print("\n\n// Copy Fields - for method custom configuration: ");
					ctx.pw.print(methodConfigKey.getForTopMethod());
				}
				else {
					ctx.pw.print("\n\n// Copy Fields - default configuration ");
				}

				ctx.pw.printNewLine();
				if (genElse) {
					ctx.pw.print("else ");
				}
				genElse = true;

				if (methodConfigKey.isWithCustomConfig()) {
					ctx.pw.print("if (");
					ctx.pw.print(methodContextValue);
					ctx.pw.print(" == ");
					ctx.pw.print(methodConfigKey.getForTopMethod());
					ctx.pw.print(") ");
				}

				ctx.pw.print("{");
				ctx.pw.levelSpaceUp();
			} else {
				ctx.pw.print("\n// Copy Fields - without custom configuration");
			}

			for (FieldConfigurationResolver.ResolvedTransformation group : groups) {
				//ctx.pw.printNewLine();

				String varSrcName = createSubPathForNestedObject(ctx, inputVarSrcName, group.getPathFrom(), cacheOfFoundPaths, false);
				String varDstName = createSubPathForNestedObject(ctx, inputVarDstName, group.getPathTo(), cacheOfFoundPaths, true);

				String checkNullCondition = null;
				if (!StringUtils.equals(varSrcName, inputVarSrcName)) {
					checkNullCondition =  varSrcName + " != null";
				}

				if (varSrcName==null || varDstName == null) {
					ctx.pw.print("\n//TODO: methodgenerator or/and destination object is not reachable!");
					checkNullCondition = null;
				}
				else if (checkNullCondition!=null) {
					ctx.pw.print("\nif (" + checkNullCondition + ") {");
					ctx.pw.levelSpaceUp();
				}
				for (FieldMappingData mappingData : group.fieldMappingData) {
					mappingData.writeSourceCode(ctx, this, methodConfigKey, varSrcName, varDstName);
				}
				if (checkNullCondition!=null) {
					ctx.pw.levelSpaceDown();
					ctx.pw.print("\n}");
				}
			}

			if (methodContextValue != null) {
				ctx.pw.levelSpaceDown();
				ctx.pw.print("\n}");
			}
		}

		// Find all interceptors
		TypeMirror srcType = requiredParams.get(0).getVariableType().getType(ctx.processingEnv);
		TypeMirror dstType = methodApiFullSyntax.getReturnType().getType(ctx.processingEnv);
		List<MethodCallApi> interceptors = new LinkedList<>();
		for (Map.Entry<MethodApiKey, MethodApiFullSyntax> entry : ownerClassInfo.getMyUsableMethods().entrySet()) {
			MethodApiKey methodApiKey = entry.getKey();
			if (methodApiKey.isApiWithReturnType()) continue;
			TypeMirror[] types = methodApiKey.getVisibleTypes();
			if (types == null) continue;
			if (types.length != 3) continue;
			if (types[0] != null) continue;

			if (!ctx.processingEnv.getTypeUtils().isAssignable(srcType, types[1])) continue;
			if (!ctx.processingEnv.getTypeUtils().isAssignable(dstType, types[2])) continue;

			// Function is OK, thay can be call
			interceptors.add(MethodCallApi.createFrom("", entry.getValue(), null));
		}

		if (!interceptors.isEmpty()) {
			List<TypeWithVariableInfo> otherVariables = methodApiFullSyntax.getParams();

			List<String> params = new ArrayList<>(2);
			params.add(inputVarSrcName);
			params.add(inputVarDstName);
			ctx.pw.printNewLine();
			ctx.pw.print("\n// Call Interceptors ... ");
			for (MethodCallApi methodCallApi : interceptors) {
				ctx.pw.print("\n");
				methodCallApi.genSourceForCallWithStringParam(ctx, params, otherVariables, this);
				ctx.pw.print(";");
			}
		}

		writeSourceInstanceCacheRegister(ctx, varSrc, varRet);

//		ctx.pw.levelSpaceDown();
//		ctx.pw.levelSpaceDown();
//
//		ctx.pw.print("\n\t}");
//		ctx.pw.print("\n);");
	}

	protected String createSubPathForNestedObject(SourceGeneratorContext ctx, String originalVariable, List<FieldValueAccessData> pathToVariable, Map<String, String> cacheOfFoundPaths, boolean canCreateObject) {
		if (pathToVariable == null || pathToVariable.isEmpty()) return originalVariable;

		StringBuilder sbPathKey = new StringBuilder();
		sbPathKey.append(originalVariable);
		boolean canBeNull = false;
		for (FieldValueAccessData fieldValueAccessData : pathToVariable) {
			sbPathKey.append(".");
			sbPathKey.append(fieldValueAccessData.getFieldName());
			originalVariable = _createSubPathForNestedObject(ctx, originalVariable, sbPathKey.toString(), fieldValueAccessData, cacheOfFoundPaths, canCreateObject, canBeNull);
			canBeNull = true;
		}
		return originalVariable;
	}

	private String _createSubPathForNestedObject(SourceGeneratorContext ctx, String parentVariable, String pathKey, FieldValueAccessData pathToVariable, Map<String, String> cacheOfFoundPaths, boolean canCreateObject, boolean canBeNull) {
		if (pathToVariable == null || StringUtils.isEmpty(pathToVariable.getFieldName())) return parentVariable;

		String variable = cacheOfFoundPaths.get(pathKey);

		if (variable==null) {
			variable = NameUtils.findBestName(cacheOfFoundPaths.keySet(), StringUtils.replace(pathKey, ".", "_"));
			cacheOfFoundPaths.put(pathKey, variable);

			TypeWithVariableInfo variableInfo = new TypeWithVariableInfo(variable, new TypeInfo(pathToVariable.getTypeOfGetter()));

			String sourceForGetter = pathToVariable.getSourceForGetter(parentVariable);
			String[] sourceForSetter = pathToVariable.getSourceForSetter(parentVariable);

			ctx.pw.print("\n");
			variableInfo.writeSourceCode(ctx);
			ctx.pw.print(" = ");
			if (!canCreateObject && canBeNull) {
				ctx.pw.print("(" + parentVariable + "==null) ? null : ");
			}
			ctx.pw.print(sourceForGetter);
			ctx.pw.print(";");

			if (canCreateObject) {
				// Test if (variable == null) { variable = new Variable(); ....)
				ctx.pw.print("\nif (");
				ctx.pw.print(variable);
				ctx.pw.print(" == null) {");

				// new instance
				ctx.pw.print("\n\t");
				ctx.pw.print(variable);
				ctx.pw.print(" = ");
				new TypeConstructorInfo(variableInfo.getVariableType(), false).writeSourceCode(ctx);
				ctx.pw.print(";");

				// set value
				ctx.pw.print("\n\t");
				ctx.pw.print(sourceForSetter[0]);
				ctx.pw.print(sourceForSetter[1]);
				ctx.pw.print(variable);
				ctx.pw.print(sourceForSetter[2]);
				ctx.pw.print(";");

				ctx.pw.print("\n}");
			}
		}

		return variable;
	}

	protected boolean analyzedDataAreDifferents() {
		if (this.analyzedDataMap.size() < 2) return false;
		// TODO : compare content !!!
		return true;
	}

	protected boolean canDoWithoutTransform(ProcessingEnvironment processingEnv, FieldMappingData fieldMapping) {
		if (fieldMapping == null) return true;
		if (fieldMapping.getSrc() == null) return true;
		if (fieldMapping.getDst() == null) return true;
		TypeMirror source = fieldMapping.getSrc().getTypeOfGetter();
		TypeMirror destination = fieldMapping.getDst().getTypeOfSetter();

		if (source == null) return true;
		if (destination == null) return true;

		if (StringUtils.isNotEmpty(fieldMapping.getMethodNameRequired())) return false;

		if (!processingEnv.getTypeUtils().isSameType(source, destination)) return false;

		if (TypeUtils.isBaseOrPrimitiveType(processingEnv, source)) return true;

		return false;
	}

	protected boolean canAccept(FieldMappingData fieldMapping, MethodConfigKey forMethodConfig) {
		if (fieldMapping==null) return false;
		if (!fieldMapping.isWithoutProblemOrNotIgnored()) return false;

		return true;
	}
}
