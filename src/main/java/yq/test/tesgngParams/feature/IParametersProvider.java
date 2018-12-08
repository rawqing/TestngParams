package yq.test.tesgngParams.feature;

import org.testng.ITestContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public interface IParametersProvider <A extends Annotation>{

    /**
     * 初始化一些数据
     * @param parametersAnnotation
     */
    void initialize(A parametersAnnotation);

    /**
     * 最终返回参数的方法
     * @param context
     * @param method
     * @return
     */
    Object[][] getParameters(ITestContext context, Method method);
}
