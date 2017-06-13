package com.coredata.core.utils;

import com.coredata.core.CoreDao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by wangjinpeng on 2017/3/21.
 */
public class ReflectUtils {

    /**
     * 获取所有的变量，包括父类的
     *
     * @param clazz    获取变量的类
     * @param endClazz 获取变量的终点
     * @return
     */
    public static List<Field> getAllFields(Class clazz, Class endClazz) {
        if (clazz == null || clazz.getName().equals(endClazz.getName())) {
            return new ArrayList<>();
        } else {
            List<Field> list = new ArrayList<>();
            Collections.addAll(list, clazz.getDeclaredFields());
            list.addAll(getAllFields(clazz.getSuperclass(), endClazz));
            return list;
        }
    }

    public static <T> CoreDao<T> getGeneratedEntityDaoImpl(Class<T> tClass) {
        // 获取全名
        String name = tClass.getSimpleName();
        String suffix = "CoreDaoImpl";
        String daoImplName = name + suffix;
        String daoPackage = tClass.getPackage().getName();
        //noinspection TryWithIdenticalCatches
        try {
            Class<?> aClass = Class.forName(daoPackage.isEmpty() ? daoImplName : daoPackage + "." + daoImplName);
            return (CoreDao<T>) aClass.newInstance();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("cannot find implementation for "
                    + tClass.getCanonicalName() + ". " + daoImplName + " does not exist");
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Cannot access the constructor"
                    + tClass.getCanonicalName());
        } catch (InstantiationException e) {
            throw new RuntimeException("Failed to create an instance of "
                    + tClass.getCanonicalName());
        }
    }
}
