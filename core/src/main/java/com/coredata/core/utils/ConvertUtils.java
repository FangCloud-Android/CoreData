package com.coredata.core.utils;

import android.util.Base64;

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
 * Created by wangjinpeng on 2017/6/26.
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
        if (s != null) {
            ObjectInputStream ois = null;
            try {
                byte[] data = Base64.decode(s, Base64.NO_WRAP);
                ois = new ObjectInputStream(new ByteArrayInputStream(data));
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

    public static List<String> listfromString(String s, String splitCharacter) {
        if (s != null) {
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

    public static String[] arrayfromString(String s, String splitCharacter) {
        if (s != null) {
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
