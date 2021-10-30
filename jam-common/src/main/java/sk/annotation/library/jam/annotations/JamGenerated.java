package sk.annotation.library.jam.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.CLASS)
public @interface JamGenerated {
    String value();
    String date() default "";
}
