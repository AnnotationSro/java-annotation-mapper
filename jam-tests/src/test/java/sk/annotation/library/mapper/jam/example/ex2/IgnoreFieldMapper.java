package sk.annotation.library.mapper.jam.example.ex2;

import sk.annotation.library.mapper.jam.annotations.FieldIgnore;
import sk.annotation.library.mapper.jam.annotations.FieldMapping;
import sk.annotation.library.mapper.jam.annotations.JamMapper;
import sk.annotation.library.mapper.jam.annotations.MapperFieldConfig;

@JamMapper
@MapperFieldConfig(
    fieldMapping = {
        @FieldMapping(s="name", d="firstName"),
        @FieldMapping(s="surname", d="lastName")
    },
    fieldIgnore = {
        @FieldIgnore("id")
    }
)
public interface IgnoreFieldMapper {
    UserOutput toOutput(UserInput userInput);
}
