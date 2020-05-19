package sk.annotation.library.jam.example.ex24;

import sk.annotation.library.jam.annotations.DisableMapperFeature;
import sk.annotation.library.jam.annotations.Mapper;
import sk.annotation.library.jam.annotations.MapperConfig;
import sk.annotation.library.jam.annotations.enums.ApplyFieldStrategy;
import sk.annotation.library.jam.annotations.enums.MapperFeature;

@Mapper
@DisableMapperFeature(MapperFeature.ALL)
abstract public class MapperNotNull1 {
    abstract public ObjOut toObj(ObjIn in);

    @MapperConfig(applyWhen = ApplyFieldStrategy.NEWVALUE_IS_NOT_NULL)
    abstract public ObjOut toObjIfNotNull(ObjIn in);

    @MapperConfig(applyWhen = ApplyFieldStrategy.OLDVALUE_IS_NULL)
    abstract public ObjOut toObjIfOldValueIsNull(ObjIn in);
}
