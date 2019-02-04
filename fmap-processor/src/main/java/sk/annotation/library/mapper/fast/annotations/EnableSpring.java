package sk.annotation.library.mapper.fast.annotations;

import sk.annotation.library.mapper.fast.annotations.enums.IocScope;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE})
public @interface EnableSpring {
	String beanName() default "";
	IocScope scope() default IocScope.DEFAULT;

}
