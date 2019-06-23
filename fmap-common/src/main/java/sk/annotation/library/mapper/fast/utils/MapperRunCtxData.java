package sk.annotation.library.mapper.fast.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
        if (instanceCache == null) instanceCache = new InstanceCache();
        return instanceCache;
    }

    public Object testTransform(Object o1, Object ret1) {
        if (o1 == null) return ret1;

        if (ret1 == null) {
            Optional<Object> ret = instanceCache.get("testTransform", o1);
            if (ret != null) return ret.orElse(null);

            ret1 = new Object();
        }
        else if (instanceCache.isRegistered("testTransform", o1, ret1)) {

        }



        // copy values

        return ret1;


    }

}
