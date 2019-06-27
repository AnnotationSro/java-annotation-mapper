package sk.annotation.library.mapper.jam.annotations;

import sk.annotation.library.mapper.jam.annotations.enums.IocScope;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE})
public @interface EnableCDI {
	String beanName() default "";
	IocScope scope() default IocScope.DEFAULT;

}