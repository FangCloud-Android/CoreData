package com.coredata.core.utils;

import android.util.Log;

import java.util.HashMap;

import io.reactivex.annotations.NonNull;

public class Debugger {

    @NonNull
    private static String tag;
    private static boolean enable = true;

    public static void setTag(String tag) {
        Debugger.tag = tag;
    }

    public static void setEnable(boolean enable) {
        Debugger.enable = enable;
    }

    public static void d(Object... infos) {
        if (!enable) {
            return;
        }
        if (infos == null || infos.length <= 0) {
            return;
        }
        StringBuilder builder = new StringBuilder();
        for (Object info : infos) {
            if (info == null) {
                builder.append("null");
            } else {
                builder.append(String.valueOf(info));
            }
        }
        Log.d(tag, builder.toString());
    }

    public static void i(String... infos) {
        if (!enable) {
            return;
        }
        if (infos == null || infos.length <= 0) {
            return;
        }
        StringBuilder builder = new StringBuilder();
        for (Object info : infos) {
            if (info == null) {
                builder.append("null");
            } else {
                builder.append(String.valueOf(info));
            }
        }
        Log.i(tag, builder.toString());
    }
}