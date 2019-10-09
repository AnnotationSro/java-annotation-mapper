package sk.annotation.library.jam.example.ex2;

import sk.annotation.library.jam.annotations.FieldIgnore;
import sk.annotation.library.jam.annotations.FieldMapping;
import sk.annotation.library.jam.annotations.Mapper;
import sk.annotation.library.jam.annotations.MapperConfig;

@Mapper
@MapperConfig(
    fieldMapping = {
        @FieldMapping(s="name", d="firstName"),
        @FieldMapping(s="surname", d="lastName")
    }
)
public interface IgnoreFieldMapper {
    @MapperConfig(
            fieldIgnore = {
				@FieldIgnore(value={"id","id2"}, types={Object.class}),
				@FieldIgnore({"id","id2"})
            }
    )
    UserOutput toOutput(UserInput userInput);

    UserOutput toOutputWithId(UserInput userInput);

    UserDetailOutput toOutputDetail(UserDetailInput detailInput);
}
