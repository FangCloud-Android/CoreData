package com.coredata.core.converter;

import com.coredata.core.CoreData;
import com.coredata.core.PropertyConverter;
import com.coredata.core.utils.ReflectUtils;

import java.lang.reflect.Type;

/**
 * 预设的json转换器
 *
 * @param <T> 转换器依赖类型，会自动填充
 */
public class JSONConverter<T> implements PropertyConverter<T, String> {

    private Type getType() {
        return ReflectUtils.getGenericityType(getClass());
    }

    @Override
    public String convertToProperty(T t) {
        return CoreData.getJSONAdapter().toString(t);
    }

    @Override
    public T convertToValue(String s) {
        return CoreData.getJSONAdapter().fromString(s, getType());
    }
}
