package com.coredata.core.utils;

import android.util.Base64;

import com.coredata.core.io.ObjectInputStreamWrap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 转换工具类，用于将给定参数转换为目标结构
 */

public class ConvertUtils {

    private static void closeStream(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException ignored) {
            }
        }
    }

    @SuppressWarnings({"TryWithIdenticalCatches", "unchecked"})
    public static <T> T fromString(String s) {
        return fromString(s, null);
    }

    public static <T> T fromString(String s, Class<T> tClass) {
        if (s != null && !s.equals("")) {
            ObjectInputStream ois = null;
            try {
                byte[] data = Base64.decode(s, Base64.NO_WRAP);
                ois = new ObjectInputStreamWrap(new ByteArrayInputStream(data), tClass);
                return (T) ois.readObject();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                closeStream(ois);
            }
        }
        return null;
    }

    public static String toString(Serializable o) {
        if (o != null) {
            ByteArrayOutputStream baos = null;
            ObjectOutputStream oos = null;
            try {
                baos = new ByteArrayOutputStream();
                oos = new ObjectOutputStream(baos);
                oos.writeObject(o);
                return Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                closeStream(oos);
                closeStream(baos);
            }
        }
        return null;
    }

    public static List<String> listFromString(String s, String splitCharacter) {
        if (s != null && !s.equals("")) {
            String[] split = s.split(splitCharacter);
            List<String> tags = new ArrayList<>();
            if (split.length > 0) {
                Collections.addAll(tags, split);
            }
            return tags;
        }
        return null;
    }

    public static String toString(Collection<String> collection, String splitCharacter) {
        StringBuilder sb = new StringBuilder();
        if (collection != null) {
            boolean isFirst = true;
            for (String str : collection) {
                if (!isFirst) {
                    sb.append(splitCharacter);
                }
                sb.append(str);
                isFirst = false;
            }
        }
        return sb.toString();
    }

    public static String[] arrayFromString(String s, String splitCharacter) {
        if (s != null && !s.equals("")) {
            return s.split(splitCharacter);
        }
        return null;
    }

    public static String toString(String[] arrays, String splitCharacter) {
        StringBuilder sb = new StringBuilder();
        if (arrays != null) {
            boolean isFirst = true;
            for (String str : arrays) {
                if (!isFirst) {
                    sb.append(splitCharacter);
                }
                sb.append(str);
                isFirst = false;
            }
        }
        return sb.toString();
    }
}
