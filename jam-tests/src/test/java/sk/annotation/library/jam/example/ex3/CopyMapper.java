package sk.annotation.library.jam.example.ex3;

import sk.annotation.library.jam.annotations.JamMapper;

@JamMapper
public interface CopyMapper {
    BeanToCopy toOutput(BeanToCopy beanInput);
}
