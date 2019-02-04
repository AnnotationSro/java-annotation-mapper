package sk.annotation.library.mapper.fast.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target({})
public @interface FieldIgnore {
	String value();
	boolean ignored() default true;
}
