package sk.annotation.library.mapper.jam.utils;

import sk.annotation.library.mapper.jam.utils.cache.DefaultInstanceCache;
import sk.annotation.library.mapper.jam.utils.cache.InstanceCache;

import java.util.HashMap;
import java.util.Map;

public class MapperRunCtxData {
    private Map<String, Object> ctxVals = new HashMap<>();
    public void putContextValue(String ctxKey, Object ctxVal) {
        if (ctxVals == null) ctxVals = new HashMap<>();
        ctxVals.put(ctxKey, ctxVal);
    }
    public <T> T getContextValue(String ctxKey) {
        if (ctxVals == null) return null;
        return (T) ctxVals.get(ctxKey);
    }


    protected InstanceCache instanceCache = null;
    public InstanceCache getInstanceCache() {
        if (instanceCache == null) instanceCache = new DefaultInstanceCache();
        return instanceCache;
    }
}
