package sk.annotation.library.mapper.jam.example.ex6;

import org.springframework.beans.factory.annotation.Autowired;
import sk.annotation.library.mapper.jam.annotations.JamMapper;

@JamMapper()
public abstract class CustomBeanMapper2 {
    @Autowired

    protected CustomBeanMapperImpl otherMapper;

    abstract CustomBeanOutput toOutput(CustomBeanInput input);
}
