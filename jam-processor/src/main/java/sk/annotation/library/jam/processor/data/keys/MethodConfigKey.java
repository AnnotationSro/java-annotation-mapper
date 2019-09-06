package sk.annotation.library.jam.processor.data.keys;

import lombok.Data;
import lombok.EqualsAndHashCode;
import sk.annotation.library.jam.processor.data.generator.method.DeclaredMethodSourceInfo;

import javax.lang.model.element.ExecutableElement;

@Data
public class MethodConfigKey {
	final private String forTopMethod;

	@EqualsAndHashCode.Exclude
	private boolean withCustomConfig = false;

	@EqualsAndHashCode.Exclude
	final private ExecutableElement method;

	@EqualsAndHashCode.Exclude
	final private DeclaredMethodSourceInfo declaredMethod;
}
