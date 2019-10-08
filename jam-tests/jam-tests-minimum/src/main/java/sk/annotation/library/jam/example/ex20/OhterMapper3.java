package sk.annotation.library.jam.example.ex20;

import sk.annotation.library.jam.example.ex19.RefType1a;

public class OhterMapper3 {
    public String byteToString(byte i) {
        return i + ":OhterMapper3";
    }

    public RefType1<Byte> createRefType1a() {
        return new RefType1<>("OhterMapper3");
    };
}
