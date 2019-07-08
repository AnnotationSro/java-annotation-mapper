package sk.annotation.library.jam.example.ex5;

import sk.annotation.library.jam.annotations.FieldMapping;
import sk.annotation.library.jam.annotations.Mapper;
import sk.annotation.library.jam.annotations.MapperFieldConfig;

@Mapper
@MapperFieldConfig(
        fieldMapping = {
                @FieldMapping(s = "zipCode", d="zip")
        }
)
public interface NestedClassListMapper {
    UserWithAddressOutput toOutput(UserWithAddressInput userWithAddressInput);
}
