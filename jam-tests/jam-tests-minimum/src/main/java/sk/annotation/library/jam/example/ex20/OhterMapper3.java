package sk.annotation.library.jam.example.ex20;

public class OhterMapper3 {
    public String byteToString(byte i) {
        return i + ":" + this.getClass().getSimpleName();
    }

    public RefType1<Byte> createRefType1a() {
        return new RefType1<>(this.getClass().getSimpleName());
    };
}
