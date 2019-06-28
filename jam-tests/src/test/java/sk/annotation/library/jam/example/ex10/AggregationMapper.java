package sk.annotation.library.jam.example.ex10;

import sk.annotation.library.jam.example.ex1.UserInput;
import sk.annotation.library.jam.annotations.FieldMapping;
import sk.annotation.library.jam.annotations.JamMapper;
import sk.annotation.library.jam.annotations.MapperFieldConfig;
import sk.annotation.library.jam.example.ex4.AddressInput;
import sk.annotation.library.jam.example.ex4.UserWithFlatAddressOutput;

@JamMapper
@MapperFieldConfig(fieldMapping = {
    @FieldMapping(s = "zipCode", d = "zip")
})
public interface AggregationMapper {
    UserWithFlatAddressOutput toOutput(UserInput userInput, AddressInput addressInput);
}
