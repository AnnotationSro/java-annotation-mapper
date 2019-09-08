package sk.annotation.library.jam.example.ex15;

import sk.annotation.library.jam.annotations.*;
import sk.annotation.library.jam.annotations.enums.IgnoreType;
import sk.annotation.library.jam.annotations.enums.MapperFeature;
import sk.annotation.library.jam.example.ex15.subpackage.Data1;

@Mapper
@MapperConfig(
	fieldIgnore = {
		@FieldIgnore(packages = "", value = "b", ignored = IgnoreType.DISABLED),
		@FieldIgnore("c"),																// applied on all fields
		@FieldIgnore(packages = "sk", value = "d", ignored = IgnoreType.IGNORE_ALL),	// applied on all fields
		@FieldIgnore(value = "e", ignored = IgnoreType.IGNORE_READ),					// if it is applied on all types, without custom field
		@FieldIgnore(value = "f", ignored = IgnoreType.IGNORE_WRITE),					// applied on all fields, but if READ is not possible, WRITE cannot be generated
		@FieldIgnore(packages = "sk.annotation.library.jam.example.ex15.subpackage", value = "g", ignored = IgnoreType.IGNORE_READ),
		@FieldIgnore(packages = "sk.annotation.library.jam.example.ex15.subpackage", value = "h", ignored = IgnoreType.IGNORE_WRITE)
	}
)
@DisableMapperFeature(MapperFeature.ALL)
public interface FieldIgnoresDirectionWithPackageMapper {
	public Data2 clone1a2(Data1 in);
	public Data1 clone2a1(Data2 in);


	@MapperConfig(
		fieldMapping = {@FieldMapping(s="e",d="f")}
	)
	public Data2 clone1b2(Data1 in);

	@MapperConfig(
		fieldMapping = {@FieldMapping(s="e",d="f")}
	)
	public Data1 clone2b1(Data2 in);
}
