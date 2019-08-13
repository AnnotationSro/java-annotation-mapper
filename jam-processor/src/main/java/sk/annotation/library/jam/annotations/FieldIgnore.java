package sk.annotation.library.jam.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target({})
public @interface FieldIgnore {
	Class[] types() default {};
	String value();
	boolean ignored() default true;
}
