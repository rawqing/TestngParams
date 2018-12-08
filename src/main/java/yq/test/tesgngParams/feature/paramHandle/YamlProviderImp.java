package yq.test.tesgngParams.feature.paramHandle;

import yq.test.tesgngParams.feature.IParametersProvider;
import yq.test.tesgngParams.feature.annotations.YamlParameter;
import yq.test.tesgngParams.feature.annotations.YamlFilePath;
import yq.test.tesgngParams.feature.enums.YmlVer;
import org.testng.ITestContext;
import yq.test.tesgngParams.utils.HooksExplain;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static yq.test.tesgngParams.utils.DoIt.allList2array;
import static yq.test.tesgngParams.utils.DoIt.castObject;
import static yq.test.tesgngParams.utils.DoIt.notEmpty;
import static yq.test.tesgngParams.utils.HooksExplain.hasHook;
import static yq.test.tesgngParams.utils.YamlRW.readYamlFile;

public class YamlProviderImp implements IParametersProvider {
    private YamlParameter yamlParameter;
    private Class<?>[] parameterTypes;
    private Class<?> testClass;
    private String testMethodName;
    private YmlVer ymlVer = YmlVer.V1;
    private final String class_key = "class";
    private final String method_key = "method";

    private String ymlRootDir = "";
    private String ymlFilePath = "";

    private HooksExplain hooksExplain;

    public YamlProviderImp(){}
    public YamlProviderImp(String ymlRootDir ,String ymlFilePath){
        this.ymlRootDir = ymlRootDir;
        this.ymlFilePath = ymlFilePath;
    }
    @Override
    public void initialize(Annotation parametersAnnotation) {
        YamlFilePath yamlAnnot = testClass.getAnnotation(YamlFilePath.class);
        if (yamlAnnot != null) {
            this.ymlVer = yamlAnnot.ver();  // 获得版本
            this.ymlFilePath = yamlAnnot.value();
        }
    }

    @Override
    public Object[][] getParameters(ITestContext context, Method method) {
        this.yamlParameter = method.getAnnotation(YamlParameter.class);
        this.parameterTypes = method.getParameterTypes();
        this.testClass = method.getDeclaringClass();
        this.testMethodName = method.getName();

        this.initialize(this.yamlParameter);
        return getParameters();
    }

    /**
     * 获取yml文件的绝对路径 ( 通过@YmlParameter方法注解 或 @YamlFilePath类注解 )
     * @return 绝对路径
     */
    private InputStream getYmlInputStream(){
        String value = "";
        if (yamlParameter != null) {
            value = yamlParameter.value();
        }
        if ("".equals(value) && this.ymlFilePath != null) {
            value = this.ymlFilePath;
        }
        if (this.ymlRootDir != null) {
            value = ymlRootDir + value;
        }

        String path = Objects.requireNonNull(this.getClass().getClassLoader().getResource(value)).getPath();
        ymlFilePath = path;
        System.out.println(String.format("yml file path : %s", path));

        InputStream rs = Thread.currentThread().getContextClassLoader().getResourceAsStream(value);
        if (rs != null) {
            return rs;
        }
        try {
            return new FileInputStream(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取最终参数
     * @return
     */
    private Object[][] getParameters() {
        List params;
        switch (this.ymlVer) {
            case V2:
                params = getMethodParamsV2(getYmlInputStream());
                break;
            default:
                params = getMethodParams(getYmlInputStream());
        }

        if (params == null) {
            throw new RuntimeException("Empty parameters .");
        }
        List res = new ArrayList();
        for (Object po : params) {
            List data = null;
            // 将每行数据封装成 List
            if (po instanceof List) {
                data = (List) po;
            } else {
                data = new ArrayList();
                data.add(po);
            }

            res.add(createParams(data ,parameterTypes));
        }
        return allList2array(res);
    }
    /**
     * 创建参数
     * @param datas
     * @param types
     * @return
     */
    private Object[] createParams(List datas ,Class[] types){
        int dataSize = datas.size();
        int typeLen = types.length;
        // 判断参数个数与参数类型个数是否一致
        if (dataSize != typeLen) {
            throw new RuntimeException(String.format("参数个数(%s)与参数类型个数(%s)不匹配",
                    dataSize, typeLen));
        }
        Object[] o = new Object[typeLen];
        for(int i=0;i<typeLen; i++) {
            Object data = datas.get(i);
            // 解释 hook 字符串
            if (data instanceof String) {
                String str = (String)data;
                if (hasHook(str)) {
                    hooksExplain = hooksExplain != null ? hooksExplain : new HooksExplain();
                    str = hooksExplain.explainStringTemplate(str);
                    data = hooksExplain.sentenceRes(str);
                }
            }

            o[i] = castObject(types[i], data);
        }
        return o;
    }
    /**
     * 捕获 yml 文档中当前 test class 的所有 method 的数据
     *      每个文档必须包含 "class" , "method" 这两个key
     * @return method 这个 key 的value
     *      一般为 ArrayList
     */
    private Object getYamlMethods(InputStream in){
        List list = readYamlFile(ymlFilePath ,in);
        for (Object obj : list) {
            Map map = (Map) obj;
            Object aClass = map.get(class_key);
            if (this.testClass.getSimpleName().equals(aClass)) {
                return map.get(method_key);
            }
        }
        return null;
    }

    /**
     * 获取当前方法在 yml 文件中定义的参数
     * @return
     */
    private List getMethodParams(InputStream in){
        Object yamlObject = this.getYamlMethods(in);
        if (yamlObject == null) {
            throw new RuntimeException("Empty parameters .");
        }
        List datas = (List) yamlObject;
        for (Object o : datas) {
            Map map = (Map)o;
            if (testMethodName.equals(map.get("name"))) {
                return (List) map.get("data");
            }
        }
        return null;
    }

    /**
     * V2版本的 yml 文件参数提取方式
     * @param in
     * @return
     */
    private List getMethodParamsV2(InputStream in){
        List docs = readYamlFile(ymlFilePath ,in);
        for (Object doc : docs) {
            Map map = (Map) doc;
            String classKey = this.testClass.getSimpleName();
            if (map.containsKey(classKey)) {
                Map methodMap = (Map) map.get(classKey);
                if (methodMap.containsKey(testMethodName)) {
                    return (List) methodMap.get(testMethodName);
                }else
                    throw new RuntimeException(String.format("test class:%s 下未找到方法:%s", classKey, testMethodName));
            }else
                throw new RuntimeException(String.format("尚未为 test class[%s] 定义参数.", classKey));
        }
        throw new RuntimeException("Empty parameters .");
    }
}
