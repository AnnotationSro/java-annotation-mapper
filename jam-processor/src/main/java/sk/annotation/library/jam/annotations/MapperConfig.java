package sk.annotation.library.jam.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE, ElementType.PACKAGE, ElementType.METHOD})
public @interface MapperConfig {
	FieldMapping[] fieldMapping() default {};
	FieldIgnore[] fieldIgnore() default {};

	ConfigGenerator[] config() default {};
    Class[] immutable() default {};
	Class[] withCustom() default {};
}
