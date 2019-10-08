package sk.annotation.library.jam.example.ex20;

import sk.annotation.library.jam.annotations.IgnoredByJamMapper;
import sk.annotation.library.jam.example.ex19.RefType1a;

public class OhterMapper4 {
    @IgnoredByJamMapper
    public String byteToString(byte i) {
        return i + ":OhterMapper4";
    }

    @IgnoredByJamMapper
    public RefType1<Byte> createRefType1a() {
        return new RefType1<>("OhterMapper4");
    };
}
