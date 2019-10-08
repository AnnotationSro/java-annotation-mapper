package sk.annotation.library.jam.example.ex20;

import sk.annotation.library.jam.annotations.Mapper;
import sk.annotation.library.jam.annotations.MapperConfig;
import sk.annotation.library.jam.example.ex19.RefType1a;

@Mapper(withCustom = {OhterMapper1.class, OhterMapper4.class /*Method in mapper is annotation IgnoredByMapper*/})
@MapperConfig
public abstract class UseOtherMapper{
    protected OhterMapper2 ohterMapper2;
    public void setOhterMapper2(OhterMapper2 ohterMapper2) {
        this.ohterMapper2 = ohterMapper2;
    }

    // private mapper is ignored
    private OhterMapper3 ohterMapper3;
    public void setOhterMapper3(OhterMapper3 ohterMapper3) {
        this.ohterMapper3 = ohterMapper3;
    }

    abstract public String intToStr(Integer i0);
    abstract public RefType1<Integer> c1(RefType1<Integer> i0);

    abstract public String longToStr(Long i0);
    abstract public RefType1<Long> c2(RefType1<Long> i0);

    abstract public String byteToString(byte i0);
    abstract public RefType1<Byte> c3(RefType1<Byte> i0);
}
