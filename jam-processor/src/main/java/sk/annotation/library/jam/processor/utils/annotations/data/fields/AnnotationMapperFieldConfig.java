package sk.annotation.library.jam.processor.utils.annotations.data.fields;

import lombok.Getter;

import java.util.LinkedList;
import java.util.List;

@Getter
public class AnnotationMapperFieldConfig {
	final private List<AnnotationFieldMapping> fieldMapping = new LinkedList<>();
	final private List<AnnotationFieldIgnore> fieldIgnore = new LinkedList<>();
}
