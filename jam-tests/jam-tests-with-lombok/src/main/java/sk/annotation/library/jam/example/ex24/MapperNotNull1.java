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

    abstract public ObjOut toObjIfNotNull1b(ObjIn in);
    abstract public ObjOut toObjIfNotNull1b(ObjIn in, ObjOut out);

    @MapperConfig(applyWhen = ApplyFieldStrategy.ALWAYS)
    abstract public ObjOut toObjIfNotNull1(ObjIn in);
    @MapperConfig(applyWhen = ApplyFieldStrategy.ALWAYS)
    abstract public ObjOut toObjIfNotNull1(ObjIn in, ObjOut out);
    @MapperConfig(applyWhen = ApplyFieldStrategy.OLDVALUE_IS_NULL)
    abstract public ObjOut toObjIfNotNull2(ObjIn in);
    @MapperConfig(applyWhen = ApplyFieldStrategy.OLDVALUE_IS_NULL)
    abstract public ObjOut toObjIfNotNull2(ObjIn in, ObjOut out);
    @MapperConfig(applyWhen = ApplyFieldStrategy.NEWVALUE_IS_NOT_NULL)
    abstract public ObjOut toObjIfNotNull3(ObjIn in);
    @MapperConfig(applyWhen = ApplyFieldStrategy.NEWVALUE_IS_NOT_NULL)
    abstract public ObjOut toObjIfNotNull3(ObjIn in, ObjOut out);

    @MapperConfig(applyWhen = ApplyFieldStrategy.OLDVALUE_IS_NULL)
    abstract public ObjOut toObjIfOldValueIsNull(ObjIn in);
}
