package sk.annotation.library.jam.utils;

import java.util.Iterator;
import java.util.ServiceLoader;

abstract public class MapperInstanceUtil {
    private MapperInstanceUtil() {
        throw new IllegalStateException("abstract method");
    }


    public static final String constPostFixClassName = "JAMImpl";

    static public <T> T getMapper(Class<T> clsMapper) {
        T val = doGetMapper(clsMapper, clsMapper.getClassLoader());
        if (val == null) {
            val = doGetMapper(clsMapper, Thread.currentThread().getContextClassLoader());
        }
        if (val == null) {
            val = doGetMapper(clsMapper, MapperInstanceUtil.class.getClassLoader());
        }

        return val;
    }

    private static <T> T doGetMapper(Class<T> clazz, ClassLoader classLoader) {
        if (classLoader == null) return null;
        if (clazz == null) return null;

        try {
            return (T) classLoader.loadClass(clazz.getName() + constPostFixClassName).newInstance();
        } catch (ClassNotFoundException var6) {
            ServiceLoader<T> loader = ServiceLoader.load(clazz, classLoader);
            if (loader != null) {
                Iterator var4 = loader.iterator();

                while (var4.hasNext()) {
                    T mapper = (T) var4.next();
                    if (mapper != null) {
                        return mapper;
                    }
                }
            }

            return null;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
