package sk.annotation.library.jam.example.ex11;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import sk.annotation.library.jam.utils.MapperUtil;

import java.util.Arrays;
import java.util.List;

public class EnumerationMapperTest {

    EnumerationMapper mapper = MapperUtil.getMapper(EnumerationMapper.class);

    private DynamicTest test1_2(Enum2 expVal, Enum1 valIn) {
        return DynamicTest.dynamicTest("Transformation (Enum1->Enum2) = " + valIn + " -> " + expVal + ".", () -> {
            Assertions.assertEquals(expVal, mapper.to2(valIn));
        });
    }


    @TestFactory
    public List<DynamicTest> test_Enum1_to_Enum2() {
        return Arrays.asList(
                test1_2(Enum2.VALUE_OK_1, Enum1.VALUE_OK_1),
                test1_2(Enum2.VALUE_OK_2, Enum1.VALUE_OK_2),
                test1_2(null, Enum1.VALUE_ONLY_1),
                // NOT POSSIBLE :test1_2(Enum2.VALUE_ONLY_2, Enum1.VALUE_ONLY_2),
                test1_2(null, null)
        );
    }

    private DynamicTest test2_1(Enum1 expVal, Enum2 valIn) {
        return DynamicTest.dynamicTest("Transformation (Enum1->Enum2) = " + valIn + " -> " + expVal + ".", () -> {
            Assertions.assertEquals(expVal, mapper.to1(valIn));
        });
    }
    @TestFactory
    public List<DynamicTest> test_Enum2_to_Enum1() {
        return Arrays.asList(
                test2_1(Enum1.VALUE_OK_1, Enum2.VALUE_OK_1),
                test2_1(Enum1.VALUE_OK_2, Enum2.VALUE_OK_2),
                // NOT POSSIBLE :test2_1(Enum1.VALUE_ONLY_1, Enum2.VALUE_ONLY_1),
                test2_1(null, Enum2.VALUE_ONLY_2),
                test2_1(null, null)
        );
    }

}
