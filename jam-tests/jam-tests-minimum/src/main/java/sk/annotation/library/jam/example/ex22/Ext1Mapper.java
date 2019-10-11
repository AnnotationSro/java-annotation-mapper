package sk.annotation.library.jam.example.ex22;

public class Ext1Mapper {
    public String toStr(Long value) {
        return value + ":" + this.getClass().getSimpleName();
    }
}
