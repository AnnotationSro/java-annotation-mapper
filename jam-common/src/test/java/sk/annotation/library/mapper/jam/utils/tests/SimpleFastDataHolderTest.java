package sk.annotation.library.mapper.jam.utils.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SimpleFastDataHolderTest {

    @Test
    public void testEmpty() {
        SimpleFastDataHolder a = new SimpleFastDataHolder();
        Assertions.assertNull(a.actualValue);
    }

    @Test
    public void testFirstValue() {
        SimpleFastDataHolder a = new SimpleFastDataHolder();
        Assertions.assertNull(a.actualValue);

        // first call
        SimpleFastDataHolder.Entry e1 = a.get(1);
        Assertions.assertNotNull(e1);
        Assertions.assertEquals(1, e1.key);
        Assertions.assertNotNull(a.actualValue);
        Assertions.assertNull(a.actualValue.after);
        Assertions.assertNull(a.actualValue.before);

        SimpleFastDataHolder.Entry e2 = a.get(1);
        Assertions.assertNotNull(e2);
        Assertions.assertEquals(1, e2.key);
        Assertions.assertNotNull(a.actualValue);
        Assertions.assertNull(a.actualValue.after);
        Assertions.assertNull(a.actualValue.before);
    }


    protected void assertOrderedKeys(SimpleFastDataHolder a, int... values) {
        Assertions.assertNotNull(a.actualValue);

        SimpleFastDataHolder.Entry v = a.actualValue;
        while (v.before!=null) v = v.before;

        for (int val: values) {
            Assertions.assertEquals(val, v.key);
            v = v.after;
        }

        Assertions.assertNull(v);
    }

    @Test
    public void testOrderingValues() {
        SimpleFastDataHolder a = new SimpleFastDataHolder();
        a.get(20);
        a.get(30);
        a.get(10);
        a.get(40);

        assertOrderedKeys(a, 10,20,30,40);

        a.get(25);
        assertOrderedKeys(a, 10,20,25,30,40);

        a.get(20);
        assertOrderedKeys(a, 10,20,25,30,40);

        a.get(30);
        assertOrderedKeys(a, 10,20,25,30,40);

        a.get(35);
        assertOrderedKeys(a, 10,20,25,30,35,40);

        a.get(36);
        assertOrderedKeys(a, 10,20,25,30,35,36,40);

        a.get(30);
        assertOrderedKeys(a, 10,20,25,30,35,36,40);

        a.get(31);
        assertOrderedKeys(a, 10,20,25,30,31,35,36,40);

        a.get(32);
        assertOrderedKeys(a, 10,20,25,30,31,32,35,36,40);

        a.get(18);
        assertOrderedKeys(a, 10,18,20,25,30,31,32,35,36,40);

        a.get(39);
        assertOrderedKeys(a, 10,18,20,25,30,31,32,35,36,39,40);
    }
}
