package sk.annotation.library.jam.processor.utils;

import org.apache.commons.lang.StringUtils;
import sk.annotation.library.jam.processor.data.TypeWithVariableInfo;

import javax.lang.model.type.TypeMirror;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

abstract public class NameUtils {
	static public String getClassSimpleName(TypeMirror type) {
		//TODO: fix later
		return type.toString().replaceAll("[^a-zA-Z0-9_]", "");
	}
	static public String getClassSimpleName(String fullPath) {
		if (fullPath == null) return null;
		int lastDot = fullPath.lastIndexOf('.');
		if (lastDot < 0) return fullPath;

		return fullPath.substring(lastDot+1);
	}
	static public String getUpperPackage(String fullPath) {
		if (fullPath == null) return null;
		int lastDot = fullPath.lastIndexOf('.');
		if (lastDot < 0) return null;

		return fullPath.substring(0, lastDot);
	}

	static public String resolveRelativePath(String fullClassName, String importedName) {
		if (StringUtils.isEmpty(importedName)) return fullClassName;
		if (StringUtils.isEmpty(fullClassName)) return fullClassName;

		if (fullClassName.length()< importedName.length() + 1) return fullClassName;

		return fullClassName.substring(importedName.length()+1);
	}


	static public String findBestName(List<TypeWithVariableInfo> params, String expectedName) {
		if (params == null || params.isEmpty()) return expectedName;
		Set<String> knownNames = params.stream().map(methodParamInfo -> methodParamInfo.getVariableName()).collect(Collectors.toSet());
		return findBestName(knownNames, expectedName);
	}
	static public String findBestName(Set<String> knownNames, String expectedName) {
		if (knownNames == null || knownNames.isEmpty()) return expectedName;
		if (!knownNames.contains(expectedName)) return expectedName;

		int i=2;
		String newName = expectedName;
		while (knownNames.contains(newName)) {
			newName = expectedName + (i++);
		}

		return newName;
	}
	static public String findBestNameAndUpdateSet(Set<String> knownNames, String expectedName) {
		String name = findBestName(knownNames, expectedName);
		knownNames.add(name);
		return name;
	}


}
