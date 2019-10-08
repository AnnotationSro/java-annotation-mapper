package sk.annotation.library.jam.example.ex21;

import java.util.Objects;

public class ObjIn2 {
    private String value2;

    public String getValue2() {
        return value2;
    }

    public void setValue2(String value2) {
        this.value2 = value2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ObjIn2 objIn2 = (ObjIn2) o;
        return Objects.equals(value2, objIn2.value2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value2);
    }
}
