package sk.annotation.library.jam.example.ex1;

import sk.annotation.library.jam.annotations.Mapper;

@Mapper
public interface SimpleMapper {
    UserOutput toOutput(UserInput userInput);
    UserInput toInput(UserOutput userOutput);
}
