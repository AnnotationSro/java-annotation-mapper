package sk.annotation.library.jam.example.ex2;

import sk.annotation.library.jam.annotations.FieldMapping;
import sk.annotation.library.jam.annotations.Mapper;
import sk.annotation.library.jam.annotations.MapperConfig;

@Mapper
@MapperConfig(fieldMapping = {
        @FieldMapping(s="name", d="firstName"),
        @FieldMapping(s="surname", d="lastName"),
        @FieldMapping(s={"id", "id2"}, d={"id2", "id"})
})
public interface CustomFieldMapper {
    UserOutput toOutput(UserInput userInput);
}
