package sk.annotation.library.mapper.fast.annotations;

import sk.annotation.library.mapper.fast.annotations.enums.ConfigErrorReporting;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE})
public @interface FastMapper {
	Class[] withCustom() default {};

	// Default Compilation Configuration
	ConfigErrorReporting defaultErrorConfig() default ConfigErrorReporting.WARNINGS_ONLY;
}
