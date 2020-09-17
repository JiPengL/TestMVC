package main.annotation;

import java.lang.annotation.*;

/**
 * @author liujipeng
 * @date 2020/9/4 10:44
 * @mail xuxiejp@163.com
 * @desc ...
 */
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Autowired {
    boolean required() default true;

}
