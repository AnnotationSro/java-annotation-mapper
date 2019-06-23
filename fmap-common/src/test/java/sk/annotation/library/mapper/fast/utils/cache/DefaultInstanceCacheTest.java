package sk.annotation.library.mapper.fast.utils.cache;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@SuppressWarnings({"NumberEquality", "UnnecessaryBoxing"})
public class DefaultInstanceCacheTest {

    private DynamicTest createDynamicTest_checkValueInObject(Optional expectedValue, DefaultInstanceCache cache, Object key1, String expectedType) {
        return DynamicTest.dynamicTest("ExpectedValue " + expectedValue + " for " + key1 + ".", () -> {
            InstanceCacheValue<Object> cacheValues = cache.getCacheValues(expectedType, key1);
            if (expectedValue == null) Assertions.assertFalse(cacheValues.isRegisteredAnyValue());
            else Assertions.assertEquals(expectedValue.orElse(null), cacheValues.getValue());
        });
    }

    @Test
    public void test_getCachedValues_beforeRegisteredValues() {
        DefaultInstanceCache cache = new DefaultInstanceCache();
        InstanceCacheValue val1 = cache.getCacheValues(null, null);
        Assertions.assertNotNull(val1);
        InstanceCacheValue val2 = cache.getCacheValues(null, null);
        Assertions.assertNotNull(val2);
        Assertions.assertTrue(val1 == val2);
    }

    @Test
    public void test_getCachedValues_afterRegisteredValues() {
        DefaultInstanceCache cache = new DefaultInstanceCache();
        InstanceCacheValue val1 = cache.getCacheValues(null, null);
        Assertions.assertNotNull(val1);
        val1.registerValue(null);

        InstanceCacheValue val2 = cache.getCacheValues(null, null);
        Assertions.assertNotNull(val2);
        Assertions.assertTrue(val1 == val2);
    }

    @Test
    public void test_getCachedValues_checkInstancesAfterRegisteredValues() {
        Integer i1 = 1;
        Integer i2 = new Integer(1);
        Integer i3 = new Integer(1);

        DefaultInstanceCache cache = new DefaultInstanceCache();
        cache.getCacheValues(null, i1).registerValue(null);
        cache.getCacheValues(null, i2).registerValue(null);
        cache.getCacheValues(null, i3).registerValue(null);

        // Test 1
        InstanceCacheValue valI1 = cache.getCacheValues(null, i1);
        InstanceCacheValue valI2 = cache.getCacheValues(null, i2);
        InstanceCacheValue valI3 = cache.getCacheValues(null, i3);
        Assertions.assertNotNull(valI1);
        Assertions.assertNotNull(valI2);
        Assertions.assertNotNull(valI3);
        Assertions.assertEquals(i1==i2, valI1 == valI2);
        Assertions.assertEquals(i1==i3, valI1 == valI3);
        Assertions.assertEquals(i2==i3, valI2 == valI3);

        // Test 2
        cache.getCacheValues("a", i1).registerValue(null);
        cache.getCacheValues("b", i1).registerValue(null);

        InstanceCacheValue valI1a = cache.getCacheValues("a", i1);
        InstanceCacheValue valI2a = cache.getCacheValues("a", i1);
        InstanceCacheValue valI3b = cache.getCacheValues("b", i1);
        Assertions.assertNotNull(valI1a);
        Assertions.assertNotNull(valI2a);
        Assertions.assertNotNull(valI3b);
        Assertions.assertSame(valI1a, valI2a);
        Assertions.assertNotSame(valI1a, valI3b);
    }


