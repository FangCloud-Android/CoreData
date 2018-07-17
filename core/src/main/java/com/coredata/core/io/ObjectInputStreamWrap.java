package com.coredata.core.io;

import java.io.*;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@link ObjectInputStream}包装类，
 * <p>
 * 为了保证Class变更后 Serializable序列化的数据可以最大程度反序列化成功
 */
public class ObjectInputStreamWrap extends ObjectInputStream {

    private static final Map<Long, Class<? extends Serializable>> classMap = new ConcurrentHashMap<>();

    public static void registerClass(Class<? extends Serializable> aClass) {
        if (classMap.containsValue(aClass)) {
            return;
        }
        try {
            Field serialVersionUID = aClass.getDeclaredField("serialVersionUID");
            serialVersionUID.setAccessible(true);
            Object o = serialVersionUID.get(aClass);
            serialVersionUID.setAccessible(false);
            if (o != null) {
                Long value = Long.valueOf(o.toString());
                classMap.put(value, aClass);
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private Class<?> aClass;

    public ObjectInputStreamWrap(InputStream in, Class<?> aClass) throws IOException {
        super(in);
        this.aClass = aClass;
    }

    public ObjectInputStreamWrap(InputStream in) throws IOException {
        super(in);
    }

    protected ObjectInputStreamWrap() throws IOException, SecurityException {
    }

    @Override
    protected ObjectStreamClass readClassDescriptor() throws IOException, ClassNotFoundException {
        ObjectStreamClass objectStreamClass = super.readClassDescriptor();
        Class<?> aClass1 = aClass;
        if (aClass1 == null) {
            aClass1 = classMap.get(objectStreamClass.getSerialVersionUID());
        }
        if (aClass1 != null) {
            if (!aClass1.getName().equals(objectStreamClass.getName())) {
                try {
                    @SuppressWarnings("JavaReflectionMemberAccess")
                    Field name = ObjectStreamClass.class.getDeclaredField("name");
                    name.setAccessible(true);
                    name.set(objectStreamClass, aClass1.getName());
                    name.setAccessible(false);
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return objectStreamClass;
    }
}
