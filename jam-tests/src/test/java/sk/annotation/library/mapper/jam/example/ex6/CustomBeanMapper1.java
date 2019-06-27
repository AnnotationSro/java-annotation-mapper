package sk.annotation.library.mapper.jam.example.ex6;

import sk.annotation.library.mapper.jam.annotations.JamMapper;

@JamMapper(withCustom = {CustomBeanMapperImpl.class})
public interface CustomBeanMapper1 {
    CustomBeanOutput toOutput(CustomBeanInput input);
}
