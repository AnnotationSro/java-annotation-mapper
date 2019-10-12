package sk.annotation.library.jam.example.ex23;

import java.util.List;

public class ObjIn {
    private Long valueLong;
    private String valueString;

    private List<Long> valueLongInList;

    public Long getValueLong() {
        return valueLong;
    }

    public void setValueLong(Long valueLong) {
        this.valueLong = valueLong;
    }

    public String getValueString() {
        return valueString;
    }

    public void setValueString(String valueString) {
        this.valueString = valueString;
    }

    public List<Long> getValueLongInList() {
        return valueLongInList;
    }

    public void setValueLongInList(List<Long> valueLongInList) {
        this.valueLongInList = valueLongInList;
    }
}
