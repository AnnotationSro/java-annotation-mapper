package sk.annotation.library.jam.example.ex21;

import sk.annotation.library.jam.annotations.Mapper;
import sk.annotation.library.jam.annotations.Return;

@Mapper
abstract public class AutoDetectReturnMapper {


    public abstract ObjMerge updateLast0(ObjIn1 o1);                                                // normal rezim
    public abstract ObjMerge updateLast1(ObjIn1 o1, ObjMerge ret);                                  // autodetected @Return(true)
    public abstract ObjMerge updateLast2(ObjIn1 o1, @Return ObjMerge ret);                          // force enabled
    public abstract ObjMerge updateLast3(ObjIn1 o1, @Return(false) ObjMerge ret);                   // force disabled

    public abstract ObjMerge updateLast4(ObjIn1 o1, ObjIn2 o2, ObjMerge ret);                       // autodetected @Return(true)
    public abstract ObjMerge updateLast5(ObjIn1 o1, ObjIn2 o2, @Return ObjMerge ret);               // force enabled
    public abstract ObjMerge updateLast6(ObjIn1 o1, ObjIn2 o2, @Return(false) ObjMerge ret);        // force disabled

}
