package cn.j8.tcp.http.router;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ControllerKey {
	public String value() default "/";
}
