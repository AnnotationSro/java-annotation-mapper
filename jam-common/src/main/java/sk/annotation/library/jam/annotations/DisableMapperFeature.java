package sk.annotation.library.jam.annotations;

import sk.annotation.library.jam.annotations.enums.MapperFeature;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target({ElementType.TYPE})
public @interface DisableMapperFeature {
	MapperFeature[] value() default {};
}
