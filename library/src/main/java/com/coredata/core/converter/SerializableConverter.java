package com.coredata.core.converter;

import com.coredata.core.PropertyConverter;
import com.coredata.core.utils.ConvertUtils;

import java.io.Serializable;

/**
 * Serializable 数据转换器
 */
public class SerializableConverter<T extends Serializable> implements PropertyConverter<T, String> {

    @Override
    public String convertToProperty(Serializable serializable) {
        return ConvertUtils.toString(serializable);
    }

    @Override
    public T convertToValue(String s) {
        return ConvertUtils.fromString(s);
    }
}
