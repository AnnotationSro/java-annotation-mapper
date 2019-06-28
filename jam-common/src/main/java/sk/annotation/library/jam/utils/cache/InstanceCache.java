package sk.annotation.library.jam.utils.cache;

public interface InstanceCache {
    default public <T> InstanceCacheValue<T> getCacheValues(String key, Object in) {
        return getCacheValues(key == null ? 0 : key.hashCode(), in);
    }
    public <T> InstanceCacheValue<T> getCacheValues(int hashKey, Object in);
}
