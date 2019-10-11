package sk.annotation.library.jam.example.ex22;

import java.math.BigInteger;

public class Ext4Mapper {
    public String toStr(BigInteger value) {
        return value + ":" + this.getClass().getSimpleName();
    }
}
