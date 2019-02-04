package sk.annotation.library.mapper.fast.processor.data.methodgenerator;

import com.sun.tools.javac.code.Type;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import sk.annotation.library.mapper.fast.processor.data.*;
import sk.annotation.library.mapper.fast.processor.data.confwrappers.FieldConfigurationResolver;
import sk.annotation.library.mapper.fast.processor.data.confwrappers.FieldMappingData;
import sk.annotation.library.mapper.fast.processor.data.confwrappers.FieldValueAccessData;
import sk.annotation.library.mapper.fast.processor.data.keys.MethodConfigKey;
import sk.annotation.library.mapper.fast.processor.data.mapi.MethodApiFullSyntax;
import sk.annotation.library.mapper.fast.processor.data.mapi.MethodApiKey;
import sk.annotation.library.mapper.fast.processor.sourcewriter.SourceGeneratorContext;
import sk.annotation.library.mapper.fast.processor.utils.NameUtils;
import sk.annotation.library.mapper.fast.processor.utils.TypeUtils;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import java.util.*;

public class CopyFieldMethodSourceInfo extends EmptyMethodSourceInfo {

	private Type typeFrom = null;
	private Type typeTo = null;

	public CopyFieldMethodSourceInfo(MapperClassInfo ownerClassInfo, MethodApiFullSyntax methodApiParams) {
		super(ownerClassInfo, methodApiParams);

		// remember Source / Destination
		typeFrom = (Type) methodApiFullSyntax.getParams().get(0).getVariable().getType().getType(null);
		typeTo = (Type) methodApiFullSyntax.getParams().get(1).getVariable().getType().getType(null);
	}

	//	protected Map<MethodConfigKey, List<FieldMappingData>> analyzedDataMap = new HashMap<>();
	protected Map<MethodConfigKey, List<FieldConfigurationResolver.ResolvedTransformation>> analyzedDataMap = new HashMap<>();
	protected SortedSet<String> allKeys = new TreeSet<>();

