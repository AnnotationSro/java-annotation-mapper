package sk.annotation.library.mapper.jam.annotations;

import sk.annotation.library.mapper.jam.annotations.enums.ConfigErrorReporting;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target({})
public @interface ConfigGenerator {
	String beanOrField();
	ConfigErrorReporting missingAsSource() default ConfigErrorReporting.WARNINGS_ONLY;
	ConfigErrorReporting missingAsDestination() default ConfigErrorReporting.WARNINGS_ONLY;
}
