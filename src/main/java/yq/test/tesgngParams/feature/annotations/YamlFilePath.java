package yq.test.tesgngParams.feature.annotations;

import yq.test.tesgngParams.feature.enums.YmlVer;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented

public @interface YamlFilePath {

    String value();

    YmlVer ver() default YmlVer.V1;
}
