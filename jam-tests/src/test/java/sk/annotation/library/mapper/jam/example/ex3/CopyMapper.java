package sk.annotation.library.mapper.jam.example.ex3;

import sk.annotation.library.mapper.jam.annotations.JamMapper;

@JamMapper
public interface CopyMapper {
    BeanToCopy toOutput(BeanToCopy beanInput);
}
