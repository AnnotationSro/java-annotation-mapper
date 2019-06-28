package sk.annotation.library.jam.example.ex1;

import sk.annotation.library.jam.annotations.JamMapper;

@JamMapper
public interface SimpleMapper {
    UserOutput toOutput(UserInput userInput);
    UserInput toInput(UserOutput userOutput);
}
