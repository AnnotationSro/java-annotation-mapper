package sk.annotation.library.jam.example.ex20;

import sk.annotation.library.jam.example.ex19.RefType1a;

public class OhterMapper1 {
    public String convInterToString(Integer i) {
        return i + ":OhterMapper1";
    }
    public RefType1<Integer> createRefType1a() {
        return new RefType1<>("OhterMapper1");
    };
}