    @Test
    public void test_DefaultInstanceCacheValue() {
        Integer i0 = null;
        Integer i1 = 1;


        {   // first using - first registered is i1
            DefaultInstanceCache cache = new DefaultInstanceCache();
            InstanceCacheValue<Object> cacheValue = cache.getCacheValues(null, i1);
            Assertions.assertNotNull(cacheValue);
            Assertions.assertFalse(cacheValue.isRegisteredAnyValue());
            Assertions.assertFalse(cacheValue.isRegistered(i0));
            Assertions.assertFalse(cacheValue.isRegistered(i1));

            cacheValue.registerValue(i1);
            Assertions.assertTrue(cacheValue.isRegisteredAnyValue());
            Assertions.assertFalse(cacheValue.isRegistered(i0));
            Assertions.assertTrue(cacheValue.isRegistered(i1));
            Assertions.assertEquals(i1, cacheValue.getValue());

            cacheValue.registerValue(i0);
            Assertions.assertTrue(cacheValue.isRegisteredAnyValue());
            Assertions.assertTrue(cacheValue.isRegistered(i0));
            Assertions.assertTrue(cacheValue.isRegistered(i1));
            Assertions.assertEquals(i1, cacheValue.getValue());
        }



        {   // first using - first registered is i0
            DefaultInstanceCache cache = new DefaultInstanceCache();
            InstanceCacheValue<Object> cacheValue = cache.getCacheValues(null, i1);

            cacheValue.registerValue(i0);
            Assertions.assertTrue(cacheValue.isRegisteredAnyValue());
            Assertions.assertTrue(cacheValue.isRegistered(i0));
            Assertions.assertFalse(cacheValue.isRegistered(i1));
            Assertions.assertEquals(i0, cacheValue.getValue());

            cacheValue.registerValue(i1);
            Assertions.assertTrue(cacheValue.isRegisteredAnyValue());
            Assertions.assertTrue(cacheValue.isRegistered(i0));
            Assertions.assertTrue(cacheValue.isRegistered(i1));
            Assertions.assertEquals(i0, cacheValue.getValue());
        }
    }

    @TestFactory
    public List<DynamicTest> testExcpetedValues_emptyCache() {
        Integer in1 = new Integer(1);
        Integer in2 = new Integer(1);
        Integer in3 = new Integer(2);

        DefaultInstanceCache cache = new DefaultInstanceCache();
        return Arrays.asList(
                createDynamicTest_checkValueInObject(null, cache, in1, null),
                createDynamicTest_checkValueInObject(null, cache, in1, "m1"),
                createDynamicTest_checkValueInObject(null, cache, in1, "m2"),
                createDynamicTest_checkValueInObject(null, cache, in2, null),
                createDynamicTest_checkValueInObject(null, cache, in2, "m1"),
                createDynamicTest_checkValueInObject(null, cache, in2, "m2"),
                createDynamicTest_checkValueInObject(null, cache, in3, null),
                createDynamicTest_checkValueInObject(null, cache, in3, "m1"),
                createDynamicTest_checkValueInObject(null, cache, in3, "m2")
        );
    }

    @TestFactory
    public List<DynamicTest> testExcpetedValues_cacheWith1Value() {
        Integer in1 = new Integer(1);
        Integer in2 = new Integer(1);
        Integer in3 = new Integer(2);

        Object valNull = null;
        Integer valInteger1 = 1;

        DefaultInstanceCache cache = new DefaultInstanceCache();
        cache.getCacheValues(null, in1).registerValue(valInteger1);
        return Arrays.asList(
                createDynamicTest_checkValueInObject(Optional.of(valInteger1), cache, in1, null),
                createDynamicTest_checkValueInObject(null, cache, in1, "m1"),
                createDynamicTest_checkValueInObject(null, cache, in1, "m2"),
                createDynamicTest_checkValueInObject(null, cache, in2, null),
                createDynamicTest_checkValueInObject(null, cache, in2, "m1"),
                createDynamicTest_checkValueInObject(null, cache, in2, "m2"),
                createDynamicTest_checkValueInObject(null, cache, in3, null),
                createDynamicTest_checkValueInObject(null, cache, in3, "m1"),
                createDynamicTest_checkValueInObject(null, cache, in3, "m2")
        );
    }

