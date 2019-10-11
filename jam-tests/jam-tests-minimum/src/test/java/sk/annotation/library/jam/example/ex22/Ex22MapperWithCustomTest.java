package sk.annotation.library.jam.example.ex22;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Ex22MapperWithCustomTest {

    final private Ext1Mapper m1;
    final private Ext2Mapper m2;
    final private Ext3Mapper m3;
    final private Ext4Mapper m4;

    public Ex22MapperWithCustomTest() {
        m1 = new Ext1Mapper();
        m2 = new Ext2Mapper();
        m3 = new Ext3Mapper();
        m4 = new Ext4Mapper();

        Ex22MapperWithCustomJAMImpl m = new Ex22MapperWithCustomJAMImpl();
        m.setExt1Mapper(m1);
        m.setExt2Mapper(m2);
        m.setExt3Mapper(m3);
        m.setExt4Mapper(null);
        m.setExt4Mapper2(m4);
        this.m=m;
    }

    final private Ex22MapperWithCustom m;

    @Test
    public void testCustomMappers() {
        assertEquals(m1.toStr(123L), m.testMapper1(123L));
        assertEquals(m2.toStr(124), m.testMapper2(124));
        assertEquals(m3.toStr(BigDecimal.valueOf(125L)), m.testMapper3(BigDecimal.valueOf(125L)));
        assertEquals("1", m.testMapper4_private(BigInteger.ONE));
        assertEquals(m4.toStr(BigInteger.TEN), m.testMapper4_ConfOnMethod(BigInteger.TEN));
    }
}
