package sk.annotation.library.jam.example.ex11;

import sk.annotation.library.jam.annotations.DisableMapperFeature;
import sk.annotation.library.jam.annotations.Mapper;
import sk.annotation.library.jam.annotations.enums.MapperFeature;

@Mapper
@DisableMapperFeature({MapperFeature.PERSISTED_DATA_IN_LOCAL_THREAD, MapperFeature.PREVENT_CYCLIC_MAPPING, MapperFeature.METHOD_SUPPORTS_CONTEXT_PARAMETERS})
public interface EnumerationMapper {
	public Enum2 to2(Enum1 obj);
	public Enum1 to1(Enum2 obj);
}
