package yq.test.tesgngParams.feature.listener;

import yq.test.tesgngParams.feature.DataFeed;
import yq.test.tesgngParams.feature.annotations.CustomParameter;
import yq.test.tesgngParams.feature.annotations.YamlParameter;
import yq.test.tesgngParams.feature.annotations.Params;
import org.testng.*;
import org.testng.annotations.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

public class ParamsListener implements IInvokedMethodListener, ITestListener {
    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {

//        System.out.println("beforeInvocation");
    }

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
//        System.out.println("afterInvocation");
    }

    @Override
    public void onTestStart(ITestResult result) {
//        System.out.println("onTestStart");
    }

    @Override
    public void onTestSuccess(ITestResult result) {
//        System.out.println("onTestSuccess");
    }

    @Override
    public void onTestFailure(ITestResult result) {
//        System.out.println("onTestFailure");
    }

    @Override
    public void onTestSkipped(ITestResult result) {
//        System.out.println("onTestSkipped");
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
//        System.out.println("onTestFailedButWithinSuccessPercentage");
    }

    @Override
    public void onStart(ITestContext context) {
        System.out.println("ParamsListener : onStart");
        ITestNGMethod[] am = context.getAllTestMethods();
        for (ITestNGMethod method : am) {
            Method m = method.getConstructorOrMethod().getMethod();
            if(m.isAnnotationPresent(YamlParameter.class)
                    || m.isAnnotationPresent(Params.class)
                    || isCustomParameters(m)) {
                setPod(m);
            }
        }
//        System.out.println("onStart");
    }

    private boolean isCustomParameters(Method method) {
        Annotation[] annotations = method.getAnnotations();
        for (Annotation an : annotations) {
            CustomParameter targetAnnotation = an.annotationType().getAnnotation(CustomParameter.class);
            if (targetAnnotation!=null) {
                return true;
            }
        }
        return false;
    }
    public void setPod(Method method){
        try {
            Test test = method.getAnnotation(Test.class);
            InvocationHandler invocationHandler = Proxy.getInvocationHandler(test);
            Field declaredField = invocationHandler.getClass().getDeclaredField("memberValues");
            declaredField.setAccessible(true);
            Map memberValues = (Map) declaredField.get(invocationHandler);
            // 修改 value 属性值
            memberValues.put("dataProviderClass", DataFeed.class);
            memberValues.put("dataProvider", "getData");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onFinish(ITestContext context) {
//        System.out.println("onFinish");
    }
}
