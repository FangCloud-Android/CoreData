package com.coredata.core.db;

/**
 * Created by wangjinpeng on 2017/8/1.
 */

public interface OpenHelperInterface {

    CoreDatabase getWritableCoreDatabase();

    CoreDatabase getReadableCoreDatabase();

}
