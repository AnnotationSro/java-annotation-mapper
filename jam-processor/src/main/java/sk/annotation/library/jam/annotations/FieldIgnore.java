package sk.annotation.library.jam.annotations;

import sk.annotation.library.jam.annotations.enums.IgnoreType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target({})
public @interface FieldIgnore {
	Class[] types() default {};
	String[] packages() default {};
	String[] value();
	IgnoreType ignored() default IgnoreType.IGNORE_ALL;
}
