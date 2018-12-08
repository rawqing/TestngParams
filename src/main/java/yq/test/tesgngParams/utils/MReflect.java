package yq.test.tesgngParams.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * Created by king on 16/11/23.
 */

public class MReflect {

    /**
     * 获取对象的所有定义的属性
     * @param object
     * @return
     */
    public static Field[] getDecFields(Object object){
        Class clz = object.getClass();
        Field [] fields = clz.getDeclaredFields();
        Field.setAccessible(fields , true);
        return fields;
    }

    /**
     * 匹配指定属性
     * @param fields
     * @param fieldName
     * @return
     */
    public static Field matcherField(Field[] fields , String fieldName){
        for(Field field : fields){
            if(field.getName().equals(fieldName))
                return field ;
        }
        return null;
    }

    /**
     * 获得匹配的属性 , 只匹配第一个被注解的属性
     * @param fields
     * @param annotation
     * @return
     */
    public static Field matcherField(Field[] fields , Class<? extends Annotation> annotation){
        Field.setAccessible(fields , true);
        for(Field field : fields){
            // 这里只获取使用了annotation注解过的的属性.
            if (field.isAnnotationPresent(annotation)) {
                return field;
            }
        }
        return null;
    }
    /**
     * 获取属性的值
     * @param field
     * @param object
     * @return
     */
    public static Object getFieldObject(Field field , Object object){
        field.setAccessible(true);
        try {
            return field.get(object);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static Object getFieldObject(String fieldName , Object object) {
        return getFieldObject(matcherField(getDecFields(object) , fieldName),object);
    }

    /**
     * 获取特定注解的属性的值
     *      只获取第一个被注解的属性值
     * @param fields
     * @param object
     * @param annotation
     * @return
     */
    public static Object getFieldObject(Field[] fields ,Object object ,Class<? extends Annotation> annotation){
        Field.setAccessible(fields , true);
        for(Field field : fields){
            try {
                // 这里只获取使用了annotation注解过的的属性.
                if (field.isAnnotationPresent(annotation)) {
                    return field.get(object);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    public static Object getFieldObject(Class<?> clz ,Object object ,Class<? extends Annotation> annotation){
        Field[] declaredFields = clz.getDeclaredFields();
        return getFieldObject(declaredFields, object, annotation);
    }
    /**
     * 通过 Class 获取对象
     * @param clz
     * @param <T>
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static <T> T getObject(Class<T> clz) {
        try {
            return clz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static Object getObject(String className){
        Class<?> clz = null;
        try {
            clz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return getObject(clz);
    }

    /**
     * 反射设置 属性的值
     * @param field
     * @param o
     * @param val
     */
    public static void setFieldValue(Field field, Object o, Object val) {
        field.setAccessible(true);
        try {
            field.set(o, val);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
