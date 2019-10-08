package sk.annotation.library.jam.example.ex20;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import sk.annotation.library.jam.example.ex19.RefType1a;

import static org.junit.jupiter.api.Assertions.*;

public class UseOtherMapperTest {

    @Test
    public void testMapper1() {
        UseOtherMapperJAMImpl m = new UseOtherMapperJAMImpl();
        m.setOhterMapper1(new OhterMapper1());
        m.setOhterMapper2(new OhterMapper2());
        m.setOhterMapper3(new OhterMapper3());
        m.setOhterMapper4(new OhterMapper4());
        assertEquals("1:OhterMapper1", m.intToStr(1));
        assertEquals("2:OhterMapper2", m.longToStr(2L));
        assertEquals("4", m.byteToString((byte)4));

        RefType1<Integer> i1 = new RefType1<>(1, "AAA");
        RefType1<Integer> o1 = m.c1(i1);
        assertNotNull(o1);
        assertEquals(i1.getNum(), o1.getNum());
        assertEquals("OhterMapper1", o1.getName());

        RefType1<Long> i2 = new RefType1<>(2L, "BBB");
        RefType1<Long> o2 = m.c2(i2);
        assertNotNull(o2);
        assertEquals(i2.getNum(), o2.getNum());
        assertEquals("OhterMapper2", o2.getName());

        RefType1<Byte> i3 = new RefType1<>((byte) 3, "CCC");
        RefType1<Byte> o3 = m.c3(i3);
        assertNotNull(o3);
        assertEquals(i3.getNum(), o3.getNum());
        assertEquals(null, o3.getName());
    }
}
