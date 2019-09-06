package sk.annotation.library.jam.example.ex4;

import sk.annotation.library.jam.annotations.FieldMapping;
import sk.annotation.library.jam.annotations.Mapper;
import sk.annotation.library.jam.annotations.MapperFieldConfig;

@Mapper
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
