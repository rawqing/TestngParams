package yq.test.tesgngParams.utils;

import bsh.EvalError;
import bsh.Interpreter;
import jdk.nashorn.internal.runtime.regexp.joni.Regex;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static yq.test.tesgngParams.utils.DoIt.distinct;
import static yq.test.tesgngParams.utils.DoIt.hasShell;
import static yq.test.tesgngParams.utils.DoIt.removeSurrounding;


public class HooksExplain {
    private Interpreter sh = new Interpreter();
    private Map<String, String> mappingPath;
    private Map<String, Object> mappings;
    private final static String mappingYml = "mapping.yml";
    private final static String mappingProperties = "config.properties";

    public final static String prefix = "${";
    public final static String suffix = "}";
    public final static String bshObj = "$";
    public final static String varObj = "$";
    public final static String template_ = "`";
    // 不支持递归/嵌套
    public final static Pattern sentenceRegex = Pattern.compile("\\$\\{.+?}");
    public final static Pattern allClassName = Pattern.compile("^(\\b[a-z_][_A-Za-z0-9.]+\\b)$");
    public final static Pattern varRegex = Pattern.compile("\\$[a-z]([a-z]|[A-Z]|[0-9]|_)+");
    public final static Pattern customObjRegex = Pattern.compile("(?<!\\.)(\\b[a-z_][_A-Za-z0-9]*(?=\\.)\\b)(?!\\s)");


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
                setInstance(bshObj, getMappingObj(bshObj));
            }
            // 为句子注入自定义的对象实例
            List<String> list = getMatchedStr(sub, customObjRegex);
            if (list != null && !list.isEmpty()) {
                for (String key : list) {
                    setInstance(key ,getMappingObj(key));
                }
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
        List<String> list = getMatchedStr(str, varRegex);
        for (String s : list) {
            setInstance(s, pullVar(s));
        }
        return str;
    }

    /**
     * 获取所有匹配正则的字符串
     * @param str
     * @param regex
     * @return
     */
    private List<String> getMatchedStr(String str, Pattern regex) {
        Matcher matcher = regex.matcher(str);
        ArrayList<String> list = new ArrayList<>();
        while (matcher.find()) {
            list.add(matcher.group());
        }
        return distinct(list);
    }

    /**
     * 拉取变量值 ,使用 $ 映射的对象中定义的成员变量
     *
     * @param varName
     * @return
     */
    private Object pullVar(String varName) {
        Object obj = getMappingObj(varObj);
        return MReflect.getFieldObject(varName.replace(varObj, ""), obj);
    }

    /**
     * 通过可以获取 配置的对应class 的实例对象
     * mapping 文件可使用 resources 下的 mapping.yml || config.properties
     * 优先使用 mapping.yml
     *
     * @param key
     * @return
     * @deprecated 每次遇到变量都会读配置文件 , 多余的io开支 . use {@link #getMappingObj(String)}
     */
    private Object getMapping(String key) {
        // mapping.yml 方式
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(mappingYml);
        if (in != null) {
            Object o = YamlRW.readYamlFile(null, in).get(0);
            if (o instanceof Map) {
                Map map = (Map) o;
                return MReflect.getObject(map.get(key).toString());
            } else
                throw new RuntimeException(String.format("%s 文件格式不正确 , 需要为 map.", mappingYml));
        }

        // config.properties 方式
        Properties properties = new Properties();
        InputStream in2 = this.getClass().getClassLoader().getResourceAsStream(mappingProperties);
        try {
            properties.load(in2);
            String value = properties.getProperty(key);
            return MReflect.getObject(value);
        } catch (IOException e) {
            throw new RuntimeException("未正确配置路径", e);
        }

    }

    /**
     * 先将映射文件中的配置全提取出来 , 减少io
     * mapping.yml 只能配置对象映射 , 故将获取全部数据
     * properties 文件可能需要和其他配置复用 , 故需要筛选 value 为全类名格式的数据
     *
     * @return
     */
    public Map<String, String> getMappingPath() {
        // mapping.yml 方式
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(mappingYml);
        if (in != null) {
            Object o = YamlRW.readYamlFile(null, in).get(0);
            if (o instanceof Map) {
                Map map = (Map) o;
                Map<String, String> mappings = new HashMap<>();
                for (Object key : map.keySet()) {
                    mappings.put(key.toString(), map.get(key).toString());
                }
                return mappings;
            } else
                System.err.println(String.format("%s 文件格式不正确 , 需要为 map.", mappingYml));
        }

        // config.properties 方式
        Properties properties = new Properties();
        InputStream in2 = this.getClass().getClassLoader().getResourceAsStream(mappingProperties);
        try {
            properties.load(in2);
            Map<String, String> mappings = new HashMap<>();
            for (Object key : properties.keySet()) {
                Object value = properties.get(key);
                if (value instanceof String
                        && allClassName.matcher(value.toString()).matches()) {
                    mappings.put(key.toString(), value.toString());
                }
            }
            return mappings;
        } catch (IOException e) {
            System.err.println("未正确配置配置文件.");
            e.printStackTrace();
        }
        return new HashMap<>();
    }

    /**
     * 或取一个映射 key 的对象实例 , 并缓存至当前对象 , 减少重复反射开支.
     * @param key
     * @return
     */
    private Object getMappingObj(String key) {
        if (this.mappingPath == null) {
            this.mappingPath = getMappingPath();
        }
        if (this.mappings == null) {
            this.mappings = new HashMap<>();
        }
        if (mappings.containsKey(key)) {
            return mappings.get(key);
        } else {
            if (! mappingPath.containsKey(key)) {
                return key;
            }
            Object object = MReflect.getObject(mappingPath.get(key));
            mappings.put(key, object);
            return object;
        }

    }

    /**
     * set 一个变量进去
     */
    public void setInstance(String key, Object obj) {
        try {
            sh.set(key, obj);
        } catch (EvalError evalError) {
            evalError.printStackTrace();
        }
    }


    /**
     * 替换正则匹配的所有
     *
     * @param regex
     * @param str
     * @param fun   目标字符串 , 使用解释结果获得
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
