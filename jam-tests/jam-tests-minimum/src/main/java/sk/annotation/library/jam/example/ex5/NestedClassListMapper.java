package sk.annotation.library.jam.example.ex5;

import sk.annotation.library.jam.annotations.FieldMapping;
import sk.annotation.library.jam.annotations.Mapper;
import sk.annotation.library.jam.annotations.MapperConfig;

import java.util.List;

@Mapper
@MapperConfig(
        fieldMapping = {
                @FieldMapping(s = "zipCode", d="zip")
        }
)
public interface NestedClassListMapper {
    UserWithAddressOutput toOutput(UserWithAddressInput userWithAddressInput);
    List<? extends String> testWidlCard(List<? extends Number> userWithAddressInput);
}
