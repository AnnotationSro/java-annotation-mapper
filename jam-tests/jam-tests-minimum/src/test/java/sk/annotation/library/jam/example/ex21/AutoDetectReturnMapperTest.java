package sk.annotation.library.jam.example.ex21;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import sk.annotation.library.jam.utils.MapperUtil;

import java.util.UUID;
import java.util.function.Consumer;

public class AutoDetectReturnMapperTest {

    AutoDetectReturnMapper mapper = MapperUtil.getMapper(AutoDetectReturnMapper.class);

    private static ObjMerge createObjMerge(String value1, String value2) {
        ObjMerge o = new ObjMerge();
        o.setValue1(value1);
        o.setValue2(value2);
        return o;
    }

    private static void createObjMerge(String value1, String value2, Consumer<ObjMerge> run) {
        run.accept(createObjMerge(value1, value2));
    }

    @Test
    public void testMapper1() {
        ObjIn1 i1 = new ObjIn1();
        i1.setValue1("1");

        ObjIn2 i2 = new ObjIn2();
        i2.setValue2("2");

        Assertions.assertEquals(createObjMerge(i1.getValue1(), null), mapper.updateLast0(i1));
        Assertions.assertEquals(createObjMerge(i1.getValue1(), null), mapper.updateLast1(i1, null));

        createObjMerge(i1.getValue1(), UUID.randomUUID().toString(), (r) -> {
            // expected merge mode - autmoatic
            Assertions.assertSame(r, mapper.updateLast1(i1, r));
        });
        createObjMerge(i1.getValue1(), UUID.randomUUID().toString(), (r) -> {
            // expected merge mode - force enabled
            Assertions.assertSame(r, mapper.updateLast2(i1, r));
        });
        createObjMerge(i1.getValue1(), UUID.randomUUID().toString(), (r) -> {
            // expected merge mode - force disabled
            Assertions.assertNotSame(r, mapper.updateLast3(i1, r));
            Assertions.assertEquals(r, mapper.updateLast3(i1, r));
        });


        createObjMerge(i1.getValue1(), i2.getValue2(), (r) -> {
            // expected merge mode - autmoatic
            Assertions.assertSame(r, mapper.updateLast4(i1, i2, r));
        });
        createObjMerge(i1.getValue1(), i2.getValue2(), (r) -> {
            // expected merge mode - force enabled
            Assertions.assertSame(r, mapper.updateLast5(i1, i2, r));
        });
        createObjMerge(i1.getValue1(), i2.getValue2(), (r) -> {
            // expected merge mode - force disabled
            Assertions.assertNotSame(r, mapper.updateLast6(i1, i2, r));
            Assertions.assertEquals(r, mapper.updateLast6(i1, i2, r));
        });
    }
}
