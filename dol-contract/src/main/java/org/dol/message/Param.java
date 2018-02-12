package org.dol.message;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by dolphin on 2017/10/16.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Param {

    String value();

    boolean required() default false;

    ParamType type() default ParamType.NONE;

    String regex() default "";

    int minLength() default 0;

    int maxLength() default 0;

    double minValue() default 0.0;

    double maxValue() default 0.0;
}
