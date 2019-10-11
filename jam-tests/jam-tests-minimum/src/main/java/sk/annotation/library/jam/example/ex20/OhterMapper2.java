package sk.annotation.library.jam.example.ex20;

public class OhterMapper2 {
    public String convLongToString(Long i) {
        return i + ":" + this.getClass().getSimpleName();
    }

    public RefType1<Long> createRefType1a() {
        return new RefType1<>(this.getClass().getSimpleName());
    };
}
