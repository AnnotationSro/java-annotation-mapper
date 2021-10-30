package sk.annotation.library.jam.processor.data.keys;

import sk.annotation.library.jam.processor.data.generator.method.DeclaredMethodSourceInfo;

import javax.lang.model.element.ExecutableElement;
import java.util.Objects;

public class MethodConfigKey {
	final private String forTopMethod;

	private boolean withCustomConfig = false;

	final private ExecutableElement method;

	final private DeclaredMethodSourceInfo declaredMethod;

	public MethodConfigKey(String forTopMethod, ExecutableElement method, DeclaredMethodSourceInfo declaredMethod) {
		this.forTopMethod = forTopMethod;
		this.method = method;
		this.declaredMethod = declaredMethod;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		MethodConfigKey that = (MethodConfigKey) o;
		return Objects.equals(forTopMethod, that.forTopMethod);
	}

	@Override
	public int hashCode() {
		return Objects.hash(forTopMethod);
	}

	public String getForTopMethod() {
		return forTopMethod;
	}

	public boolean isWithCustomConfig() {
		return withCustomConfig;
	}

	public void setWithCustomConfig(boolean withCustomConfig) {
		this.withCustomConfig = withCustomConfig;
	}

	public ExecutableElement getMethod() {
		return method;
	}

	public DeclaredMethodSourceInfo getDeclaredMethod() {
		return declaredMethod;
	}
}
