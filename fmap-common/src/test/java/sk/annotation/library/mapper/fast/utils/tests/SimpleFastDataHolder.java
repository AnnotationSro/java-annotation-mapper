package sk.annotation.library.mapper.fast.utils.tests;

import lombok.Getter;
import lombok.Setter;

public class SimpleFastDataHolder<T> {
    public static class Entry<T> {
        protected int key;
        @Setter @Getter
        protected T value;

        protected Entry<T> before = null;
        protected Entry<T> after = null;
    }

    protected Entry<T> actualValue;

    public Entry<T> get(int key) {
        if (actualValue == null) {
            actualValue = new Entry<>();
            actualValue.key = key;
            return actualValue;
        }
        if (actualValue.key == key) return actualValue;

        Entry<T> e = actualValue;
        if (key<actualValue.key) {
            while (e.before!=null && key<e.key) e = e.before;
        }
        else {
            while (e.after!=null && key>e.key) e = e.after;
        }
        if (e.key == key) return e;

        actualValue = new Entry<>();
        actualValue.key = key;
        if (e.key < key) {
            actualValue.after = e.after;
            actualValue.before = e;
        }
        else {
            actualValue.before = e.before;
            actualValue.after = e;
        }
        if (actualValue.after!=null) actualValue.after.before = actualValue;
        if (actualValue.before!=null) actualValue.before.after = actualValue;
        return actualValue;
    }
}
