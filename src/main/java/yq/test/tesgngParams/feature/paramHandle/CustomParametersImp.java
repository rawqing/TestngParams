package yq.test.tesgngParams.feature.paramHandle;

import yq.test.tesgngParams.feature.IParametersProvider;
import org.testng.ITestContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class CustomParametersImp implements IParametersProvider {
    private Annotation annotation;
    private Class<? extends IParametersProvider> provider;

    public CustomParametersImp(Annotation annotation, Class<? extends IParametersProvider> provider) {
        this.annotation = annotation;
        this.provider = provider;
    }

    @Override
    public void initialize(Annotation parametersAnnotation) {
        this.annotation = parametersAnnotation;
    }

    @Override
    public Object[][] getParameters(ITestContext context, Method method) {
        if (provider != null) {
            try {
                IParametersProvider iParametersProvider = provider.newInstance();
                iParametersProvider.initialize(this.annotation);
                return iParametersProvider.getParameters(context, method);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new Object[0][];
    }
}
