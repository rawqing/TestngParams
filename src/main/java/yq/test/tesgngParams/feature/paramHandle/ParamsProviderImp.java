package yq.test.tesgngParams.feature.paramHandle;

import yq.test.tesgngParams.feature.IParametersProvider;
import yq.test.tesgngParams.feature.annotations.Params;
import yq.test.tesgngParams.utils.DoIt;
import org.testng.ITestContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;

public class ParamsProviderImp implements IParametersProvider {
    private Method method = null;

    @Override
    public void initialize(Annotation parametersAnnotation) {

    }

    @Override
    public Object[][] getParameters(ITestContext context, Method method) {
        this.method = method;
        return getParameters();
    }

    private Object[][] getParameters(){
        Params annotation = method.getAnnotation(Params.class);
        String[] params = annotation.value();
        if (params.length < 1) {
            throw new RuntimeException("未获取到有效参数 , 预期:String[] ; 实际: "+ Arrays.toString(params));
        }
        Class<?>[] parameterTypes = method.getParameterTypes();
        Object[][] os = new Object[params.length][parameterTypes.length];
        int i=0;
        for (String ss : params) {
            String[] ps = ss.split(",");
            Object[] objects = createParams(ps, parameterTypes);
            os[i] = objects;
            i++;
        }
        return os;
    }

    private Object[] createParams(String[] datas , Class[] types){
        int dataSize = datas.length;
        int typeLen = types.length;
        // 判断参数个数与参数类型个数是否一致
        if (dataSize != typeLen) {
            throw new RuntimeException(String.format("参数个数(%s)与参数类型个数(%s)不匹配",
                    dataSize, typeLen));
        }
        Object[] o = new Object[typeLen];
        for(int i=0;i<typeLen; i++) {
            Object data = datas[i].trim();
            o[i] = DoIt.castObject(types[i], data);
        }
        return o;
    }
}
