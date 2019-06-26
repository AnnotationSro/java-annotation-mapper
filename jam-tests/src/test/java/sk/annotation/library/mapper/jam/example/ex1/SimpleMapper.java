package sk.annotation.library.mapper.jam.example.ex1;

import sk.annotation.library.mapper.jam.annotations.JamMapper;

@JamMapper
public interface SimpleMapper {
    UserOutput toOutput(UserInput userInput);
    UserInput toInput(UserOutput userOutput);
}
