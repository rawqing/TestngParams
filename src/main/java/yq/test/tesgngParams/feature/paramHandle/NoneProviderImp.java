package yq.test.tesgngParams.feature.paramHandle;

import yq.test.tesgngParams.feature.IParametersProvider;
import org.testng.ITestContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class NoneProviderImp implements IParametersProvider {
    @Override
    public void initialize(Annotation parametersAnnotation) {

    }

    @Override
    public Object[][] getParameters(ITestContext context, Method method) {
        return new Object[0][];
    }
}
