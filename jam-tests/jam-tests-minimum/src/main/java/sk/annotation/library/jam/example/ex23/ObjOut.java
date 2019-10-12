package sk.annotation.library.jam.example.ex23;

import java.util.List;

public class ObjOut {
    private MyTypeObj<Long> valueLong;
    private MyTypeObj<String> valueString;

    private List<MyTypeObj<Long>> valueLongInList;

    public MyTypeObj<Long> getValueLong() {
        return valueLong;
    }

    public void setValueLong(MyTypeObj<Long> valueLong) {
        this.valueLong = valueLong;
    }

    public MyTypeObj<String> getValueString() {
        return valueString;
    }

    public void setValueString(MyTypeObj<String> valueString) {
        this.valueString = valueString;
    }

    public List<MyTypeObj<Long>> getValueLongInList() {
        return valueLongInList;
    }

    public void setValueLongInList(List<MyTypeObj<Long>> valueLongInList) {
        this.valueLongInList = valueLongInList;
    }
}
