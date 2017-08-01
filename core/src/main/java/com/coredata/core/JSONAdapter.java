package com.coredata.core;

import java.lang.reflect.Type;

/**
 * Created by wangjinpeng on 2017/6/26.
 */

public interface JSONAdapter {

    String toString(Object o);

    <T> T fromString(String s, Type type);
}
