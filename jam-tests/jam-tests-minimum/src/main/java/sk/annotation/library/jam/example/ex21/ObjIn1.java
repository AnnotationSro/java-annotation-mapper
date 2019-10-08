package sk.annotation.library.jam.example.ex21;

import java.util.Objects;

public class ObjIn1 {
    private String value1;

    public String getValue1() {
        return value1;
    }

    public void setValue1(String value1) {
        this.value1 = value1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ObjIn1 objIn1 = (ObjIn1) o;
        return Objects.equals(value1, objIn1.value1);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value1);
    }
}
