package sk.annotation.library.mapper.fast.utils.context;

import java.util.*;

public class MapperContextData {
	protected MapperContextData() {}

	////////////////////////////////////////////////////////////////////////
	// Context Values
	private LinkedList<Integer> topMethodsScope = new LinkedList<>();
	private Map<String, Object> ctxValues = null;
	void putContextValue(String key, Object value) {
		if (value == null && ctxValues == null) return;
		if (ctxValues==null) ctxValues = new HashMap<>();
		ctxValues.put(key, value);
	}
	<T> T getContextValue(String key) {
		return (T) ctxValues.get(key);
	}

	void startMethodScope(Integer scope) {
		topMethodsScope.addFirst(scope);
	}
	Integer getMethodScope() {
		return topMethodsScope.getFirst();
	}
	void endMethodScope() {
		topMethodsScope.removeFirst();
	}

	////////////////////////////////////////////////////////////////////////
	// Cache implementation
	private Map<String, List<Object[]>> usedInputs = new HashMap<>();
	private Map<String, Optional<Object>> retValues = new HashMap<>();

	private String getOrCreateCacheKeyForInputs(String expectedType, Object[] objs) {
		if (objs == null || objs.length == 0) return null;

		String hash = expectedType + ":" + Arrays.hashCode(objs);

		List<Object[]> knownInputs = usedInputs.computeIfAbsent(hash, (a) -> new LinkedList<>());
		int ind=0;
		for (Object[] knownInput : knownInputs) {
			if (sameInputInstances(knownInput, objs)) return hash + "_" + ind;
			ind++;
		}

		knownInputs.add(objs);
		ind++;
		return hash + "_" + ind;
	}
	private static boolean sameInputInstances(Object[] inp1, Object[] inp2) {
		if ((inp1 == null || inp1.length==0) && (inp2 == null || inp2.length==0)) return true;
		if (inp1 == null || inp2 == null) return false;
		if (inp1.length != inp2.length) return false;

		for (int i=0; i<inp1.length; i++) {
			// Instance compare !!!
			if (inp1[i]!=inp2[i]) return false;
		}
		return true;
	}

	<T> T getCacheOrCallTransform(String expectedType, Object[] inputs, T retDefault, IConstructor<T> constructor, IConsumer<T> filler) throws Exception {
		String key = getOrCreateCacheKeyForInputs(expectedType, inputs);

		// If inputs cannot by cached
		if (key == null) return callTransform(retDefault, constructor, filler);

		Optional<? extends T> ret = (Optional<? extends T>) retValues.get(key);
		if (ret == null) {
			ret = Optional.ofNullable(callTransform(retDefault, constructor, filler));
			retValues.put(key, (Optional)ret);
		}
		return ret.orElse(null);
	}

	protected <T> T callTransform(T retDefault, IConstructor<T> constructor, IConsumer<T> filler) throws Exception {
		if (retDefault == null) retDefault = constructor.get();

		if (filler != null) filler.accept(retDefault);

		return retDefault;
	}
}