    @TestFactory
    public List<DynamicTest> testExcpetedValues_cacheWith2Values_asNull() {
        Integer in1 = new Integer(1);
        Integer in2 = new Integer(1);
        Integer in3 = new Integer(2);

        Object valNull = null;
        Integer valInteger1 = 1;
        String valString = null;

        DefaultInstanceCache cache = new DefaultInstanceCache();
        cache.getCacheValues(null, in1).registerValue(valInteger1);
        cache.getCacheValues("m1", in2).registerValue(valString);
        return Arrays.asList(
                createDynamicTest_checkValueInObject(Optional.of(valInteger1), cache, in1, null),
                createDynamicTest_checkValueInObject(null, cache, in1, "m1"),
                createDynamicTest_checkValueInObject(null, cache, in1, "m2"),
                createDynamicTest_checkValueInObject(null, cache, in2, null),
                createDynamicTest_checkValueInObject(Optional.ofNullable(valString), cache, in2, "m1"),
                createDynamicTest_checkValueInObject(null, cache, in2, "m2"),
                createDynamicTest_checkValueInObject(null, cache, in3, null),
                createDynamicTest_checkValueInObject(null, cache, in3, "m1"),
                createDynamicTest_checkValueInObject(null, cache, in3, "m2")
        );
    }

    @TestFactory
    public List<DynamicTest> testExcpetedValues_cacheWith2Values() {
        Integer in1 = new Integer(1);
        Integer in2 = new Integer(1);
        Integer in3 = new Integer(2);

        Object valNull = null;
        Integer valInteger1 = 1;
        String valString = new String("1");

        DefaultInstanceCache cache = new DefaultInstanceCache();
        cache.getCacheValues(null, in1).registerValue(valInteger1);
        cache.getCacheValues("m1", in2).registerValue(valString);
        return Arrays.asList(
                createDynamicTest_checkValueInObject(Optional.of(valInteger1), cache, in1, null),
                createDynamicTest_checkValueInObject(null, cache, in1, "m1"),
                createDynamicTest_checkValueInObject(null, cache, in1, "m2"),
                createDynamicTest_checkValueInObject(null, cache, in2, null),
                createDynamicTest_checkValueInObject(Optional.of(valString), cache, in2, "m1"),
                createDynamicTest_checkValueInObject(null, cache, in2, "m2"),
                createDynamicTest_checkValueInObject(null, cache, in3, null),
                createDynamicTest_checkValueInObject(null, cache, in3, "m1"),
                createDynamicTest_checkValueInObject(null, cache, in3, "m2")
        );
    }

   @TestFactory
    public List<DynamicTest> testExcpetedValues_cacheWith3Values() {
        Integer in1 = new Integer(1);
        Integer in2 = new Integer(1);
        Integer in3 = new Integer(2);

        Integer valInteger1 = 1;
        Integer valInteger2 = 2;
        String valString = new String("1");

        DefaultInstanceCache cache = new DefaultInstanceCache();
        cache.getCacheValues(null, in1).registerValue(valInteger1);
        cache.getCacheValues("m1", in2).registerValue(valString);
        cache.getCacheValues("m1", in1).registerValue(valInteger2);
        return Arrays.asList(
                createDynamicTest_checkValueInObject(Optional.of(valInteger1), cache, in1, null),
                createDynamicTest_checkValueInObject(Optional.of(valInteger2), cache, in1, "m1"),
                createDynamicTest_checkValueInObject(null, cache, in1, "m2"),
                createDynamicTest_checkValueInObject(null, cache, in2, null),
                createDynamicTest_checkValueInObject(Optional.of(valString), cache, in2, "m1"),
                createDynamicTest_checkValueInObject(null, cache, in2, "m2"),
                createDynamicTest_checkValueInObject(null, cache, in3, null),
                createDynamicTest_checkValueInObject(null, cache, in3, "m1"),
                createDynamicTest_checkValueInObject(null, cache, in3, "m2")
        );
    }


}
