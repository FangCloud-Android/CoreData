package com.coredata.core.converter;

import com.coredata.core.PropertyConverter;
import com.coredata.core.utils.ConvertUtils;

/**
 * 字符串数组转换器，用";"分隔，建议简单的格式使用
 */
public class StringArrayConverter implements PropertyConverter<String[], String> {

    @Override
    public String convertToProperty(String[] strings) {
        return ConvertUtils.toString(strings, ";");
    }

    @Override
    public String[] convertToValue(String s) {
        return ConvertUtils.arrayFromString(s, ";");
    }
}
