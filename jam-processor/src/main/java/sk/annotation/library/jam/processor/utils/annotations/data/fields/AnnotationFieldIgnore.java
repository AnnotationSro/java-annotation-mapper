package sk.annotation.library.jam.processor.utils.annotations.data.fields;

import sk.annotation.library.jam.annotations.enums.IgnoreType;

public class AnnotationFieldIgnore extends AnnotationFieldId {
	IgnoreType ignored;

	public IgnoreType getIgnored() {
		return ignored;
	}

	public void setIgnored(IgnoreType ignored) {
		this.ignored = ignored;
	}
}
