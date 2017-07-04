package com.coredata.core.db;

import com.coredata.core.BaseSet;

/**
 * Created by wangjinpeng on 2017/7/4.
 */

public interface UpdateDeleteSetInterface<T> extends SetInterface<T> {

    boolean execute();

    UpdateDeleteWhere<UpdateDeleteSetInterface<T>, ? extends BaseSet<T>, T> where(String columnName);
}
