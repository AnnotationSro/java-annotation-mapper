package sk.annotation.library.jam.processor.sourcewriter;

import com.sun.tools.javac.code.Type;
import org.apache.commons.lang.StringUtils;
import sk.annotation.library.jam.processor.utils.TypeUtils;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImportsTypeDefinitions implements SourceGenerator {
	protected final Map<String, String> realNames = new LinkedHashMap<>();
	protected final Set<String> imports = new TreeSet<>();
	protected final String destPackage;

	public ImportsTypeDefinitions(TypeElement forClass) {
		destPackage = TypeUtils.findPackageName(forClass.asType());
	}


	private static Pattern classNamePattern = Pattern.compile("[a-zA-Z0-9._]+");
	public void registerImports(ProcessingEnvironment processingEnv, TypeMirror type) {
		resolveType(processingEnv, type, true);
	}
	private String resolveType(ProcessingEnvironment processingEnv, TypeMirror type, boolean create) {
		if (type == null || type.getKind() == TypeKind.VOID) return null;
		if (type.getKind().isPrimitive()) return type.toString();

		if (!(type instanceof Type)) {
			return type.toString();
		}

		String rawValue = type.toString();

		Matcher matcher = classNamePattern.matcher(rawValue);
		StringBuilder sbOut = new StringBuilder();
		int lastStart = 0;
		while (matcher.find()) {
			String before = StringUtils.substring(rawValue, lastStart, matcher.start());
			if (StringUtils.isNotEmpty(before)) {
				sbOut.append(before);
			}

			lastStart = matcher.end();

			String oneClassName = matcher.group();
			String shortenClassName = realNames.get(oneClassName);
			if (shortenClassName == null && create) {
				ResolveImportStatus ris = resolveImportStatus(processingEnv, oneClassName);
				if (ris!=null && ris.printName!=null) {
					shortenClassName = ris.printName;
					realNames.put(oneClassName, shortenClassName);
					if (ris.importPath != null) imports.add(ris.importPath);
				}
			}

			sbOut.append(shortenClassName!=null ? shortenClassName : oneClassName);
		}

		if (lastStart>=0 && lastStart<rawValue.length()) {
			sbOut.append(StringUtils.substring(rawValue, lastStart));
		}

		return sbOut.toString();
	}

	private static class ResolveImportStatus {
		String importPath;
		String printName;

		public ResolveImportStatus(String importPath, String printName) {
			this.importPath = importPath;
			this.printName = printName;
		}
	}

	private ResolveImportStatus resolveImportStatus(ProcessingEnvironment processingEnv, String simpleName) {
		TypeMirror type = TypeUtils.convertToType(processingEnv, simpleName);
		if (type == null) return null;

		Type topElementType = TypeUtils.findTopElementType(type);
		if (topElementType == null) return null;

		String packageName = TypeUtils.findPackageName(topElementType);
		if (packageName == null) return new ResolveImportStatus(null, simpleName);

		String shortName = StringUtils.substring(simpleName, packageName.length() + 1);
		if (StringUtils.equals(packageName, this.destPackage)) return new ResolveImportStatus(null, shortName);
		if (StringUtils.equals(packageName, "java.lang")) return new ResolveImportStatus(null, shortName);

		String importName = topElementType.asElement().toString();
		return new ResolveImportStatus(importName, shortName);
	}

	public static void main(String[] args) {
		String rawValue = "java.util.List<my.custom.MyCustom.SecondCustom>";

	}

	public String resolveType(TypeMirror type) {
		// No optimalization so far
		return resolveType(null, type, false);
	}

//	public final void registerType(String fullPath) {
//		registerType(new TypeInfo(fullPath));
//	}
//
//	public final void registerType(TypeInfo typeDefinition) {
//		if (typeDefinition == null) return;
//		_resolveTypeDefinitionName(typeDefinition, true);
//		for (TypeInfo parameterType : typeDefinition.getParameterTypes()) {
//			_resolveTypeDefinitionName(parameterType, true);
//		}
//	}

	public final Map<String, String> registerTypes(ProcessingEnvironment processingEnv, String... fullPath) {
		if (fullPath == null || fullPath.length==0) return Collections.emptyMap();

		Map<String, String> ret = new HashMap<>();

		return ret;
	}

/*
	protected final String _resolveTypeDefinitionName(ProcessingEnvironment processingEnv, String cannonicalName, boolean canAddImport) {
		String name = realNames.get(cannonicalName);
		if (name != null) return name;

		// 1) we finding Type
		String forImportCanBe = ElementUtils.findTypeElementToImport(processingEnv, cannonicalName);
		if (forImportCanBe == null) return name;

		String packageName = ClassNameUtils.getUpperPackage(forImportCanBe);


		for (String toImportObj = cannonicalName; toImportObj!=null; toImportObj = ClassNameUtils.getUpperPackage(toImportObj)) {

		}


		String packageName = typeDefinition.getPackage();
		if (Objects.equals(packageName, inPackageName)) {
			realNames.put(cannonicalName, typeDefinition.getSimpleClassName());
			return _getTypeDefinitionJavaSource(typeDefinition);
		}

		if (Objects.equals(packageName, "java.lang")) {
			realNames.put(cannonicalName, typeDefinition.getSimpleClassName());
			return _getTypeDefinitionJavaSource(typeDefinition);
		}

		if (canAddImport) {
			realNames.put(cannonicalName, typeDefinition.getSimpleClassName());
			imports.add(cannonicalName);
			return _getTypeDefinitionJavaSource(typeDefinition);
		}

		return cannonicalName;
	}



	protected String _getTypeDefinitionJavaSource(TypeInfo typeDefinition) {
		return typeDefinition.getSimpleClassName();
	}

	protected void sbAppendTypeDefinition(StringBuilder sb, TypeInfo typeDefinition) {
		sb.append(_resolveTypeDefinitionName(typeDefinition, false));

		if (!typeDefinition.getParameterTypes().isEmpty()) {
			sb.append("<");
			for (int i = 0; i < typeDefinition.getParameterTypes().size(); i++) {
				if (i > 0) sb.append(",");
				sbAppendTypeDefinition(sb, typeDefinition.getParameterTypes().get(i));
			}

			sb.append(">");
		}
	}

	public String getTypeDefinitionName(TypeInfo typeDefinition) {
		StringBuilder sb = new StringBuilder();
		sbAppendTypeDefinition(sb, typeDefinition);
		return sb.toString();
	}*/

	@Override
	public void writeSourceCode(SourceGeneratorContext ctx) {
		if (!imports.isEmpty()) {
			imports.forEach(imp -> ctx.pw.print("\nimport " + imp + ";"));
			ctx.pw.printNewLine();
		}
	}
}
