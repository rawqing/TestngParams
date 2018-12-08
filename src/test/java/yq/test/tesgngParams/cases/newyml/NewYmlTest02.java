package yq.test.tesgngParams.cases.newyml;

import yq.test.tesgngParams.feature.annotations.YamlFilePath;
import yq.test.tesgngParams.feature.annotations.YamlParameter;
import yq.test.tesgngParams.feature.enums.YmlVer;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import yq.test.tesgngParams.feature.listener.ParamsListener;

@YamlFilePath(value = "testdata/newyml.yml" ,ver = YmlVer.V2)
@Listeners(ParamsListener.class)
public class NewYmlTest02 {

    @Test
    @YamlParameter
    public void yml_case2_1(String name, int age) {
        System.out.println(String.format("my name is %s , %d years old .",name ,age));
    }

    @Test
    @YamlParameter
    public void yml_case2_2(String s) {
        System.out.println("say : "+s);
    }
}
