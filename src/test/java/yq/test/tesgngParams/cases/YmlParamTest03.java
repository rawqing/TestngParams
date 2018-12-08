package yq.test.tesgngParams.cases;

import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import yq.test.tesgngParams.cases.feature.MyParams;
import yq.test.tesgngParams.feature.annotations.YamlFilePath;
import yq.test.tesgngParams.feature.annotations.YamlParameter;
import yq.test.tesgngParams.feature.listener.ParamsListener;

@YamlFilePath(("testdata/yml01.yml"))
@Listeners(ParamsListener.class)
public class YmlParamTest03 {

    @Test
    @YamlParameter
    public void yml_case01(String name, int age) {
        System.out.println(String.format("my name is %s , %d years old .",name ,age));
    }
}
