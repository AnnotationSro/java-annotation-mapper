package sk.annotation.library.jam.processor.utils.annotations.data;

import com.sun.tools.javac.code.Type;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
public class AnnotationMapperConfig {
	final private List<AnnotationConfigGenerator> config = new LinkedList<>();
	private List<Type> immutable;
}
