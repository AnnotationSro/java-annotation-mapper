package sk.annotation.library.jam.example.ex6;

import sk.annotation.library.jam.annotations.Mapper;
import sk.annotation.library.jam.annotations.MapperConfig;

@Mapper
@MapperConfig(withCustom = {CustomBeanMapperImpl.class})
public interface CustomBeanMapper1 {
    CustomBeanOutput toOutput(CustomBeanInput input);
}
