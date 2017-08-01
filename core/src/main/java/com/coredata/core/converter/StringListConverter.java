package com.coredata.core.converter;

import com.coredata.core.PropertyConverter;
import com.coredata.core.utils.ConvertUtils;

import java.util.List;

/**
 * 字符串list转换器，用";"分隔，建议简单的格式使用
 */
public class StringListConverter implements PropertyConverter<List<String>, String> {

    @Override
    public String convertToProperty(List<String> strings) {
        return ConvertUtils.toString(strings, ";");
    }

    @Override
    public List<String> convertToValue(String s) {
        return ConvertUtils.listfromString(s, ";");
    }
}
