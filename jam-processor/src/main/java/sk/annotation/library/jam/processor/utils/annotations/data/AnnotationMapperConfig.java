package sk.annotation.library.jam.processor.utils.annotations.data;

import sk.annotation.library.jam.annotations.enums.ApplyFieldStrategy;
import sk.annotation.library.jam.processor.utils.annotations.data.fields.AnnotationFieldIgnore;
import sk.annotation.library.jam.processor.utils.annotations.data.fields.AnnotationFieldMapping;

import javax.lang.model.type.DeclaredType;
import java.util.LinkedList;
import java.util.List;

public class AnnotationMapperConfig {
	public TypeConfig type;

	final private List<AnnotationFieldMapping> fieldMapping = new LinkedList<>();
	final private List<AnnotationFieldIgnore> fieldIgnore = new LinkedList<>();
	final private List<AnnotationConfigGenerator> config = new LinkedList<>();
	final private List<DeclaredType> immutable = new LinkedList<>();
	final private List<DeclaredType> withCustom = new LinkedList<>();

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

	public List<DeclaredType> getImmutable() {
		return immutable;
	}

	public List<DeclaredType> getWithCustom() {
		return withCustom;
	}

	public List<ApplyFieldStrategy> getApplyWhen() {
		return applyWhen;
	}
}
