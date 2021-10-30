package sk.annotation.library.jam.processor.utils.annotations.data;

import com.sun.tools.javac.code.Type;
import sk.annotation.library.jam.annotations.enums.ApplyFieldStrategy;
import sk.annotation.library.jam.processor.utils.annotations.data.fields.AnnotationFieldIgnore;
import sk.annotation.library.jam.processor.utils.annotations.data.fields.AnnotationFieldMapping;

import java.util.LinkedList;
import java.util.List;

public class AnnotationMapperConfig {
	public TypeConfig type;

	final private List<AnnotationFieldMapping> fieldMapping = new LinkedList<>();
	final private List<AnnotationFieldIgnore> fieldIgnore = new LinkedList<>();
	final private List<AnnotationConfigGenerator> config = new LinkedList<>();
	final private List<Type> immutable = new LinkedList<>();
	final private List<Type> withCustom = new LinkedList<>();

	final private List<ApplyFieldStrategy> applyWhen = new LinkedList<>();

	public TypeConfig getType() {
		return type;
	}

	public void setType(TypeConfig type) {
		this.type = type;
	}

	public List<AnnotationFieldMapping> getFieldMapping() {
		return fieldMapping;
	}

	public List<AnnotationFieldIgnore> getFieldIgnore() {
		return fieldIgnore;
	}

	public List<AnnotationConfigGenerator> getConfig() {
		return config;
	}

	public List<Type> getImmutable() {
		return immutable;
	}

	public List<Type> getWithCustom() {
		return withCustom;
	}

	public List<ApplyFieldStrategy> getApplyWhen() {
		return applyWhen;
	}
}
