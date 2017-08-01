package com.coredata.core;

/**
 * Created by wangjinpeng on 2017/6/3.
 */

public interface PropertyConverter<Value, Property> {

    Property convertToProperty(Value value);

    Value convertToValue(Property property);
}
