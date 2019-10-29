package sk.annotation.library.jam.example.ex20;

import sk.annotation.library.jam.annotations.JamVisibility;
import sk.annotation.library.jam.annotations.enums.MapperVisibility;

public class OhterMapper4 {
    @JamVisibility(MapperVisibility.IGNORED)
    public String byteToString(byte i) {
        return i + ":" + this.getClass().getSimpleName();
    }

    @JamVisibility(MapperVisibility.IGNORED)
    public RefType1<Byte> createRefType1a() {
        return new RefType1<>(this.getClass().getSimpleName());
    };
}
