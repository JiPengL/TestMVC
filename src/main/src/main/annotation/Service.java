package main.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author liujipeng
 * @date 2020/9/4 10:44
 * @mail xuxiejp@163.com
 * @desc ...
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Service {
    String value() default "";
}
