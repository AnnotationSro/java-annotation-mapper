import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FaileTest {
    @Test
    public void test() {
        if (!System.getProperty("java.version").startsWith("1.8")) {
            Assertions.assertEquals(1, 2, "Test failed build in tests " + System.getProperty("java.version"));
        }
    }
}
