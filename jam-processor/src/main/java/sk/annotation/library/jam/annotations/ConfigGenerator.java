package sk.annotation.library.jam.annotations;

import sk.annotation.library.jam.annotations.enums.ConfigErrorReporting;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target({})
public @interface ConfigGenerator {
	Class[] fieldType() default {};
	String field();
	ConfigErrorReporting missingAsSource() default ConfigErrorReporting.WARNINGS_ONLY;
	ConfigErrorReporting missingAsDestination() default ConfigErrorReporting.WARNINGS_ONLY;
}
