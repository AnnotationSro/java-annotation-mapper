package sk.annotation.library.jam.example.ex20;

import sk.annotation.library.jam.example.ex19.RefType1a;

public class OhterMapper2 {
    public String convLongToString(Long i) {
        return i + ":OhterMapper2";
    }

    public RefType1<Long> createRefType1a() {
        return new RefType1<>("OhterMapper2");
    };
}
