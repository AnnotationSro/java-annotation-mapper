package sk.annotation.library.jam.example.ex8;

import sk.annotation.library.jam.annotations.Mapper;
import sk.annotation.library.jam.annotations.Return;
import sk.annotation.library.jam.example.ex1.UserInput;
import sk.annotation.library.jam.example.ex1.UserOutput;

@Mapper
public interface UpdateDestinationBeanMapper {
    UserOutput toOutput(UserInput input, @Return UserOutput output);
}
