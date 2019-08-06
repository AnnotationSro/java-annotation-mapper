package sk.annotation.library.jam.example.ex14;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import sk.annotation.library.jam.utils.MapperUtil;

import java.math.BigDecimal;
import java.math.BigInteger;

public class SimpleTypesMapperTest {

    SimpleTypesMapper mapper = MapperUtil.getMapper(SimpleTypesMapper.class);

    @Test
    public void testSimple() {
        Assertions.assertEquals(false, mapper.t1(Boolean.FALSE));
        Assertions.assertEquals(false, mapper.t1(null));
        Assertions.assertEquals(true, mapper.t1(Boolean.TRUE));

        Assertions.assertEquals(Boolean.FALSE, mapper.t9(false));
        Assertions.assertEquals(Boolean.TRUE, mapper.t9(true));

        Byte byteWrapper = 21;
        byte byteSimple = 21;
        Assertions.assertEquals(byteSimple, mapper.t2(byteWrapper));
        Assertions.assertEquals(0, mapper.t2(null));
        Assertions.assertNotEquals(22, mapper.t2(byteWrapper));

        Assertions.assertEquals(byteWrapper, mapper.t10(byteSimple));
        Assertions.assertNotEquals(new Byte((byte)22), mapper.t10(byteSimple));

        Short shortWrapper = 1234;
        short shortSimple = 1234;
        Assertions.assertEquals(shortSimple, mapper.t3(shortWrapper));
        Assertions.assertEquals(0, mapper.t3(null));
        Assertions.assertNotEquals(123, mapper.t3(shortWrapper));

        Assertions.assertEquals(shortWrapper, mapper.t11(shortSimple));
        Assertions.assertNotEquals(new Short((short)123), mapper.t11(shortSimple));

        Integer intWrapper = 123456;
        int intSimple = 123456;
        Assertions.assertEquals(intSimple, mapper.t4(intWrapper));
        Assertions.assertEquals(0, mapper.t4(null));
        Assertions.assertNotEquals(456, mapper.t4(intWrapper));

        Assertions.assertEquals(intWrapper, mapper.t12(intSimple));
        Assertions.assertNotEquals(new Integer(456), mapper.t12(intSimple));

        Long longWrapper = 12345678909L;
        long longSimple = 12345678909L;
        Assertions.assertEquals(longSimple, mapper.t5(longWrapper));
        Assertions.assertEquals(0, mapper.t5(null));
        Assertions.assertNotEquals(12345678L, mapper.t5(longWrapper));

        Assertions.assertEquals(longWrapper, mapper.t13(longSimple));
        Assertions.assertNotEquals(new Long(12345678L), mapper.t13(longSimple));

        Float floatWrapper = 1234.567f;
        float floatSimple = 1234.567f;
        Assertions.assertEquals(floatSimple, mapper.t6(floatWrapper));
        Assertions.assertEquals(0, mapper.t6(null));
        Assertions.assertNotEquals(1234.56f, mapper.t6(floatWrapper));
        Assertions.assertNotEquals(1234.5678f, mapper.t6(floatWrapper));

        Assertions.assertEquals(floatWrapper, mapper.t14(floatSimple));
        Assertions.assertNotEquals(new Float(1234.56f), mapper.t14(floatSimple));
        Assertions.assertNotEquals(new Float(1234.5678f), mapper.t14(floatSimple));

        Double doubleWrapper = 1234.567890123;
        double doubleSimple = 1234.567890123;
        Assertions.assertEquals(doubleSimple, mapper.t7(doubleWrapper));
        Assertions.assertEquals(0, mapper.t7(null));
        Assertions.assertNotEquals(1234.5678901234, mapper.t7(doubleWrapper));
        Assertions.assertNotEquals(1234.56789012, mapper.t7(doubleWrapper));

        Assertions.assertEquals(doubleWrapper, mapper.t15(doubleSimple));
        Assertions.assertNotEquals(new Double(1234.5678901234), mapper.t15(doubleSimple));
        Assertions.assertNotEquals(new Double(1234.56789012), mapper.t15(doubleSimple));

        Character charWrapper = 'A';
        char charSimple = 'A';
        Assertions.assertEquals(charSimple, mapper.t8(charWrapper));
        Assertions.assertEquals(Character.MIN_VALUE, mapper.t8(null));
        Assertions.assertNotEquals('a', mapper.t8(charWrapper));

        Assertions.assertEquals(charWrapper, mapper.t16(charSimple));
        Assertions.assertNotEquals('a', mapper.t16(charSimple));
    }

