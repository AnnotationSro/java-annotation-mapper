package sk.annotation.library.jam.processor.utils.annotations.data;

import com.sun.tools.javac.code.Type;
import lombok.Getter;
import lombok.Setter;
import sk.annotation.library.jam.processor.utils.annotations.data.fields.AnnotationFieldIgnore;
import sk.annotation.library.jam.processor.utils.annotations.data.fields.AnnotationFieldMapping;

import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
public class AnnotationMapperConfig {
	public TypeConfig type;

	final private List<AnnotationFieldMapping> fieldMapping = new LinkedList<>();
	final private List<AnnotationFieldIgnore> fieldIgnore = new LinkedList<>();
	final private List<AnnotationConfigGenerator> config = new LinkedList<>();
	final private List<Type> immutable = new LinkedList<>();
}
