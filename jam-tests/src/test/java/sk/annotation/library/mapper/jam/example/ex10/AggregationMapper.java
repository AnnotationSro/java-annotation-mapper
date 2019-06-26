package sk.annotation.library.mapper.jam.example.ex10;

import sk.annotation.library.mapper.jam.annotations.FieldMapping;
import sk.annotation.library.mapper.jam.annotations.JamMapper;
import sk.annotation.library.mapper.jam.annotations.MapperFieldConfig;
import sk.annotation.library.mapper.jam.example.ex1.UserInput;
import sk.annotation.library.mapper.jam.example.ex4.AddressInput;
import sk.annotation.library.mapper.jam.example.ex4.UserWithFlatAddressOutput;
import sk.annotation.library.mapper.jam.example.ex9.UserWithRoleInput;
import sk.annotation.library.mapper.jam.example.ex9.UserWithRoleOutput;

@JamMapper
@MapperFieldConfig(fieldMapping = {
    @FieldMapping(s = "zipCode", d = "zip")
})
public interface AggregationMapper {
    UserWithFlatAddressOutput toOutput(UserInput userInput, AddressInput addressInput);
}
