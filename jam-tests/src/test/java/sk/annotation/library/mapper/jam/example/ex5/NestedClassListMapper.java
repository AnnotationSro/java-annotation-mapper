package sk.annotation.library.mapper.jam.example.ex5;

import sk.annotation.library.mapper.jam.annotations.FieldMapping;
import sk.annotation.library.mapper.jam.annotations.JamMapper;
import sk.annotation.library.mapper.jam.annotations.MapperFieldConfig;

@JamMapper
@MapperFieldConfig(
        fieldMapping = {
                @FieldMapping(s = "zipCode", d="zip")
        }
)
public interface NestedClassListMapper {
    UserWithAddressOutput toOutput(UserWithAddressInput userWithAddressInput);
}
