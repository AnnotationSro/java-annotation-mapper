package sk.annotation.library.mapper.jam.example.ex6;

import org.springframework.beans.factory.annotation.Autowired;
import sk.annotation.library.mapper.jam.annotations.JamMapper;

@JamMapper(withCustom = {CustomBeanMapperImpl.class})
public interface CustomBeanMapper {
    CustomBeanOutput toOutput(CustomBeanInput input);
}
