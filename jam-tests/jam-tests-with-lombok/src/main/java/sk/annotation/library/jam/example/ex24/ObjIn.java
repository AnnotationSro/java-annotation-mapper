package sk.annotation.library.jam.example.ex24;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ObjIn {
    private Long test1_Long2Long;
    private Long test2_Long2long;
    private long test3_long2Long;
    private long test4_long2long;

    private List<Long> valueLongInList;
}
