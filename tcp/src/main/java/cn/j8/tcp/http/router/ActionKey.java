package cn.j8.tcp.http.router;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ActionKey {
	public String value() default "";
}
