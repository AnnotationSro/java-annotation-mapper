package sk.annotation.library.mapper.fast.utils.context;

import java.util.Iterator;
import java.util.ServiceLoader;

public class MapperUtil {
	MapperUtil() {
	}

	static private final ThreadLocal<MapperContextData> threadCtx = new ThreadLocal<>();

	private static <T> T doWithReturn(IFunction<MapperContextData, T> runner) {
		MapperContextData ctx = threadCtx.get();
		boolean createdContext = false;
		try {
			if (ctx == null) {
				createdContext = true;
				ctx = new MapperContextData();
				threadCtx.set(ctx);
			}

			return runner.apply(ctx);
		} catch (Exception e) {
			if (e instanceof RuntimeException) throw (RuntimeException) e;
			throw new IllegalStateException(e);
		} finally {
			if (createdContext) threadCtx.remove();
		}
	}


	////////////////////////////////////////////////////////////////////////
	// Context values
	static public void putContextValue(String key, Object value) {
		MapperContextData ctx = threadCtx.get();
		if (ctx == null) return;
		ctx.putContextValue(key, value);
	}

	static public <T> T getContextValue(String key) {
		MapperContextData ctx = threadCtx.get();
		if (ctx == null) return null;
		return (T) ctx.getContextValue(key);
	}

	static public int getCurrentMethodContext() {
		MapperContextData ctx = threadCtx.get();
		if (ctx == null) return 0;
		Integer ctxMethod = ctx.getMethodScope();
		if (ctxMethod==null) return 0;
		return ctxMethod;
	}

	////////////////////////////////////////////////////////////////////////
	// Cache implementation
	static private boolean isNull(Object[] objs) {
		if (objs == null || objs.length == 0) return true;
		for (Object o : objs) {
			if (o != null) return false;
		}
		return true;
	}

	static public <T, C extends IConstructor<T>> T doTransform(String expectedType, Object[] inputs, T retDefault, C constructor, IConsumer<T> callOn) {
		if (isNull(inputs)) return retDefault;

		return doWithReturn(ctx -> {
			return ctx.getCacheOrCallTransform(expectedType, inputs, retDefault, constructor, callOn);
		});
	}

	static public <T> T doTransform(Integer methodContext, ICaller before, IConstructor<T> transform) {
		return doWithReturn(ctx -> {
			ctx.startMethodScope(methodContext);
			try {
				if (before != null) before.call();
				return transform.get();
			} finally {
				ctx.endMethodScope();
			}
		});
	}

	public static final String constPostFixClassName = "MapperImpl";

	static public <T> T getMapper(Class<T> clsMapper) {
		T val = doGetMapper(clsMapper, clsMapper.getClassLoader());
		if (val == null) {
			val = doGetMapper(clsMapper, Thread.currentThread().getContextClassLoader());
		}
		if (val == null) {
			val = doGetMapper(clsMapper, MapperUtil.class.getClassLoader());
		}

		return val;
	}

	private static <T> T doGetMapper(Class<T> clazz, ClassLoader classLoader) {
		if (classLoader == null) return null;
		if (clazz == null) return null;

		try {
			return (T) classLoader.loadClass(clazz.getName() + constPostFixClassName).newInstance();
		}
		catch (ClassNotFoundException var6) {
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