    @Test
    public void testCrossTypeByte() {
        Assertions.assertEquals((short)42, mapper.t17((byte)42));
        Assertions.assertEquals(new Short((short)42), mapper.t18((byte)42));
        Assertions.assertEquals(42, mapper.t19((byte)42));
        Assertions.assertEquals(new Integer(42), mapper.t20((byte)42));
        Assertions.assertEquals(42L, mapper.t21((byte)42));
        Assertions.assertEquals(new Long(42L), mapper.t22((byte)42));
        Assertions.assertEquals(42.0f, mapper.t23((byte)42));
        Assertions.assertEquals(new Float(42.0f), mapper.t24((byte)42));
        Assertions.assertEquals(42.0d, mapper.t25((byte)42));
        Assertions.assertEquals(new Double(42.0d), mapper.t26((byte)42));

        Assertions.assertEquals((short)42, mapper.t27(new Byte((byte)42)));
        Assertions.assertEquals(new Short((short)42), mapper.t28(new Byte((byte)42)));
        Assertions.assertEquals(42, mapper.t29(new Byte((byte)42)));
        Assertions.assertEquals(new Integer(42), mapper.t30(new Byte((byte)42)));
        Assertions.assertEquals(42L, mapper.t31(new Byte((byte)42)));
        Assertions.assertEquals(new Long(42L), mapper.t32(new Byte((byte)42)));
        Assertions.assertEquals(42.0f, mapper.t33(new Byte((byte)42)));
        Assertions.assertEquals(new Float(42.0f), mapper.t34(new Byte((byte)42)));
        Assertions.assertEquals(42.0d, mapper.t35(new Byte((byte)42)));
        Assertions.assertEquals(new Double(42.0d), mapper.t36(new Byte((byte)42)));

        Assertions.assertEquals((short)0, mapper.t27(null));
        Assertions.assertNull(mapper.t28(null));
        Assertions.assertEquals(0, mapper.t29(null));
        Assertions.assertNull(mapper.t30(null));
        Assertions.assertEquals(0L, mapper.t31(null));
        Assertions.assertNull(mapper.t32(null));
        Assertions.assertEquals(0.0f, mapper.t33(null));
        Assertions.assertNull(mapper.t34(null));
        Assertions.assertEquals(0.0d, mapper.t35(null));
        Assertions.assertNull(mapper.t36(null));
    }

    @Test
    public void testCrossTypeShort(){
        Assertions.assertEquals((byte)42, mapper.t37((short)42));
        Assertions.assertEquals(new Byte((byte)42), mapper.t38((short)42));
        Assertions.assertEquals((int)42, mapper.t39((short)42));
        Assertions.assertEquals(new Integer((int)42), mapper.t40((short)42));
        Assertions.assertEquals((long)42L, mapper.t41((short)42L));
        Assertions.assertEquals(new Long((long)42L), mapper.t42((short)42L));
        Assertions.assertEquals((float)42.0f, mapper.t43((short)42.0f));
        Assertions.assertEquals(new Float((float)42.0f), mapper.t44((short)42.0f));
        Assertions.assertEquals((double)42.0d, mapper.t45((short)42.0d));
        Assertions.assertEquals(new Double((double)42.0d), mapper.t46((short)42.0d));

        Assertions.assertEquals((byte)42, mapper.t47(new Short((short)42)));
        Assertions.assertEquals(new Byte((byte)42), mapper.t48(new Short((short)42)));
        Assertions.assertEquals((int)42, mapper.t49(new Short((short)42)));
        Assertions.assertEquals(new Integer((int)42), mapper.t50(new Short((short)42)));
        Assertions.assertEquals((long)42L, mapper.t51(new Short((short)42L)));
        Assertions.assertEquals(new Long((long)42L), mapper.t52(new Short((short)42L)));
        Assertions.assertEquals((float)42.0f, mapper.t53(new Short((short)42.0f)));
        Assertions.assertEquals(new Float((float)42.0f), mapper.t54(new Short((short)42.0f)));
        Assertions.assertEquals((double)42.0d, mapper.t55(new Short((short)42.0d)));
        Assertions.assertEquals(new Double((double)42.0d), mapper.t56(new Short((short)42.0d)));

        Assertions.assertEquals((byte)0, mapper.t47(null));
        Assertions.assertNull(mapper.t48(null));
        Assertions.assertEquals((int)0, mapper.t49(null));
        Assertions.assertNull(mapper.t50(null));
        Assertions.assertEquals((long)0L, mapper.t51(null));
        Assertions.assertNull(mapper.t52(null));
        Assertions.assertEquals((float)0.0f, mapper.t53(null));
        Assertions.assertNull(mapper.t54(null));
        Assertions.assertEquals((double)0.0d, mapper.t55(null));
        Assertions.assertNull(mapper.t56(null));
    }

