package sk.annotation.library.jam.example.ex20;

import sk.annotation.library.jam.annotations.IgnoredByJamMapper;

public class OhterMapper4 {
    @IgnoredByJamMapper
    public String byteToString(byte i) {
        return i + ":" + this.getClass().getSimpleName();
    }

    @IgnoredByJamMapper
    public RefType1<Byte> createRefType1a() {
        return new RefType1<>(this.getClass().getSimpleName());
    };
}
