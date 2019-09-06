package sk.annotation.library.jam.example.ex6;

import org.springframework.beans.factory.annotation.Autowired;
import sk.annotation.library.jam.annotations.Mapper;

@Mapper()
public abstract class CustomBeanMapper2 {
    @Autowired

    protected CustomBeanMapperImpl otherMapper;

    abstract CustomBeanOutput toOutput(CustomBeanInput input);
}
