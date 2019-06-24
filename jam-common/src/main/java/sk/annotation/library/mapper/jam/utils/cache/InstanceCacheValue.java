package sk.annotation.library.mapper.jam.utils.cache;

public interface InstanceCacheValue<T> {
    /**
     * @return first registered value or value
     */
    public T getValue();

    /**
     * @return true, if any value is registered
     */
    public boolean isRegisteredAnyValue();


    /**
     * @return true, if value in parameter is registered
     */
    public boolean isRegistered(T value);

    /**
     * register value
     */
    public void registerValue(T value);
}
