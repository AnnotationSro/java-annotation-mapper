package sk.annotation.library.jam.processor.utils.annotations.data.fields;

import lombok.Getter;
import lombok.Setter;
import sk.annotation.library.jam.annotations.enums.ApplyFieldStrategy;

import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
public class AnnotationFieldMapping {
	AnnotationFieldId d;
	AnnotationFieldId s;

	boolean ignoreDirectionS2D;
	boolean ignoreDirectionD2S;

	String methodNameS2D;
	String methodNameD2S;

	List<ApplyFieldStrategy> applyWhenS2D = new LinkedList<>();
	List<ApplyFieldStrategy> applyWhenD2S = new LinkedList<>();
}
