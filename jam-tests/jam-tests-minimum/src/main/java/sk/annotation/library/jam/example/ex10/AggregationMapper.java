package sk.annotation.library.jam.example.ex10;

import sk.annotation.library.jam.annotations.FieldMapping;
import sk.annotation.library.jam.annotations.Mapper;
import sk.annotation.library.jam.annotations.MapperConfig;
import sk.annotation.library.jam.example.ex1.UserInput;
import sk.annotation.library.jam.example.ex4.AddressInput;
import sk.annotation.library.jam.example.ex4.UserWithFlatAddressOutput;

@Mapper
@MapperConfig(fieldMapping = {
    @FieldMapping(s = "zipCode", d = "zip")
})
public interface AggregationMapper {
    UserWithFlatAddressOutput toOutput(UserInput userInput, AddressInput addressInput);
}
