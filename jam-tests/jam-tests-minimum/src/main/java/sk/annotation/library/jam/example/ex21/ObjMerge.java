package sk.annotation.library.jam.example.ex21;

import java.util.Objects;

public class ObjMerge {
    private String value1;
    private String value2;

    public String getValue1() {
        return value1;
    }

    public void setValue1(String value1) {
        this.value1 = value1;
    }

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
        ObjMerge objMerge = (ObjMerge) o;
        return Objects.equals(value1, objMerge.value1) &&
                Objects.equals(value2, objMerge.value2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value1, value2);
    }
}
