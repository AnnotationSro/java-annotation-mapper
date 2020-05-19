package sk.annotation.library.jam.example.ex24;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import sk.annotation.library.jam.utils.MapperUtil;

import java.util.Arrays;

public class MapperNotNullTest {
    private MapperNotNull1 mapper1 = MapperUtil.getMapper(MapperNotNull1.class);
    private MapperNotNull2 mapper2 = MapperUtil.getMapper(MapperNotNull2.class);

    ObjIn cIn() {
        ObjIn in = new ObjIn();
        in.setTest1_Long2Long(1L);
        in.setTest2_Long2long(2L);
        in.setTest3_long2Long(3L);
        in.setTest4_long2long(4L);
        in.setValueLongInList(Arrays.asList(5L, 6L));
        return in;
    }

    @Test
    public void test_m1() {
        ObjIn in = cIn();
        ObjOut out1 = mapper1.toObj(in);
        ObjOut out2 = mapper1.toObj(in);
        Assertions.assertNotNull(out1);
        Assertions.assertNotNull(out2);
        Assertions.assertEquals(out1, out2);
        Assertions.assertNotSame(out1, out2);
    }

    @Test
    public void test_m2() {
        ObjIn in = cIn();
        ObjOut out1 = mapper1.toObj(in);

        in.setTest1_Long2Long(null);
        in.setTest2_Long2long(null);

        Long l1 = 20L;
        long l2 = 30L;
        out1.setTest1_Long2Long(l1);
        out1.setTest2_Long2long(l2);

        out1 = mapper2.toObjIfNotNull(in, out1);
        Assertions.assertNotNull(out1);

        Assertions.assertEquals(l1, out1.getTest1_Long2Long());
        Assertions.assertEquals(l2, out1.getTest2_Long2long());
        Assertions.assertEquals(in.getTest3_long2Long(), out1.getTest3_long2Long());
        Assertions.assertEquals(in.getTest4_long2long(), out1.getTest4_long2long());
        Assertions.assertEquals(in.getValueLongInList(), out1.getValueLongInList());
    }

    //TODO: Missing test for method3
}
