package yq.test.tesgngParams.utils;


import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by king on 2016/9/9.
 * 功能: 做一些事情
 */
public class DoIt {

    /*************************/
    public static void sleep(long time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /***********************************/
    public static Date dateValueOf(String date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /********************************/

    public static String string2ASCII(String string){
        String newString = "";
        char[] chars = string.toCharArray();
        for(char c: chars){
            newString = newString + Integer.valueOf(c) + ",";
        }
        return newString;
    }

    /**
     * 进制转换 , 尚未提供10进制以下的转换功能
     * @param i 十进制数
     * @param scale must be 10 < scale < 71
     * @return 指定进制的字符串
     */
    public static String conversionScale(long i , int scale){
        char[] chars = new char[scale];
        char ch = 'A';
        char nu = '0';
        long m = 0;  //商
        long y = 0;  //余
        int index = chars.length;
        long c = 0;  //差

        do{
            m = i / scale;
            y = i % scale;
            c = y - 10;
            chars[--index] = (char) (c < 0 ? nu + y : ch + c);
            i = m;
        }while (m!=0);

        return new String(chars ,index ,chars.length-index);
    }


    /**
     * long 转16进制字符串大写格式
     * @param number
     * @return
     */
    public static String toHexUpString(long number){
        return Long.toHexString(number).toUpperCase();
    }

    /**
     * 16进制字符串转int格式
     * @param hex
     * @return
     */
    public static int hexString2int(String hex){
        Long l =  Long.parseLong(hex ,16);
        return l.intValue();
    }

    public static boolean notEmpty(Object o){
       return o !=null;
    }
    public static boolean notEmpty(String s){
        return s != null && s.length() > 0;
    }
    public static boolean notEmpty(Object[] o) {
        return o != null && o.length > 0;
    }
    public static boolean notEmpty(List o) {
        return o != null && !o.isEmpty();
    }
    public static boolean notEmpty(Map o) {
        return o != null && !o.isEmpty();
    }

    public static Object[] list2array(List list) {
        int size = list.size();
        if (size < 1) {
            return null;
        }
        Object[] es =  new Object[size];
        for(int i=0; i<size ; i++) {
            es[i] = list.get(i);
        }
        return es;
    }
    public static Object[][] allList2array(List list) {
        int size = list.size();
        if (size < 1) {
            return null;
        }
        int subSize = ((Object[])list.get(0)).length;
        Object[][] es =  new Object[size][subSize];
        for(int i=0; i<size ; i++) {
            Object[] o = (Object[]) list.get(i);
            System.arraycopy(o, 0, es[i], 0, subSize);
        }
        return es;
    }

    /**
     * 类型转换
     * @param clz
     * @param data
     * @return
     */
    public static Object castObject(Class clz, Object data) {
        //int
        if (clz == Integer.class || clz == int.class) {
            if(data instanceof Integer) return data;
            return Integer.valueOf((String)data);
        }
        //string
        if (clz == String.class) {
            if(data instanceof String )return data;
            return String.valueOf(data);
        }
        //float
        if (clz == Float.class || clz == float.class) {
            data = String.valueOf(data);
            return Float.valueOf((String)data);
        }
        //double
        if (clz == Double.class || clz == double.class) {
            data = String.valueOf(data);
            return Double.valueOf((String)data);
        }
        //long
        if (clz == Long.class || clz == long.class) {
            data = String.valueOf(data);
            return Long.valueOf((String)data);
        }
        //boolean
        if (clz == Boolean.class || clz == boolean.class) {
            if(data instanceof Boolean)
                return data;
            if(!(data instanceof String))
                data = String.valueOf(data);
            return Boolean.valueOf((String) data);
        }
        if (clz == BigDecimal.class) {
            if (data instanceof BigDecimal) return data;
            data = String.valueOf(data);
            return new BigDecimal((String)data);
        }
        if (clz.isEnum()) {
            return Enum.valueOf(clz, (String) data);
        }
        System.out.println("没有合适的类型 : "+ clz);
        return data;
    }

    /**
     * 简单的创建一个 HashMap
     * @param k
     * @param v
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K,V> Map<K,V> createMap(K k ,V v){
        Map<K,V> map = new HashMap<>();
        map.put(k, v);
        return map;
    }

    /**
     * 校验字符串是否包含一个壳
     * @param str
     * @param prefix
     * @param suffix
     * @return
     */
    public static Boolean hasShell(String str ,String prefix ,String suffix){
        return str.startsWith(prefix) && str.endsWith(suffix);
    }

    /**
     * 删除壳
     * @param str
     * @param prefix
     * @param suffix
     * @return
     */
    public static String removeSurrounding(String str ,String prefix ,String suffix){
        if ((str.length() >= prefix.length() + suffix.length()) && str.startsWith(prefix) && str.endsWith(suffix)) {
            return str.substring(prefix.length(), str.length() - suffix.length());
        }
        return str;
    }

    /**
     * list 内容去重
     * @param list
     * @param <T>
     * @return
     */
    public static <T>List distinct(List<T> list) {
        HashSet<T> h = new HashSet<>(list);
        list.clear();
        list.addAll(h);
        return list;
    }

    /**
     * 简单转换时间
     * @param date 时间字符串
     * @param simpleFormat 时间格式
     * @return
     */
    public static long getSimpleTime(String simpleFormat, String date) {
        try {
            return new SimpleDateFormat(simpleFormat , Locale.CHINA).parse(date).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
