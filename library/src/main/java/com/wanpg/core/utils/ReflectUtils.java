package com.wanpg.core.utils;

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
     * @param clazz 获取变量的类
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

}