    @Test
    public void testCrossTypeInt(){
        Assertions.assertEquals((byte)42, mapper.t57((int)42));
        Assertions.assertEquals(new Byte((byte)42), mapper.t58((int)42));
        Assertions.assertEquals((short)42, mapper.t59((int)42));
        Assertions.assertEquals(new Short((short)42), mapper.t60((int)42));
        Assertions.assertEquals((long)42L, mapper.t61((int)42L));
        Assertions.assertEquals(new Long((long)42L), mapper.t62((int)42L));
        Assertions.assertEquals((float)42.0f, mapper.t63((int)42.0f));
        Assertions.assertEquals(new Float((float)42.0f), mapper.t64((int)42.0f));
        Assertions.assertEquals((double)42.0d, mapper.t65((int)42.0d));
        Assertions.assertEquals(new Double((double)42.0d), mapper.t66((int)42.0d));

        Assertions.assertEquals((byte)42, mapper.t67(new Integer((int)42)));
        Assertions.assertEquals(new Byte((byte)42), mapper.t68(new Integer((int)42)));
        Assertions.assertEquals((short)42, mapper.t69(new Integer((int)42)));
        Assertions.assertEquals(new Short((short)42), mapper.t70(new Integer((int)42)));
        Assertions.assertEquals((long)42L, mapper.t71(new Integer((int)42L)));
        Assertions.assertEquals(new Long((long)42L), mapper.t72(new Integer((int)42L)));
        Assertions.assertEquals((float)42.0f, mapper.t73(new Integer((int)42.0f)));
        Assertions.assertEquals(new Float((float)42.0f), mapper.t74(new Integer((int)42.0f)));
        Assertions.assertEquals((double)42.0d, mapper.t75(new Integer((int)42.0d)));
        Assertions.assertEquals(new Double((double)42.0d), mapper.t76(new Integer((int)42.0d)));

        Assertions.assertEquals((byte)0, mapper.t67(null));
        Assertions.assertNull(mapper.t68(null));
        Assertions.assertEquals((short)0, mapper.t69(null));
        Assertions.assertNull(mapper.t70(null));
        Assertions.assertEquals((long)0L, mapper.t71(null));
        Assertions.assertNull(mapper.t72(null));
        Assertions.assertEquals((float)0.0f, mapper.t73(null));
        Assertions.assertNull(mapper.t74(null));
        Assertions.assertEquals((double)0.0d, mapper.t75(null));
        Assertions.assertNull(mapper.t76(null));
    }

