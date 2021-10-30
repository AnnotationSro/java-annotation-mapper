import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FaileTest {
    @Test
    public void test() {
        Assertions.assertEquals(1, 2, "Test failed build in tests");
    }
}
