package sk.annotation.library.jam.example.ex20;

import sk.annotation.library.jam.annotations.IgnoredByJamMapper;

public class OhterMapper4 {
    @IgnoredByJamMapper
    public String byteToString(byte i) {
        return i + ":OhterMapper4";
    }
}
