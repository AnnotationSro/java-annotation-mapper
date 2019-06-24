package sk.annotation.library.mapper.jam.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.PARAMETER})
public @interface Context {
	String value() default "ctx";
}
