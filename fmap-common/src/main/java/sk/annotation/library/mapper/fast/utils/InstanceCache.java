package sk.annotation.library.mapper.fast.utils;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Optional;

@SuppressWarnings("unchecked")
public class InstanceCache {
    protected final HashMap<String, IdentityHashMap<Object, IdentityHashMap<Object, Boolean>>> objCache = new HashMap<>();

    //    protected final IdentityHashMap<Object, HashMap<String, Optional<Object>>> objCache = new IdentityHashMap<>();
//    protected final IdentityHashMap<Object, HashMap<String, IdentityHashMap<Object, Boolean>>> objRegistered = new IdentityHashMap<>();
    public void put(Object in, Object out) {
        put(null, in, out);
    }

    public void put(String expectedType, Object in, Object out) {
        objCache
                .computeIfAbsent(expectedType, (b) -> new IdentityHashMap<>())
                .computeIfAbsent(in, (a) -> {
                    IdentityHashMap aa = new IdentityHashMap<>();
                    aa.put(null, out);
                    return aa;
                })
                .put(out, true);
    }

    public <T> Optional<T> get(Object in) {
        return get(null, in);
    }

    public <T> Optional<T> get(String expectedType, Object in) {
        IdentityHashMap<Object, IdentityHashMap<Object, Boolean>> cache = objCache.get(expectedType);
        if (cache == null) return null;

        IdentityHashMap<Object, Boolean> vals = cache.get(in);
        if (vals == null) return null;

        return Optional.ofNullable((T) vals.get(null));
    }

    public boolean isRegistered(String expectedType, Object in, Object out) {
        IdentityHashMap<Object, IdentityHashMap<Object, Boolean>> cache = objCache.get(expectedType);
        if (cache == null) return false;

        IdentityHashMap<Object, Boolean> vals = cache.get(in);
        if (vals == null) return false;

        return vals.getOrDefault(out, false);
    }

}
