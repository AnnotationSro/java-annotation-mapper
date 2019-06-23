package sk.annotation.library.mapper.fast.annotations;

import sk.annotation.library.mapper.fast.annotations.enums.MapperFeature;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE})
public @interface DisableMapperFeature {
	MapperFeature[] value() default {};
}
