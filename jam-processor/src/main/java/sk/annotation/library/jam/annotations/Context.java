package sk.annotation.library.jam.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target({ElementType.PARAMETER})
public @interface Context {
	String value() default "ctx";

	public static final String jamConfif = "#JAM#conf";
	public static final String jamContext = "#JAM#ctx";
}
