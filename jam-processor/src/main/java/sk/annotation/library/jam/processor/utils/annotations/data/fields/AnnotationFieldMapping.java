package sk.annotation.library.jam.processor.utils.annotations.data.fields;

import sk.annotation.library.jam.annotations.enums.ApplyFieldStrategy;

import java.util.LinkedList;
import java.util.List;

public class AnnotationFieldMapping {
	AnnotationFieldId d;
	AnnotationFieldId s;

	boolean ignoreDirectionS2D;
	boolean ignoreDirectionD2S;

	String methodNameS2D;
	String methodNameD2S;

	List<ApplyFieldStrategy> applyWhenS2D = new LinkedList<>();
	List<ApplyFieldStrategy> applyWhenD2S = new LinkedList<>();

	public AnnotationFieldId getD() {
		return d;
	}

	public void setD(AnnotationFieldId d) {
		this.d = d;
	}

	public AnnotationFieldId getS() {
		return s;
	}

	public void setS(AnnotationFieldId s) {
		this.s = s;
	}

	public boolean isIgnoreDirectionS2D() {
		return ignoreDirectionS2D;
	}

	public void setIgnoreDirectionS2D(boolean ignoreDirectionS2D) {
		this.ignoreDirectionS2D = ignoreDirectionS2D;
	}

	public boolean isIgnoreDirectionD2S() {
		return ignoreDirectionD2S;
	}

	public void setIgnoreDirectionD2S(boolean ignoreDirectionD2S) {
		this.ignoreDirectionD2S = ignoreDirectionD2S;
	}

	public String getMethodNameS2D() {
		return methodNameS2D;
	}

	public void setMethodNameS2D(String methodNameS2D) {
		this.methodNameS2D = methodNameS2D;
	}

	public String getMethodNameD2S() {
		return methodNameD2S;
	}

	public void setMethodNameD2S(String methodNameD2S) {
		this.methodNameD2S = methodNameD2S;
	}

	public List<ApplyFieldStrategy> getApplyWhenS2D() {
		return applyWhenS2D;
	}

	public void setApplyWhenS2D(List<ApplyFieldStrategy> applyWhenS2D) {
		this.applyWhenS2D = applyWhenS2D;
	}

	public List<ApplyFieldStrategy> getApplyWhenD2S() {
		return applyWhenD2S;
	}

	public void setApplyWhenD2S(List<ApplyFieldStrategy> applyWhenD2S) {
		this.applyWhenD2S = applyWhenD2S;
	}
}
