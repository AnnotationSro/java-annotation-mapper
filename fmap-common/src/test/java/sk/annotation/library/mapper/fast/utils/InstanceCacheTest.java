package sk.annotation.library.mapper.fast.utils;

import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

@SuppressWarnings({"UnnecessaryBoxing", "ConstantConditions"})
public class InstanceCacheTest {
    static private class InstanceCacheExtForTest extends InstanceCache {
//        public IdentityHashMap<Object, HashMap<String, Optional<Object>>> getObjCache() {
//            return this.objCache;
//        }
    }

    private static void assertResult(Object expectedValue, InstanceCacheExtForTest cache, Object key1, String expectedType) {
        Optional<Object> o = cache.get(expectedType, key1);
        Object test = o==null? null : o.orElse(null);
        Assert.assertEquals(expectedValue, test);
    }

    @Test
    public void testSimpleInstance() {
        Integer key1 = new Integer(1);
        Integer key2 = new Integer(1);
        Integer key3 = new Integer(2);

        Object valNull = null;
        Integer valInteger1 = 1;
        String valString = "1";

        InstanceCacheExtForTest cache = new InstanceCacheExtForTest();
        assertResult(valNull, cache, key1, null);
        assertResult(valNull, cache, key1, "m1");
        assertResult(valNull, cache, key1, "m2");
        assertResult(valNull, cache, key2, null);
        assertResult(valNull, cache, key2, "m1");
        assertResult(valNull, cache, key2, "m2");
        assertResult(valNull, cache, key3, null);
        assertResult(valNull, cache, key3, "m1");
        assertResult(valNull, cache, key3, "m2");

        cache.put(key1, valInteger1);
        assertResult(valInteger1, cache, key1, null);
        assertResult(valNull, cache, key1, "m1");
        assertResult(valNull, cache, key1, "m2");
        assertResult(valNull, cache, key2, null);
        assertResult(valNull, cache, key2, "m1");
        assertResult(valNull, cache, key2, "m2");
        assertResult(valNull, cache, key3, null);
        assertResult(valNull, cache, key3, "m1");
        assertResult(valNull, cache, key3, "m2");

        cache.put("m1", key2, valString);
        assertResult(valInteger1, cache, key1, null);
        assertResult(valNull, cache, key1, "m1");
        assertResult(valNull, cache, key1, "m2");
        assertResult(valNull, cache, key2, null);
        assertResult(valString, cache, key2, "m1");
        assertResult(valNull, cache, key2, "m2");
        assertResult(valNull, cache, key3, null);
        assertResult(valNull, cache, key3, "m1");
        assertResult(valNull, cache, key3, "m2");


        cache.put("m1", key1, valInteger1);
        assertResult(valInteger1, cache, key1, null);
        assertResult(valInteger1, cache, key1, "m1");
        assertResult(valNull, cache, key1, "m2");
        assertResult(valNull, cache, key2, null);
        assertResult(valString, cache, key2, "m1");
        assertResult(valNull, cache, key2, "m2");
        assertResult(valNull, cache, key3, null);
        assertResult(valNull, cache, key3, "m1");
        assertResult(valNull, cache, key3, "m2");
    }
}
