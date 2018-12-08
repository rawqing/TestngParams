package yq.test.tesgngParams.feature;

import yq.test.tesgngParams.feature.annotations.YamlParameter;
import yq.test.tesgngParams.feature.annotations.CustomParameter;
import yq.test.tesgngParams.feature.annotations.Params;
import yq.test.tesgngParams.feature.paramHandle.CustomParametersImp;
import yq.test.tesgngParams.feature.paramHandle.NoneProviderImp;
import yq.test.tesgngParams.feature.paramHandle.ParamsProviderImp;
import yq.test.tesgngParams.feature.paramHandle.YamlProviderImp;
import org.testng.ITestContext;
import org.testng.annotations.DataProvider;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class DataFeed {

    @DataProvider
    public  Object[][] getData(ITestContext context,Method method) {
        return getProvider(method).getParameters(context, method);
    }

    /**
     * 分发至不同的 provider 去处理数据
     * @param method
     * @return
     */
    private IParametersProvider getProvider(Method method) {
        if(method.isAnnotationPresent(YamlParameter.class)){
            return new YamlProviderImp();
        }
        if (method.isAnnotationPresent(Params.class)) {
            return new ParamsProviderImp();
        }
        return customParameters(method);
    }

    /**
     * 使用自定义的参数解析方式
     * @param m
     * @return
     */
    private IParametersProvider customParameters(Method m){
        Annotation[] annotations = m.getAnnotations();

        for (Annotation an : annotations) {
            CustomParameter targetAnnotation = an.annotationType().getAnnotation(CustomParameter.class);
            if (targetAnnotation!=null) {
                Class<? extends IParametersProvider> provider = targetAnnotation.provider();
                return new CustomParametersImp(an, provider);
            }
        }
        return new NoneProviderImp();
    }
}
