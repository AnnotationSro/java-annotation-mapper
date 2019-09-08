package sk.annotation.library.jam.example.ex15;

import sk.annotation.library.jam.annotations.FieldIgnore;
import sk.annotation.library.jam.annotations.Mapper;
import sk.annotation.library.jam.annotations.MapperConfig;
import sk.annotation.library.jam.annotations.enums.IgnoreType;
import sk.annotation.library.jam.example.ex15.subpackage.Data1;


@Mapper
@MapperConfig(
	fieldIgnore = {
		@FieldIgnore("b"),												// testIgnoreB
		@FieldIgnore(types=IData.class, value = "c"),					// testIgnoreC
		@FieldIgnore(types={Data2.class,Data3.class}, value = "d"),		// testIgnoreD
		@FieldIgnore("f"),												// testIgnoreF
		@FieldIgnore(types=IData.class, value = "g"),					// testIgnoreG
	}
)
public interface FieldIgnoresMapper {
	public Data1 clone1(Data1 d1);


	@MapperConfig(
		fieldIgnore = {
			@FieldIgnore("e"),												            // testIgnoreE
			@FieldIgnore(types = Data1.class, value="f",ignored = IgnoreType.DISABLED),	// testIgnoreF
			@FieldIgnore(types = Data3.class, value="g",ignored = IgnoreType.DISABLED)	// testIgnoreG
		}
	)
	public Data1 clone1b(Data1 d1);

	public Data2 clone2(Data2 d1);

	@MapperConfig(
		fieldIgnore = {
			@FieldIgnore("e"),												            // testIgnoreE
			@FieldIgnore(types = Data1.class, value="f",ignored = IgnoreType.DISABLED),	// testIgnoreF
			@FieldIgnore(types = Data3.class, value="g",ignored = IgnoreType.DISABLED)	// testIgnoreG
		}
	)
	public Data2 clone2b(Data2 d1);

	public Data3 clone3(Data3 d1);

	@MapperConfig(
		fieldIgnore = {
			@FieldIgnore("e"),												            // testIgnoreE
			@FieldIgnore(types = Data1.class, value="f",ignored = IgnoreType.DISABLED),	// testIgnoreF
			@FieldIgnore(types = Data3.class, value="g",ignored = IgnoreType.DISABLED)	// testIgnoreG
		}
	)
	public Data3 clone3b(Data3 d1);
}
