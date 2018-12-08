package yq.test.tesgngParams.cases.feature;

import yq.test.tesgngParams.feature.IParametersProvider;
import yq.test.tesgngParams.feature.paramHandle.YamlProviderImp;
import org.testng.ITestContext;

import java.lang.reflect.Method;

public class MyParamsImp implements IParametersProvider<MyParams> {
    private MyParams c;
    @Override
    public void initialize(MyParams parametersAnnotation) {
        c = parametersAnnotation;
    }

    @Override
    public Object[][] getParameters(ITestContext context, Method method) {
        System.out.println("hello");
        YamlProviderImp yamlProviderImp = new YamlProviderImp("testdata/" ,c.value());
        return yamlProviderImp.getParameters(context, method);
    }
}
