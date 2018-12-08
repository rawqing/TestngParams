package yq.test.tesgngParams.utils;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 读写yaml文件
 */
public class YamlRW {

    private static List list = null;
    private static String ymlPath = null;

    /**
     * 读取yaml文件中包含的 document ,并通过List方式返回
     * @param absolutePath yaml 文件的绝对路径
     * @return document list
     */
    public static List readYamlFile(String absolutePath ,InputStream in){
        if (absolutePath != null &&
                absolutePath.equals(ymlPath) &&
                list != null
        ) {
            return list;
        }
        list = new ArrayList();

        YamlReader reader = null;
        //优先使用流
        if (in != null) {
            reader = new YamlReader(new InputStreamReader(in));
        }else {
            try {
                if (absolutePath != null) {
                    File file = new File(absolutePath);
                    reader = new YamlReader(new FileReader(file));
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        while (true) {
            Object contact = null;
            try {
                contact = reader.read();
            } catch (YamlException e) {
                e.printStackTrace();
            }
            if (contact == null) break;
            list.add(contact);
        }
        ymlPath = absolutePath;
        return list;
    }


}
