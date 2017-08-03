package com.wanpg.core.test;

import android.app.Application;

import com.coredata.core.CoreData;
import com.wanpg.core.test.model.Author;
import com.wanpg.core.test.model.Book;
import com.wanpg.core.test.model.Magazine;
import com.wanpg.core.test.model.Time;

import java.io.File;

/**
 * Created by wangjinpeng on 2017/6/4.
 */

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CoreData.init(this, CoreData.Builder.builder()
                .name(getExternalCacheDir() + File.separator + "test.db")
                .password("123456")
//                .name(getExternalCacheDir() + File.separator + "test_nopwd.db")
                .register(
                        Book.class,
                        Author.class,
                        Time.class,
                        Magazine.class)
                .version(13)
        );
    }
}
