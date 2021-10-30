package sk.annotation.library.jam.example.ex23;

import sk.annotation.library.jam.annotations.DisableMapperFeature;
import sk.annotation.library.jam.annotations.Mapper;
import sk.annotation.library.jam.annotations.enums.MapperFeature;

import java.util.List;

@Mapper
@DisableMapperFeature(MapperFeature.ALL)
abstract public class MapperWithGenerics {

    protected <T> MyTypeObj<T> createMyTypeObj(T value) {
        MyTypeObj<T> ret = new MyTypeObj<T>();
        ret.setObj(value);
        return ret;
    }
    abstract public MyTypeObj<Long> convertLong(Long o);
    abstract public MyTypeObj<String> convertString(String o);
    abstract public MyTypeObj<String> convertUncompatible(Long o);
//    abstract public <T extends MyTypeObj<String>> T myUpdate(Long o, T out);

    abstract public ObjOut convert(ObjIn o);

    public int cnt1 = 0;
    public int cnt2 = 0;
    public int cnt3 = 0;
    public int cnt4 = 0;

    protected void interceptor1(Object o1, Object o2) {cnt1++;}
    protected <T> void interceptor2(T o1, Object o2) {cnt2++;}
    protected <T extends Long> void interceptor3(T o1, Object o2) {cnt3++;}
    protected <T, MYOBJ extends MyTypeObj<T>, LST_OUT extends List<MYOBJ>, LST_IN extends List<T>> void interceptor4(LST_IN o1, LST_OUT o2) {cnt4++;}

}
