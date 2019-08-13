package sk.annotation.library.jam.processor.utils.annotations.data.fields;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnnotationFieldMapping {
	AnnotationFieldId d;
	AnnotationFieldId s;

	boolean ignoreDirectionS2D;
	boolean ignoreDirectionD2S;

	String methodNameS2D;
	String methodNameD2S;

}
