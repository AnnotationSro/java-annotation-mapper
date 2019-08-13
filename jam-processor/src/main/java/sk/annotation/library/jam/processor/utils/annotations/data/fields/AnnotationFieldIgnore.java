package sk.annotation.library.jam.processor.utils.annotations.data.fields;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnnotationFieldIgnore extends AnnotationFieldId {
	boolean ignored;
}
