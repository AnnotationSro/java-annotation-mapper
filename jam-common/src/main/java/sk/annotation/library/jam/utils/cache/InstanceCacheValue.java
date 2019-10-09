package sk.annotation.library.jam.utils.cache;

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
     * @param value - value for test
     * @return true, if value in parameter is registered
     */
    public boolean isRegistered(T value);

    /**
     * @param value - register this value
     */
    public void registerValue(T value);
}
