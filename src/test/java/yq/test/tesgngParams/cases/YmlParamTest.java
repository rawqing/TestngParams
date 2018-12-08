package yq.test.tesgngParams.cases;

import yq.test.tesgngParams.cases.feature.MyParams;
import yq.test.tesgngParams.feature.annotations.YamlFilePath;
import yq.test.tesgngParams.feature.listener.ParamsListener;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import yq.test.tesgngParams.utils.DoIt;

@YamlFilePath(("yml01.yml"))
@Listeners(yq.test.tesgngParams.feature.listener.ParamsListener.class)
public class YmlParamTest {

    @Test
    @MyParams("yml01.yml")
    public void yml_case01(String name, int age) {
        System.out.println(String.format("my name is %s , %d years old .",name ,age));
    }

    @Test
    @MyParams
    public void yml_case02(String s) {
        System.out.println("say : "+s);
    }

    @Test
    public void ss() {
        String s = "aC:\\sdfl";
        System.out.println(s.matches("^[a-zA-z]:[/|\\\\].+"));

        String s2 = "`!lsjdfls@@`";
        System.out.println(DoIt.removeSurrounding(s2 ,"`!!" ,"@`"));
    }
}
