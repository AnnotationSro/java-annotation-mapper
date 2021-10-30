package sk.annotation.library.jam.processor.utils.commons;

abstract public class ObjectUtils {
    private ObjectUtils() {}

    public static <T> T firstNonNull(T... values) {
        if (values == null) return null;
        for (T value : values) {
            if (value != null) return value;
        }
        return null;
    }
}
