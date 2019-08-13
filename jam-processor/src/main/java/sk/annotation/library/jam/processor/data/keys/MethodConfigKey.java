package sk.annotation.library.jam.processor.data.keys;

import lombok.Data;
import lombok.EqualsAndHashCode;
import sk.annotation.library.jam.annotations.MapperFieldConfig;

import javax.lang.model.element.ExecutableElement;
import java.util.LinkedList;
import java.util.List;

@Data
public class MethodConfigKey {
	final private String forTopMethod;

	@EqualsAndHashCode.Exclude
	private boolean withCustomConfig = false;

	@EqualsAndHashCode.Exclude
	final private ExecutableElement method;
}
