package sk.annotation.library.jam.processor.utils.annotations.data;

import lombok.Getter;
import lombok.Setter;
import sk.annotation.library.jam.annotations.enums.ConfigErrorReporting;
import sk.annotation.library.jam.processor.utils.annotations.data.fields.AnnotationFieldId;

@Getter @Setter
public class AnnotationConfigGenerator {
	private AnnotationFieldId fieldId;
	private ConfigErrorReporting missingAsSource;
	private ConfigErrorReporting missingAsDestination;
}
