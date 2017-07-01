package com.wanpg.core.test;

import android.app.Application;

import com.coredata.core.CoreData;
import com.coredata.core.JSONAdapter;
import com.wanpg.core.test.model.Author;
import com.wanpg.core.test.model.Book;
import com.wanpg.core.test.model.Magazine;
import com.wanpg.core.test.model.Time;

import java.io.File;
import java.lang.reflect.Type;

/**
 * Created by wangjinpeng on 2017/6/4.
 */

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CoreData.init(this, CoreData.Builder.builder()
                .name(getExternalCacheDir() + File.separator + "test.db")
                .register(
                        Book.class,
                        Author.class,
                        Time.class,
                        Magazine.class)
                .registerJSONAdapter(new JSONAdapter() {
                    @Override
                    public String toString(Object o) {
                        return null;
                    }

                    @Override
                    public <T> T fromString(String s, Type type) {
                        return null;
                    }
                })
                .version(13));
    }
}
