package sk.annotation.library.jam.example.ex24;

import lombok.Data;

import java.util.List;

@Data
public class ObjOut {
    private Long test1_Long2Long;
    private long test2_Long2long;
    private Long test3_long2Long;
    private long test4_long2long;

    private List<Long> valueLongInList;
}
