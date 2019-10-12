package sk.annotation.library.jam.example.ex23;

import org.junit.Test;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class MapperWithGenericsTest {

    @Test
    public void test_method_convertLong() {
        MapperWithGenerics mapper = new MapperWithGenericsJAMImpl();
        Long in = System.currentTimeMillis();
        MyTypeObj<Long> ret = mapper.convertLong(in);
        assertNotNull(ret);
        assertEquals(in, ret.getObj());

        // Check interceptors
        assertEquals(1, mapper.cnt1);
        assertEquals(1, mapper.cnt2);
        assertEquals(1, mapper.cnt3);
        assertEquals(0, mapper.cnt4);
    }

    @Test
    public void test_method_convertString() {
        MapperWithGenerics mapper = new MapperWithGenericsJAMImpl();
        String in = UUID.randomUUID().toString();
        MyTypeObj<String> ret = mapper.convertString(in);
        assertNotNull(ret);
        assertEquals(in, ret.getObj());

        // Check interceptors
        assertEquals(1, mapper.cnt1);
        assertEquals(1, mapper.cnt2);
        assertEquals(0, mapper.cnt3);
        assertEquals(0, mapper.cnt4);
    }

    @Test
    public void test_method_convertUncompatible() {
        MapperWithGenerics mapper = new MapperWithGenericsJAMImpl();
        Long in = System.currentTimeMillis();
        MyTypeObj<String> ret = mapper.convertUncompatible(in);
        assertNotNull(ret);
        assertNull(ret.getObj());

        // Check interceptors
        assertEquals(2, mapper.cnt1);
        assertEquals(2, mapper.cnt2);
        assertEquals(2, mapper.cnt3);
        assertEquals(0, mapper.cnt4);
    }

    @Test
    public void test_method_convert() {
        MapperWithGenerics mapper = new MapperWithGenericsJAMImpl();

        ObjIn in = new ObjIn();
        in.setValueLong(System.currentTimeMillis());
        in.setValueLongInList(Arrays.asList(new SecureRandom().nextLong(), System.currentTimeMillis()));
        in.setValueString(UUID.randomUUID().toString());


        ObjOut ret = mapper.convert(in);
        assertNotNull(ret);
        assertNotNull(ret.getValueLong());
        assertEquals(in.getValueLong(), ret.getValueLong().getObj());
        assertNotNull(ret.getValueString());
        assertEquals(in.getValueString(), ret.getValueString().getObj());
        assertNotNull(ret.getValueLongInList());
        assertEquals(in.getValueLongInList().size(), ret.getValueLongInList().size());
        for (int i=0; i<2; i++) {
            assertNotNull(ret.getValueLongInList().get(i));
            assertEquals(ret.getValueLongInList().get(i).getObj(), in.getValueLongInList().get(i));
        }

        // Check interceptors
        assertEquals(3, mapper.cnt1);
        assertEquals(3, mapper.cnt2);
        assertEquals(0, mapper.cnt3); // interceptor for implemented methods is not resolved
        assertEquals(1, mapper.cnt4);
    }
}
