package sk.annotation.library.jam.example.ex5;

import sk.annotation.library.jam.annotations.FieldMapping;
import sk.annotation.library.jam.annotations.JamMapper;
import sk.annotation.library.jam.annotations.MapperFieldConfig;

@JamMapper
@MapperFieldConfig(
        fieldMapping = {
                @FieldMapping(s = "zipCode", d="zip")
        }
)
public interface NestedClassListMapper {
    UserWithAddressOutput toOutput(UserWithAddressInput userWithAddressInput);
}