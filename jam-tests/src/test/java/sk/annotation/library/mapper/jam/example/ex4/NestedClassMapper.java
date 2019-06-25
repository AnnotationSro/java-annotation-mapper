package sk.annotation.library.mapper.jam.example.ex4;

import sk.annotation.library.mapper.jam.annotations.FieldMapping;
import sk.annotation.library.mapper.jam.annotations.JamMapper;
import sk.annotation.library.mapper.jam.annotations.MapperFieldConfig;

@JamMapper
@MapperFieldConfig(
        fieldMapping = {
                @FieldMapping(s = "zipCode", d="zip")
        }
)
public interface NestedClassMapper {
    UserWithAddressOutput toOutput(UserWithAddressInput userWithAddressInput);
}
