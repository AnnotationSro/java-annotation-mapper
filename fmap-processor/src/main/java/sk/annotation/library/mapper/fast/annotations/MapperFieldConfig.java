package sk.annotation.library.mapper.fast.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE,ElementType.METHOD})
public @interface MapperFieldConfig {
	FieldMapping[] fieldMapping() default {};
	FieldIgnore[] fieldIgnore() default {};
	ConfigGenerator[] config() default {};
}