    @Test
    public void testCrossTypeLong(){
        Assertions.assertEquals((byte)42, mapper.t77((long)42));
        Assertions.assertEquals(new Byte((byte)42), mapper.t78((long)42));
        Assertions.assertEquals((short)42, mapper.t79((long)42));
        Assertions.assertEquals(new Short((short)42), mapper.t80((long)42));
        Assertions.assertEquals((int)42, mapper.t81((long)42));
        Assertions.assertEquals(new Integer((int)42), mapper.t82((long)42));
        Assertions.assertEquals((float)42.0f, mapper.t83((long)42.0f));
        Assertions.assertEquals(new Float((float)42.0f), mapper.t84((long)42.0f));
        Assertions.assertEquals((double)42.0d, mapper.t85((long)42.0d));
        Assertions.assertEquals(new Double((double)42.0d), mapper.t86((long)42.0d));

        Assertions.assertEquals((byte)42, mapper.t87(new Long((long)42)));
        Assertions.assertEquals(new Byte((byte)42), mapper.t88(new Long((long)42)));
        Assertions.assertEquals((short)42, mapper.t89(new Long((long)42)));
        Assertions.assertEquals(new Short((short)42), mapper.t90(new Long((long)42)));
        Assertions.assertEquals((int)42, mapper.t91(new Long((long)42)));
        Assertions.assertEquals(new Integer((int)42), mapper.t92(new Long((long)42)));
        Assertions.assertEquals((float)42.0f, mapper.t93(new Long((long)42.0f)));
        Assertions.assertEquals(new Float((float)42.0f), mapper.t94(new Long((long)42.0f)));
        Assertions.assertEquals((double)42.0d, mapper.t95(new Long((long)42.0d)));
        Assertions.assertEquals(new Double((double)42.0d), mapper.t96(new Long((long)42.0d)));

        Assertions.assertEquals((byte)0, mapper.t87(null));
        Assertions.assertNull(mapper.t88(null));
        Assertions.assertEquals((short)0, mapper.t89(null));
        Assertions.assertNull(mapper.t90(null));
        Assertions.assertEquals((int)0, mapper.t91(null));
        Assertions.assertNull(mapper.t92(null));
        Assertions.assertEquals((float)0.0f, mapper.t93(null));
        Assertions.assertNull(mapper.t94(null));
        Assertions.assertEquals((double)0.0d, mapper.t95(null));
        Assertions.assertNull(mapper.t96(null));
    }

    @Test
    public void testCrossTypeFloat(){
        Assertions.assertEquals((byte)42, mapper.t168((float)42));
        Assertions.assertEquals(new Byte((byte)42), mapper.t169((float)42));
        Assertions.assertEquals((short)42, mapper.t170((float)42));
        Assertions.assertEquals(new Short((short)42), mapper.t171((float)42));
        Assertions.assertEquals((int)42, mapper.t172((float)42));
        Assertions.assertEquals(new Integer((int)42), mapper.t173((float)42));
        Assertions.assertEquals((long)42L, mapper.t174((float)42L));
        Assertions.assertEquals(new Long((long)42L), mapper.t175((float)42L));
        Assertions.assertEquals((double)42.0d, mapper.t176((float)42.0d));
        Assertions.assertEquals(new Double((double)42.0d), mapper.t177((float)42.0d));

        Assertions.assertEquals((byte)42, mapper.t178(new Float((float)42)));
        Assertions.assertEquals(new Byte((byte)42), mapper.t179(new Float((float)42)));
        Assertions.assertEquals((short)42, mapper.t180(new Float((float)42)));
        Assertions.assertEquals(new Short((short)42), mapper.t181(new Float((float)42)));
        Assertions.assertEquals((int)42, mapper.t182(new Float((float)42)));
        Assertions.assertEquals(new Integer((int)42), mapper.t183(new Float((float)42)));
        Assertions.assertEquals((long)42L, mapper.t184(new Float((float)42L)));
        Assertions.assertEquals(new Long((long)42L), mapper.t185(new Float((float)42L)));
        Assertions.assertEquals((double)42.0d, mapper.t186(new Float((float)42.0d)));
        Assertions.assertEquals(new Double((double)42.0d), mapper.t187(new Float((float)42.0d)));

        Assertions.assertEquals((byte)0, mapper.t178(null));
        Assertions.assertNull(mapper.t179(null));
        Assertions.assertEquals((short)0, mapper.t180(null));
        Assertions.assertNull(mapper.t181(null));
        Assertions.assertEquals((int)0, mapper.t182(null));
        Assertions.assertNull(mapper.t183(null));
        Assertions.assertEquals((long)0L, mapper.t184(null));
        Assertions.assertNull(mapper.t185(null));
        Assertions.assertEquals((double)0.0d, mapper.t186(null));
        Assertions.assertNull(mapper.t187(null));
    }

