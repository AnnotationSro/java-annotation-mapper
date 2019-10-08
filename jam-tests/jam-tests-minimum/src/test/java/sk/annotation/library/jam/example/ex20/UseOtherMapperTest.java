package sk.annotation.library.jam.example.ex20;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UseOtherMapperTest {

    @Test
    public void testMapper1() {
        UseOtherMapperJAMImpl m = new UseOtherMapperJAMImpl();
        m.setOhterMapper1(new OhterMapper1());
        m.setOhterMapper2(new OhterMapper2());
        m.setOhterMapper3(new OhterMapper3());
        m.setOhterMapper4(new OhterMapper4());
        Assertions.assertEquals("1:OhterMapper1", m.intToStr(1));
        Assertions.assertEquals("2:OhterMapper2", m.longToStr(2L));
        Assertions.assertEquals("4", m.byteToString((byte)4));
    }
}
