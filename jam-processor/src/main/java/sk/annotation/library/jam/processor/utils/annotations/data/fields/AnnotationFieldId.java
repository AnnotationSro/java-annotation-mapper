package sk.annotation.library.jam.processor.utils.annotations.data.fields;

import com.sun.tools.javac.code.Type;
import sk.annotation.library.jam.processor.data.confwrappers.FieldValueAccessData;
import sk.annotation.library.jam.processor.utils.TypeUtils;
import sk.annotation.library.jam.processor.utils.commons.StringUtils;

import javax.annotation.processing.ProcessingEnvironment;
import java.util.List;

public class AnnotationFieldId {
	private List<String> packages;
	private List<Type> types;
	private String value;

	public boolean isTypeAcceptable(ProcessingEnvironment processingEnv, Type type) {
		return isTypeAcceptable_type(processingEnv, type) && isTypeAcceptable_package(processingEnv, type);
	}
	public boolean isAcceptableTypeAndName(ProcessingEnvironment processingEnv, Type type, List<FieldValueAccessData> fieldPath) {
		if (isTypeAcceptable_type(processingEnv, type) || isTypeAcceptable_package(processingEnv, type)) {
			if (getValue()==null) return true;
			if (StringUtils.equals(getValue(), fieldPath.get(0).getFieldName())) return true;
			return false;
		}
		return false;
	}

	private boolean isTypeAcceptable_package(ProcessingEnvironment processingEnv, Type type) {
		// Type check
		if (packages == null || packages.isEmpty()) {
			return true;
		}
		String pckgName = TypeUtils.findPackageName(type);
		if (pckgName == null) pckgName = "";
		for (String basePackage : packages) {
			if (pckgName.indexOf(basePackage)==0) {
				return true;
			}
		}
		return false;
	}

	private boolean isTypeAcceptable_type(ProcessingEnvironment processingEnv, Type type) {
		// Type check
		if (types == null || types.isEmpty()) {
			return true;
		}
		for (Type tp : types) {
			if (TypeUtils.isAssignable(processingEnv, type, tp)) {
				return true;
			}
		}
		return false;
	}


	public List<String> getPackages() {
		return packages;
	}

	public void setPackages(List<String> packages) {
		this.packages = packages;
	}

	public List<Type> getTypes() {
		return types;
	}

	public void setTypes(List<Type> types) {
		this.types = types;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
