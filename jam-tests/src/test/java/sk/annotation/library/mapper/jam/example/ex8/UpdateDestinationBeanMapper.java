package sk.annotation.library.mapper.jam.example.ex8;

import sk.annotation.library.mapper.jam.annotations.JamMapper;
import sk.annotation.library.mapper.jam.annotations.Return;
import sk.annotation.library.mapper.jam.example.ex1.UserInput;
import sk.annotation.library.mapper.jam.example.ex1.UserOutput;

@JamMapper
public interface UpdateDestinationBeanMapper {
    UserOutput toOutput(UserInput input, @Return UserOutput output);
}
