package sk.annotation.library.jam.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;

abstract public class MapperUtil {
    private MapperUtil() {
        throw new IllegalStateException("abstract method");
    }


    public static final String constPostFixClassName = "JAMImpl";

    static public <T> T getMapper(Class<T> clsMapper) {
    	return getMapper(clsMapper, null);
	}
	static private final Map<String, Object> cache = new HashMap<>();
    static public <T> T getMapper(Class<T> clsMapper, Object fromOtherMapper) {
    	if (fromOtherMapper!=null) {
			cache.putIfAbsent(fromOtherMapper.getClass().getCanonicalName(), fromOtherMapper);
		}
    	String cacheKey = clsMapper.getName() + constPostFixClassName;
    	Object o = cache.get(cacheKey);
    	if (o != null) {
    		return (T) o;
		}


        T val = doGetMapper(clsMapper, clsMapper.getClassLoader());
        if (val == null) {
            val = doGetMapper(clsMapper, Thread.currentThread().getContextClassLoader());
        }
        if (val == null) {
            val = doGetMapper(clsMapper, MapperUtil.class.getClassLoader());
        }

        if (val != null) {
        	cache.put(cacheKey, val);
		}

        return val;
    }

    private static <T> T doGetMapper(Class<T> clazz, ClassLoader classLoader) {
        if (classLoader == null) return null;
        if (clazz == null) return null;

        try {
            return (T) classLoader.loadClass(clazz.getName() + constPostFixClassName).getDeclaredConstructor().newInstance();
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
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
