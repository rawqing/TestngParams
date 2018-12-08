package yq.test.tesgngParams.cases.feature;

import yq.test.tesgngParams.feature.annotations.CustomParameter;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@CustomParameter(provider = MyParamsImp.class)
public @interface MyParams {

    String value() default "";
}