    @Test
    public void testCrossTypeDouble(){
        Assertions.assertEquals((byte)42, mapper.t188((double)42));
        Assertions.assertEquals(new Byte((byte)42), mapper.t189((double)42));
        Assertions.assertEquals((short)42, mapper.t190((double)42));
        Assertions.assertEquals(new Short((short)42), mapper.t191((double)42));
        Assertions.assertEquals((int)42, mapper.t192((double)42));
        Assertions.assertEquals(new Integer((int)42), mapper.t193((double)42));
        Assertions.assertEquals((long)42L, mapper.t194((double)42L));
        Assertions.assertEquals(new Long((long)42L), mapper.t195((double)42L));
        Assertions.assertEquals((float)42.0f, mapper.t196((double)42.0f));
        Assertions.assertEquals(new Float((float)42.0f), mapper.t197((double)42.0f));

        Assertions.assertEquals((byte)42, mapper.t198(new Double((double)42)));
        Assertions.assertEquals(new Byte((byte)42), mapper.t199(new Double((double)42)));
        Assertions.assertEquals((short)42, mapper.t200(new Double((double)42)));
        Assertions.assertEquals(new Short((short)42), mapper.t201(new Double((double)42)));
        Assertions.assertEquals((int)42, mapper.t202(new Double((double)42)));
        Assertions.assertEquals(new Integer((int)42), mapper.t203(new Double((double)42)));
        Assertions.assertEquals((long)42L, mapper.t204(new Double((double)42L)));
        Assertions.assertEquals(new Long((long)42L), mapper.t205(new Double((double)42L)));
        Assertions.assertEquals((float)42.0f, mapper.t206(new Double((double)42.0f)));
        Assertions.assertEquals(new Float((float)42.0f), mapper.t207(new Double((double)42.0f)));
        Assertions.assertEquals((float)42.12f, mapper.t206(new Double((double)42.12f)));
        Assertions.assertEquals(new Float((float)42.123f), mapper.t207(new Double((double)42.123f)));

        Assertions.assertEquals((byte)0, mapper.t198(null));
        Assertions.assertNull(mapper.t199(null));
        Assertions.assertEquals((short)0, mapper.t200(null));
        Assertions.assertNull(mapper.t201(null));
        Assertions.assertEquals((int)0, mapper.t202(null));
        Assertions.assertNull(mapper.t203(null));
        Assertions.assertEquals((long)0L, mapper.t204(null));
        Assertions.assertNull(mapper.t205(null));
        Assertions.assertEquals((float)0.0f, mapper.t206(null));
        Assertions.assertNull(mapper.t207(null));
    }

