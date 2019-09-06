package sk.annotation.library.jam.example.ex12;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import sk.annotation.library.jam.utils.MapperUtil;

import java.time.*;

public class ExampleImmputableMapperTest {

    ExampleImmputableMapper mapper = MapperUtil.getMapper(ExampleImmputableMapper.class);

    @SuppressWarnings({"UnnecessaryBoxing"})
    public Obj createNewObj() {
        Obj obj = new Obj();
        obj.setBoolean1(true);
        obj.setBoolean2(new Boolean(true));
        obj.setByte1((byte) 1);
        obj.setByte2(new Byte((byte) 2));
        obj.setShort1((short) 3);
        obj.setShort2(new Short((short) 4));
        obj.setInteger1(5);
        obj.setInteger2(new Integer(6));
        obj.setLong1(7L);
        obj.setLong2(new Long(7L));
        obj.setFloat1(1.0f);
        obj.setFloat2(new Float(1.2f));
        obj.setDouble1(2.0d);
        obj.setDouble2(new Double(2.1d));
        obj.setText("abcd");
        obj.setCh('z');
        obj.setLocalDate(LocalDate.of(2000, 11, 20));
        obj.setLocalDateTime(LocalDateTime.of(2000, 11, 20, 13, 14, 15, 16));
        obj.setLocalTime(LocalTime.of(13, 14, 15, 16));
        obj.setZonedDateTime(ZonedDateTime.of(2000, 11, 20, 13, 14, 15, 16, ZoneId.systemDefault()));
        obj.setInstant(Instant.ofEpochMilli(1L));
        obj.setO1(new Obj1());
        obj.getO1().setTest("a123");
        obj.setO2(new Obj2());
        obj.getO2().setTest("b123");
        obj.setO3(new Obj3());
        obj.getO3().setTest("c123");
        return obj;
    }

    @Test
    public void testImmutableMapping() {
        Obj in = createNewObj();
        Obj out = mapper.to2(in);

        Assertions.assertNotSame(out, in);

        Assertions.assertSame(out.isBoolean1(), in.isBoolean1());
        Assertions.assertSame(out.getBoolean2(), in.getBoolean2());
        Assertions.assertSame(out.getByte1(), in.getByte1());
        Assertions.assertSame(out.getByte2(), in.getByte2());
        Assertions.assertSame(out.getShort1(), in.getShort1());
        Assertions.assertSame(out.getShort2(), in.getShort2());
        Assertions.assertSame(out.getInteger1(), in.getInteger1());
        Assertions.assertSame(out.getInteger2(), in.getInteger2());
        Assertions.assertSame(out.getLong1(), in.getLong1());
        Assertions.assertSame(out.getLong2(), in.getLong2());
        Assertions.assertEquals(out.getFloat1(), in.getFloat1(), 0.01f);
        Assertions.assertSame(out.getFloat2(), in.getFloat2());
        Assertions.assertEquals(out.getDouble1(), in.getDouble1(), 0.01f);
        Assertions.assertSame(out.getDouble2(), in.getDouble2());
        Assertions.assertSame(out.getText(), in.getText());
        Assertions.assertSame(out.getCh(), in.getCh());
        Assertions.assertSame(out.getLocalDate(), in.getLocalDate());
        Assertions.assertSame(out.getLocalDateTime(), in.getLocalDateTime());
        Assertions.assertSame(out.getLocalTime(), in.getLocalTime());
        Assertions.assertSame(out.getZonedDateTime(), in.getZonedDateTime());
        Assertions.assertSame(out.getInstant(), in.getInstant());
        Assertions.assertSame(out.getO1(), in.getO1());     // Manual setup @MapperConfig(immutable = {Obj1.class}) on mapper
        Assertions.assertSame(out.getO2(), in.getO2());     // Manual setup @MapperConfig(immutable = {Obj2.class}) on package
        Assertions.assertNotSame(out.getO3(), in.getO3());
    }
}
