package sk.annotation.library.jam.example.ex22;

import sk.annotation.library.jam.annotations.DisableMapperFeature;
import sk.annotation.library.jam.annotations.Mapper;
import sk.annotation.library.jam.annotations.MapperConfig;
import sk.annotation.library.jam.annotations.enums.MapperFeature;

import java.math.BigDecimal;
import java.math.BigInteger;

@Mapper
@MapperConfig(withCustom = Ext2Mapper.class)
@DisableMapperFeature(MapperFeature.ALL)
abstract public class Ex22MapperWithCustom {
    protected Ext3Mapper ext3Mapper;
    public void setExt3Mapper(Ext3Mapper ext3Mapper) {
        this.ext3Mapper = ext3Mapper;
    }

    // private mapper is ignored
    private Ext4Mapper ext4Mapper;
    public void setExt4Mapper(Ext4Mapper ext4Mapper) {
        this.ext4Mapper = ext4Mapper;
    }

    abstract public String testMapper1(Long value);
    abstract public String testMapper2(Integer value);
    abstract public String testMapper3(BigDecimal value);


    abstract public String testMapper4_private(BigInteger value);

    @MapperConfig(withCustom = Ext4Mapper.class)
    abstract public String testMapper4_ConfOnMethod(BigInteger value);
}