	@Override
	protected void analyzeAndGenerateDependMethods(ProcessingEnvironment processingEnv, MethodConfigKey forMethodConfig) {
		if (this.analyzedDataMap.containsKey(forMethodConfig)) {
			return;
		}

		// Ak je uz default
		if (!forMethodConfig.isWithCustomConfig()) {
			for (MethodConfigKey methodConfigKey : this.analyzedDataMap.keySet()) {
				if (!methodConfigKey.isWithCustomConfig()) return;
			}
		}

		/////////////////////////
		// 1) Collect all information ...

		FieldConfigurationResolver resolver = new FieldConfigurationResolver(ownerClassInfo.getFastMapperConfig(), forMethodConfig.getConfigurations());
		List<FieldConfigurationResolver.ResolvedTransformation> transformGroups = resolver.findTransformationGroups(processingEnv, typeFrom, typeTo);
		this.analyzedDataMap.put(forMethodConfig, transformGroups);

		/////////////////////////
		// 2) Check missing transformation methods ...
		for (FieldConfigurationResolver.ResolvedTransformation group : transformGroups) {
			registerInports(group.getPathFrom());
			registerInports(group.getPathTo());


			// Check transformation types
			for (FieldMappingData fieldMapping : group.fieldMappingData) {
				// kontrola rovnakeho typu d jednoduchosti
				if (canDoWithoutTransform(processingEnv, fieldMapping)) {
					continue;
				}

				if (!fieldMapping.isWithoutProblemOrNotIgnored()) continue;

				TypeMirror sourceType = fieldMapping.getSrc().getTypeOfGetter();
				TypeMirror destinationType = fieldMapping.getDst().getTypeOfSetter();


				// TODO: Dokonci toto neskor
				if (StringUtils.isNotEmpty(fieldMapping.getMethodNameRequired())) {
					processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "@sk.annotation.library.mapper.fast.annotations.FieldMapping: methodNameS2D or methodNameD2S are not supported yet (this cofiguration is ignored).");
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

	protected void writeInputs(SourceGeneratorContext ctx) {
		ctx.pw.print("\n\t\"" + StringEscapeUtils.escapeJava(methodApiFullSyntax.getReturnType().getType(ctx.processingEnv).toString()) + "\", ");
		ctx.pw.print("\n\tnew Object[] {");
		ctx.pw.print(methodApiFullSyntax.getParams().get(0).getVariable().getName());
		ctx.pw.print("},");
	}

	protected void writeConstructor(SourceGeneratorContext ctx) {
		ctx.pw.print("\n\t");

		MethodApiKey constructorApiKey = new MethodApiKey(methodApiFullSyntax.getReturnType(), Collections.emptyList());
		MethodCallApi methodCallApi = ownerClassInfo.findMethodApiToCall(constructorApiKey);
		if (methodCallApi != null) {
			if (StringUtils.isNotEmpty(methodCallApi.getPathToSyntax())) {
				ctx.pw.print(methodCallApi.getPathToSyntax());
				ctx.pw.print("::");
				ctx.pw.print(methodCallApi.getMethodSyntax().getName());
				ctx.pw.print(",");
				return;
			}

			ctx.pw.print(ownerClassInfo.getSimpleClassName());
			ctx.pw.print(".this::");
			ctx.pw.print(methodCallApi.getMethodSyntax().getName());
			ctx.pw.print(",");
			return;
		}

		// Todo - check Collections & Interfaces & Default Public Constructors !!!
		new TypeConstructorInfo(methodApiFullSyntax.getReturnType(), true).writeSourceCode(ctx);
		ctx.pw.print(",");
	}

	@Override
	protected void writeSourceCodeBody(SourceGeneratorContext ctx) {
		String inputVarSrcName = methodApiFullSyntax.getParams().get(0).getVariable().getName();
		String inputVarDstName = NameUtils.findBestName(this.usedNames, "dest");
		this.usedNames.add(inputVarDstName);

		ctx.pw.print("\n// Copy Fields ");

		ctx.pw.print("\nreturn MapperUtil.doTransform(");
		//ctx.pw.print("\n\tnew Object[] {DataType2.class, value1},");
		writeInputs(ctx);
		ctx.pw.print("\n\t");
		ctx.pw.print(methodApiFullSyntax.getParams().get(1).getVariable().getName());
		ctx.pw.print(",");
		//ctx.pw.print("\n\tDataType2::new,");
		writeConstructor(ctx);
		ctx.pw.print("\n\t(" + inputVarDstName + ") -> {");

		ctx.pw.levelSpaceUp();
		ctx.pw.levelSpaceUp();


		String methodContextValue = null;
		if (analyzedDataAreDifferents()) {
			methodContextValue = NameUtils.findBestName(this.usedNames, "methodConfig");
			this.usedNames.add(methodContextValue);

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
					ctx.pw.print(methodConfigKey.getForTopMethod().hashCode() + "");
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

		// Vyhladanie vsetkych interceptors
		TypeMirror srcType = methodApiFullSyntax.getParams().get(0).getVariable().getType().getType(ctx.processingEnv);
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
			List<String> params = new ArrayList<>(2);
			params.add(inputVarSrcName);
			params.add(inputVarDstName);
			ctx.pw.printNewLine();
			ctx.pw.print("\n// Call Interceptors ... ");
			for (MethodCallApi methodCallApi : interceptors) {
				ctx.pw.print("\n");
				methodCallApi.genSourceForCall(ctx, params, Collections.emptyMap());
				ctx.pw.print(";");
			}
		}

		ctx.pw.levelSpaceDown();
		ctx.pw.levelSpaceDown();

		ctx.pw.print("\n\t}");
		ctx.pw.print("\n);");
	}

	protected String createSubPathForNestedObject(SourceGeneratorContext ctx, String originalVariable, List<FieldValueAccessData> pathToVariable, Map<String, String> cacheOfFoundPaths, boolean canCreateObject) {
		if (pathToVariable == null || pathToVariable.isEmpty()) return originalVariable;

		//
		StringBuilder sbPathKey = new StringBuilder();
		sbPathKey.append(originalVariable);
		for (FieldValueAccessData fieldValueAccessData : pathToVariable) {
			sbPathKey.append(".");
			sbPathKey.append(fieldValueAccessData.getFieldName());
			originalVariable = _createSubPathForNestedObject(ctx, originalVariable, sbPathKey.toString(), fieldValueAccessData, cacheOfFoundPaths, canCreateObject);
		}
		return originalVariable;
	}

	private String _createSubPathForNestedObject(SourceGeneratorContext ctx, String parentVariable, String pathKey, FieldValueAccessData pathToVariable, Map<String, String> cacheOfFoundPaths, boolean canCreateObject) {
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
			if (!canCreateObject) {
				ctx.pw.print("(" + parentVariable + "==null) ? null : ");
			}
			ctx.pw.print(sourceForGetter);
			ctx.pw.print(";");

			if (canCreateObject) {
				// Test na if (variable == null) { variable = new Variable(); ....)
				ctx.pw.print("\nif (");
				ctx.pw.print(variable);
				ctx.pw.print(" == null) {");

				// new instance
				ctx.pw.print("\n\t");
				ctx.pw.print(variable);
				ctx.pw.print(" = ");
				new TypeConstructorInfo(variableInfo.getType(), false).writeSourceCode(ctx);
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