    @Test
    public void testCrossTypeString(){
        Assertions.assertEquals(true, mapper.t97("true"));
        Assertions.assertEquals(false, mapper.t97("tr"));
        Assertions.assertEquals(false, mapper.t97(null));
        Assertions.assertEquals(Boolean.FALSE, mapper.t98("false"));
        Assertions.assertEquals(Boolean.TRUE, mapper.t98("true"));
        Assertions.assertNull(mapper.t98(null));
        Assertions.assertEquals((byte)42, mapper.t99("42"));
        Assertions.assertEquals(0, mapper.t99(null));
        Assertions.assertEquals(new Byte((byte)42), mapper.t101("42"));
        Assertions.assertNull(mapper.t101(null));
        Assertions.assertEquals((short)42, mapper.t102("42"));
        Assertions.assertEquals(0, mapper.t102(null));
        Assertions.assertEquals(new Short((short)42), mapper.t103("42"));
        Assertions.assertNull(mapper.t103(null));
        Assertions.assertEquals(42, mapper.t104("42"));
        Assertions.assertEquals(0, mapper.t104(null));
        Assertions.assertEquals(new Integer(42), mapper.t105("42"));
        Assertions.assertNull(mapper.t105(null));
        Assertions.assertEquals(42.12f, mapper.t106("42.12"));
        Assertions.assertEquals(0, mapper.t106(null));
        Assertions.assertEquals(new Float(42.12f), mapper.t107("42.12"));
        Assertions.assertNull(mapper.t107(null));
        Assertions.assertEquals(432.123456d, mapper.t108("432.123456"));
        Assertions.assertEquals(0, mapper.t108(null));
        Assertions.assertEquals(new Double(432.123456d), mapper.t109("432.123456"));
        Assertions.assertNull(mapper.t109(null));
        Assertions.assertEquals('X', mapper.t110("X"));
        Assertions.assertEquals(Character.MIN_VALUE, mapper.t110(null));
        Assertions.assertEquals(new Character('X'), mapper.t111("X"));
        Assertions.assertNull(mapper.t111(null));

        Assertions.assertEquals("true", mapper.t112(true));
        Assertions.assertEquals("false", mapper.t112(false));
        Assertions.assertEquals("true", mapper.t113(Boolean.TRUE));
        Assertions.assertEquals("false", mapper.t113(Boolean.FALSE));
        Assertions.assertNull(mapper.t113(null));
        Assertions.assertEquals("42", mapper.t114((byte)42));
        Assertions.assertEquals("42", mapper.t115(new Byte((byte)42)));
        Assertions.assertNull(mapper.t115(null));
        Assertions.assertEquals("42", mapper.t116((short)42));
        Assertions.assertEquals("42", mapper.t117(new Short((short)42)));
        Assertions.assertNull( mapper.t117(null));
        Assertions.assertEquals("42", mapper.t118(42));
        Assertions.assertEquals("42", mapper.t119(new Integer(42)));
        Assertions.assertNull( mapper.t119(null));
        Assertions.assertEquals("42.12", mapper.t120(42.12f));
        Assertions.assertEquals("42.12", mapper.t121(new Float(42.12f)));
        Assertions.assertNull( mapper.t121(null));
        Assertions.assertEquals("42.1234567", mapper.t122(42.1234567d));
        Assertions.assertEquals("42.1234567", mapper.t123(new Double(42.1234567d)));
        Assertions.assertNull( mapper.t123(null));
        Assertions.assertEquals("X", mapper.t124('X'));
        Assertions.assertEquals("X", mapper.t125(new Character('X')));
        Assertions.assertNull( mapper.t125(null));
    }

    @Test
    public void testCrossTypeBigInteger(){
        Assertions.assertEquals(new BigInteger("42"), mapper.t126((byte)42));
        Assertions.assertEquals(new BigInteger("42"), mapper.t127(new Byte((byte)42)));
        Assertions.assertNull(mapper.t127(null));
        Assertions.assertEquals(new BigInteger("42"), mapper.t128((short) 42));
        Assertions.assertEquals(new BigInteger("42"), mapper.t129(new Short((short) 42)));
        Assertions.assertNull(mapper.t129(null));
        Assertions.assertEquals(new BigInteger("42"), mapper.t130(42));
        Assertions.assertEquals(new BigInteger("42"), mapper.t131(new Integer(42)));
        Assertions.assertNull(mapper.t131(null));
        Assertions.assertEquals(new BigInteger("42"), mapper.t132(42L));
        Assertions.assertEquals(new BigInteger("42"), mapper.t133(new Long(42L)));
        Assertions.assertNull(mapper.t133(null));
        Assertions.assertEquals(new BigInteger("42"), mapper.t134("42"));
        Assertions.assertNull(mapper.t134(null));

        Assertions.assertEquals((byte)42, mapper.t135(new BigInteger("42")));
        Assertions.assertEquals(0, mapper.t135(null));
        Assertions.assertEquals(new Byte((byte)42), mapper.t136(new BigInteger("42")));
        Assertions.assertNull(mapper.t136(null));
        Assertions.assertEquals((short)42, mapper.t137(new BigInteger("42")));
        Assertions.assertEquals(0, mapper.t137(null));
        Assertions.assertEquals(new Short((short)42), mapper.t138(new BigInteger("42")));
        Assertions.assertNull(mapper.t138(null));
        Assertions.assertEquals(42, mapper.t139(new BigInteger("42")));
        Assertions.assertEquals(0, mapper.t139(null));
        Assertions.assertEquals(new Integer(42), mapper.t140(new BigInteger("42")));
        Assertions.assertNull(mapper.t140(null));
        Assertions.assertEquals(42.0f, mapper.t141(new BigInteger("42")));
        Assertions.assertEquals(0f, mapper.t141(null));
        Assertions.assertEquals(new Float(42.0f), mapper.t142(new BigInteger("42")));
        Assertions.assertNull(mapper.t142(null));
        Assertions.assertEquals(42.0d, mapper.t143(new BigInteger("42")));
        Assertions.assertEquals(0d, mapper.t143(null));
        Assertions.assertEquals(new Double(42.0d), mapper.t144(new BigInteger("42")));
        Assertions.assertNull(mapper.t144(null));
        Assertions.assertEquals("42", mapper.t145(new BigInteger("42")));
        Assertions.assertNull(mapper.t145(null));
    }

