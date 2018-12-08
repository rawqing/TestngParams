package yq.test.tesgngParams.utils;

import bsh.EvalError;
import bsh.Interpreter;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static yq.test.tesgngParams.utils.DoIt.distinct;
import static yq.test.tesgngParams.utils.DoIt.hasShell;
import static yq.test.tesgngParams.utils.DoIt.removeSurrounding;


public class HooksExplain {
    private Interpreter sh = new Interpreter();
    private final static String mappingYml = "mapping.yml";
    private final static String mappingProperties = "config.properties";

    public final static String prefix = "${";
    public final static String suffix = "}";
    public final static String bshObj = "$";
    public final static String varObj = "$";
    public final static String template_ = "`";
    // 不支持递归/嵌套
    public final static Pattern sentenceRegex = Pattern.compile("\\$\\{.+?}");
    public final static Pattern varRegex = Pattern.compile("\\$[a-z]([a-z]|[A-Z]|[0-9]|_)+");


    public static boolean hasHook(String str) {
        return hasShell(str, prefix, suffix) || hasShell(str, template_, template_);
    }
    /**
     * 解释字符串模板
     *
     * @param str
     * @return
     */
    public String explainStringTemplate(String str) {
        if (!hasShell(str, template_, template_))
            return str;
        str = removeSurrounding(str, template_, template_);
        // 先解释句子
        str = replaceAll(sentenceRegex, str, s -> sentenceRes(s).toString());
        // 再解释变量
        str = replaceAll(varRegex, str, s -> pullVar(s).toString());
        return str;
    }


    /**
     * 执行一条语句并返回结果
     *
     * @param str
     * @return
     */
    public Object sentenceRes(String str) {
        if (!hasShell(str, prefix, suffix))
            return str;
        //为句子去壳 ${  }
        String sub = removeSurrounding(str, prefix, suffix);
        // 为语句注入变量 , 如果包含变量的话
        sub = injectVar(sub);
        try {
            // 为句子注入 $ 映射的对象实例
            if (sub.contains(bshObj)) {
                sh.set(bshObj, getMapping(bshObj));
            }
            // 执行语句
            return sh.eval(sub);
        } catch (EvalError evalError) {
            evalError.printStackTrace();
            return null;
        }

    }

    /**
     * 为句子注入变量 (以$加字符为整体的 )
     *
     * @param str
     * @return
     */
    private String injectVar(String str) {
        Matcher matcher = varRegex.matcher(str);
        ArrayList<String> list = new ArrayList<>();
        while (matcher.find()) {
            list.add(matcher.group());
        }
        distinct(list);
        for (String s : list) {
            setInstance(s, pullVar(s));
        }
        return str;
    }

    /**
     * 拉取变量值 ,使用 $ 映射的对象中定义的成员变量
     *
     * @param varName
     * @return
     */
    private Object pullVar(String varName) {
        Object obj = getMapping(varObj);
        return MReflect.getFieldObject(varName.replace(varObj,""), obj);
    }

    /**
     * 通过可以获取 配置的对应class 的实例对象
     * mapping 文件可使用 resources 下的 mapping.yml || config.properties
     *  优先使用 mapping.yml
     * @param key
     * @return
     */
    private Object getMapping(String key) {
        // mapping.yml 方式
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(mappingYml);
        if (in != null) {
            Object o = YamlRW.readYamlFile(null, in).get(0);
            if (o instanceof Map) {
                Map map = (Map) o;
                return MReflect.getObject(map.get(key).toString());
            }else
                throw new RuntimeException(String.format("%s 文件格式不正确 , 需要为 map.", mappingYml));
        }

        // config.properties 方式
        Properties properties = new Properties();
        InputStream in2 = this.getClass().getClassLoader().getResourceAsStream(mappingProperties);
        try {
            properties.load(in2);
            String value =  properties.getProperty(key);
            return MReflect.getObject(value);
        } catch (IOException e) {
            throw new RuntimeException("未正确配置路径", e);
        }

    }

    /**
     * set 一个变量进去
     */
    public void setInstance(String str, Object obj) {
        try {
            sh.set(str, obj);
        } catch (EvalError evalError) {
            evalError.printStackTrace();
        }
    }


    /**
     * 替换正则匹配的所有
     * @param regex
     * @param str
     * @param fun 目标字符串 , 使用解释结果获得
     * @return
     */
    private String replaceAll(Pattern regex, String str, MFunc fun) {
        Matcher matches = regex.matcher(str);
        while (matches.find()) {
            String sub = matches.group();

            str = str.replace(sub, fun.runFunc(sub));
        }
        return str;
    }

}
