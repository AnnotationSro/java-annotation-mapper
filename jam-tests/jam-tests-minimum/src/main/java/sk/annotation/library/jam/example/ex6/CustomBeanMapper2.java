package sk.annotation.library.jam.example.ex6;

import sk.annotation.library.jam.annotations.Mapper;

@Mapper()
public abstract class CustomBeanMapper2 {
    protected CustomBeanMapperImpl otherMapper;

    abstract CustomBeanOutput toOutput(CustomBeanInput input);
}
