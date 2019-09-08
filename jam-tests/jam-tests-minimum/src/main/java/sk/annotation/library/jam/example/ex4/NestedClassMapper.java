package sk.annotation.library.jam.example.ex4;

import sk.annotation.library.jam.annotations.FieldMapping;
import sk.annotation.library.jam.annotations.Mapper;
import sk.annotation.library.jam.annotations.MapperConfig;

@Mapper
public interface NestedClassMapper {
    @MapperConfig(
            fieldMapping = {
                    @FieldMapping(s = "zipCode", d="zip")
            }
    )
    UserWithAddressOutput toOutput(UserWithAddressInput userWithAddressInput);
    @MapperConfig(fieldMapping = {
            @FieldMapping(s = "address.street", d = "street"),
            @FieldMapping(s = "address.number", d = "number"),
            @FieldMapping(s = "address.city", d = "city"),
            @FieldMapping(s = "address.zipCode", d = "zip")
    })
    UserWithFlatAddressOutput toOutputFlatten(UserWithAddressInput userWithAddressInput);
}
