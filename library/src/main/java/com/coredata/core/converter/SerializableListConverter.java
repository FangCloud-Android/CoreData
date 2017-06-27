package com.coredata.core.converter;

import com.coredata.core.PropertyConverter;
import com.coredata.core.utils.ConvertUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Serializable List 数据转换器
 */
public class SerializableListConverter<T extends Serializable>
        implements PropertyConverter<List<T>, String> {

    @Override
    public String convertToProperty(List<T> tList) {
        if (tList != null) {
            return ConvertUtils.toString(new ArrayList<>(tList));
        }
        return null;
    }

    @Override
    public List<T> convertToValue(String s) {
        return ConvertUtils.fromString(s);
    }
}
