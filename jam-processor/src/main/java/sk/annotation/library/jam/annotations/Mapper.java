package sk.annotation.library.jam.annotations;

import sk.annotation.library.jam.annotations.enums.ConfigErrorReporting;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE})
public @interface Mapper {
	Class[] withCustom() default {};

	// Default Compilation Configuration
	ConfigErrorReporting defaultErrorConfig() default ConfigErrorReporting.WARNINGS_ONLY;
}