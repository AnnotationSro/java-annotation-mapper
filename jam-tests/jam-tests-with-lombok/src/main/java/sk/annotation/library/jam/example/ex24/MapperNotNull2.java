package sk.annotation.library.jam.example.ex24;

import sk.annotation.library.jam.annotations.DisableMapperFeature;
import sk.annotation.library.jam.annotations.Mapper;
import sk.annotation.library.jam.annotations.MapperConfig;
import sk.annotation.library.jam.annotations.Return;
import sk.annotation.library.jam.annotations.enums.ApplyFieldStrategy;
import sk.annotation.library.jam.annotations.enums.MapperFeature;

@Mapper
@DisableMapperFeature(MapperFeature.ALL)
abstract public class MapperNotNull2 {
    @MapperConfig(applyWhen = ApplyFieldStrategy.NEWVALUE_IS_NOT_NULL)
    abstract public ObjOut toObjIfNotNull(ObjIn in, @Return ObjOut out);
}
