package sk.annotation.library.jam.example.ex2;

import sk.annotation.library.jam.annotations.FieldIgnore;
import sk.annotation.library.jam.annotations.FieldMapping;
import sk.annotation.library.jam.annotations.Mapper;
import sk.annotation.library.jam.annotations.MapperFieldConfig;

@Mapper
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
