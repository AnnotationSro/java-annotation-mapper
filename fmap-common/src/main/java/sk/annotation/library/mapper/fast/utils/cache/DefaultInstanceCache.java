package sk.annotation.library.mapper.fast.utils.cache;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class DefaultInstanceCache extends HashMap<Object, InstanceKeyValueMap6> implements InstanceCache {

    @SuppressWarnings("unchecked")
    @Override
    public <T> InstanceCacheValue<T> getCacheValues(int key, Object in) {
        InstanceKeyValueMap6 v;
        if ((v = this.get(in)) == null) {
            v = new InstanceKeyValueMap6(key, in);
            super.put(in, v);
            return v;
        }
        return v.find(key, in);
    }
}

class DefaultInstanceCacheValue<T> implements InstanceCacheValue<T> {
    final protected int key;
    final protected Object in;

    private T value;
    private boolean registered = false;
    private List<T> otherValues = null;

    public DefaultInstanceCacheValue(int key, Object in) {
        this.in = in;
        this.key = key;
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public boolean isRegisteredAnyValue() {
        return registered;
    }

    @Override
    public boolean isRegistered(T value) {
        if (!registered) return false;
        if (this.value == value) return true;
        if (otherValues!=null) {
            for (T otherValue : otherValues) {
                if (otherValue == value) return true;
            }
        }
        return false;
    }

    @Override
    public void registerValue(T value) {
        if (this.registered) {
            if (isRegistered(value)) return;
            if (otherValues==null) otherValues = new LinkedList<>();
            otherValues.add(value);
            return;
        }
        this.registered = true;
        this.value = value;
    }
}

class InstanceKeyValueMap6 extends DefaultInstanceCacheValue {
    public InstanceKeyValueMap6(int key, Object in) {
        super(key, in);
    }

    List<DefaultInstanceCacheValue> otherInstances = null;
    DefaultInstanceCacheValue find(int key, Object in) {
        if (this.in == in && this.key == key) return this;

        if (otherInstances == null) otherInstances = new LinkedList<>();
        // find good instances
        for (DefaultInstanceCacheValue vv : otherInstances) {
            if (vv.in == in && vv.key == key) return vv;
        }

        DefaultInstanceCacheValue ret = new DefaultInstanceCacheValue(key, in);
        otherInstances.add(ret);
        return ret;
    }
}