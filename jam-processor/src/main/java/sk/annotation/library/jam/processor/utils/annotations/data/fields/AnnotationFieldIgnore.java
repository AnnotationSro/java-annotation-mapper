package sk.annotation.library.jam.processor.utils.annotations.data.fields;

import lombok.Getter;
import lombok.Setter;
import sk.annotation.library.jam.annotations.enums.IgnoreType;

@Getter
@Setter
public class AnnotationFieldIgnore extends AnnotationFieldId {
	IgnoreType ignored;
}