    @Test
    public void testCrossTypeBigDecimal(){
        Assertions.assertEquals(new BigDecimal("42"), mapper.t146((byte)42));
        Assertions.assertEquals(new BigDecimal("42"), mapper.t147(new Byte((byte)42)));
        Assertions.assertNull(mapper.t147(null));
        Assertions.assertEquals(new BigDecimal("42"), mapper.t148((short)42));
        Assertions.assertEquals(new BigDecimal("42"), mapper.t149(new Short((short)42)));
        Assertions.assertNull(mapper.t149(null));
        Assertions.assertEquals(new BigDecimal("42"), mapper.t150(42));
        Assertions.assertEquals(new BigDecimal("42"), mapper.t151(new Integer(42)));
        Assertions.assertNull(mapper.t151(null));
        Assertions.assertEquals(new BigDecimal("42.0"), mapper.t152(42.0f));
        Assertions.assertEquals(new BigDecimal("42.0"), mapper.t153(new Float(42.0f)));
        Assertions.assertNull(mapper.t153(null));
        Assertions.assertEquals(new BigDecimal("42.0"), mapper.t154(42.0d));
        Assertions.assertEquals(new BigDecimal("42.0"), mapper.t155(new Double(42.0d)));
        Assertions.assertNull(mapper.t155(null));
        Assertions.assertEquals(new BigDecimal("42"), mapper.t156("42"));

        Assertions.assertEquals((byte)42, mapper.t157(new BigDecimal("42")));
        Assertions.assertEquals(0, mapper.t157(null));
        Assertions.assertEquals(new Byte((byte)42), mapper.t158(new BigDecimal("42")));
        Assertions.assertNull(mapper.t158(null));
        Assertions.assertEquals((short)42, mapper.t159(new BigDecimal("42")));
        Assertions.assertEquals(0, mapper.t159(null));
        Assertions.assertEquals(new Short((short)42), mapper.t160(new BigDecimal("42")));
        Assertions.assertNull(mapper.t160(null));
        Assertions.assertEquals(42, mapper.t161(new BigDecimal("42")));
        Assertions.assertEquals(0, mapper.t161(null));
        Assertions.assertEquals(new Integer(42), mapper.t162(new BigDecimal("42")));
        Assertions.assertNull(mapper.t162(null));
        Assertions.assertEquals(42.0f, mapper.t163(new BigDecimal("42")));
        Assertions.assertEquals(0f, mapper.t163(null));
        Assertions.assertEquals(new Float(42.0f), mapper.t164(new BigDecimal("42")));
        Assertions.assertNull(mapper.t164(null));
        Assertions.assertEquals(42.0d, mapper.t165(new BigDecimal("42")));
        Assertions.assertEquals(0, mapper.t165(null));
        Assertions.assertEquals(new Double(42.0d), mapper.t166(new BigDecimal("42")));
        Assertions.assertNull(mapper.t166(null));
        Assertions.assertEquals("42", mapper.t167(new BigDecimal("42")));
    }
}
