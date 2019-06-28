package sk.annotation.library.jam.example.ex2;

import sk.annotation.library.jam.annotations.FieldMapping;
import sk.annotation.library.jam.annotations.JamMapper;
import sk.annotation.library.jam.annotations.MapperFieldConfig;

@JamMapper
@MapperFieldConfig(fieldMapping = {
        @FieldMapping(s="name", d="firstName"),
        @FieldMapping(s="surname", d="lastName")
})
public interface CustomFieldMapper {
    UserOutput toOutput(UserInput userInput);
}
