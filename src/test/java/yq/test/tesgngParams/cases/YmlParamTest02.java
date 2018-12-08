package yq.test.tesgngParams.cases;

import yq.test.tesgngParams.feature.annotations.YamlParameter;
import yq.test.tesgngParams.feature.listener.ParamsListener;
import yq.test.tesgngParams.feature.annotations.Params;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(ParamsListener.class)
public class YmlParamTest02 {

    @Test
    @YamlParameter("testdata/yml01.yml")
    public void yml_case01(boolean print ,String s) {
        if (print) {
            System.out.println(s);
        }else{
            System.out.println(s);
        }
    }

    @Test
    @Params({
            "jone ,8",
            "  wang, 19"
    })
    public void params_case01(String name, int age) {
        System.out.println(String.format("~params_case : my name is %s , %d years old .",name ,age));
    }
}
