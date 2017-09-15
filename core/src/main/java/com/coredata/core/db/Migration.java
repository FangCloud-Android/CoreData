package com.coredata.core.db;

/**
 * Created by wangjinpeng on 2017/9/15.
 */

public interface Migration {

    void onStart(CoreDatabase db, int oldVersion, int newVersion);

    void onEnd(CoreDatabase db, int oldVersion, int newVersion);
}
