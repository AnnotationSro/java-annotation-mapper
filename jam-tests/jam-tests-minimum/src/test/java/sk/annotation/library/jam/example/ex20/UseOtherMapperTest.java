package sk.annotation.library.jam.example.ex20;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UseOtherMapperTest {
    final private OhterMapper1 m1;
    final private OhterMapper2 m2;
    final private OhterMapper3 m3;
    final private OhterMapper4 m4;

    final private UseOtherMapper m;

    public UseOtherMapperTest() {
        m1 = new OhterMapper1();
        m2 = new OhterMapper2();
        m3 = new OhterMapper3();
        m4 = new OhterMapper4();

        UseOtherMapperJAMImpl m = new UseOtherMapperJAMImpl();
        m.setOhterMapper1(m1);
        m.setOtherMapper2(m2);
        m.setOtherMapper3(m3);
        //Compilation error: unused mapper are not implemented        m.setOhterMapper4(m4);
        this.m=m;
    }

    @Test
    public void testMapper1() {
        assertEquals(m1.convInterToString(1), m.intToStr(1));
        assertEquals(m2.convLongToString(2L), m.longToStr(2L));
        assertEquals(""+(byte)4, m.byteToString((byte)4));    // default transfrom, because m3 is private => invisible for mapper

        RefType1<Integer> i1 = new RefType1<>(1, "AAA");
        RefType1<Integer> o1 = m.c1(i1);
        assertNotNull(o1);
        assertEquals(i1.getNum(), o1.getNum());
        assertEquals(m1.createRefType1a().getName(), o1.getName());

        RefType1<Long> i2 = new RefType1<>(2L, "BBB");
        RefType1<Long> o2 = m.c2(i2);
        assertNotNull(o2);
        assertEquals(i2.getNum(), o2.getNum());
        assertEquals(m2.createRefType1a().getName(), o2.getName());

        RefType1<Byte> i3 = new RefType1<>((byte) 3, "CCC");
        RefType1<Byte> o3 = m.c3(i3);
        assertNotNull(o3);
        assertEquals(i3.getNum(), o3.getNum());
        assertEquals(null, o3.getName());
    }
}
