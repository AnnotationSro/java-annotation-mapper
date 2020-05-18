package sk.annotation.library.jam.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target({})
public @interface FieldMapping {
	String[] sPackages() default {};
	Class[] sTypes() default {};
	String[] s();

	String[] dPackages() default {};
	Class[] dTypes() default {};
	String[] d();

	boolean ignoreDirectionS2D() default false;
	boolean ignoreDirectionD2S() default false;

	String methodNameS2D() default "";
	String methodNameD2S() default "";
}
