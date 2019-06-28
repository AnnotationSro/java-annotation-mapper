package sk.annotation.library.jam.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target({})
public @interface FieldMapping {
	String d();
	String s();

	boolean ignoreDirectionS2D() default false;
	boolean ignoreDirectionD2S() default false;

	String methodNameS2D() default "";
	String methodNameD2S() default "";
}
