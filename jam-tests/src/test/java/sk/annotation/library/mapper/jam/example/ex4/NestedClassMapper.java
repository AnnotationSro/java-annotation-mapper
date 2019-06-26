package sk.annotation.library.mapper.jam.example.ex4;

import sk.annotation.library.mapper.jam.annotations.FieldMapping;
import sk.annotation.library.mapper.jam.annotations.JamMapper;
import sk.annotation.library.mapper.jam.annotations.MapperFieldConfig;

@JamMapper
public interface NestedClassMapper {
    @MapperFieldConfig(
            fieldMapping = {
                    @FieldMapping(s = "zipCode", d="zip")
            }
    )
    UserWithAddressOutput toOutput(UserWithAddressInput userWithAddressInput);
    @MapperFieldConfig(fieldMapping = {
            @FieldMapping(s = "address.street", d = "street"),
            @FieldMapping(s = "address.number", d = "number"),
            @FieldMapping(s = "address.city", d = "city"),
            @FieldMapping(s = "address.zipCode", d = "zip")
    })
    UserWithFlatAddressOutput toOutputFlatten(UserWithAddressInput userWithAddressInput);
}
