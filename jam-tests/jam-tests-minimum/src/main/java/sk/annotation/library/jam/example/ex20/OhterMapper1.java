package sk.annotation.library.jam.example.ex20;

public class OhterMapper1 {
    public String convInterToString(Integer i) {
        return i + ":" + this.getClass().getSimpleName();
    }
    public RefType1<Integer> createRefType1a() {
        return new RefType1<>(this.getClass().getSimpleName());
    };
}
