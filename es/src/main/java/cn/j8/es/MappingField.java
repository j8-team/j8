package cn.j8.es;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface MappingField {
    String type() default "text";
    String analyzer() default "";
    String search_analyzer() default "";
    String format() default "";
}
