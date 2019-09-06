package sk.annotation.library.jam.example.ex3;

import org.junit.Test;
import sk.annotation.library.jam.utils.MapperUtil;

import static org.junit.Assert.*;

public class CopyMapperTest {
    @Test
    public void testIgnoreFieldMapper(){
        final Long id = 42L;
        final String message = "This is just test message";

        BeanToCopy input = new BeanToCopy();
        input.setId(id);
        input.setMessage(message);

        CopyMapper mapper = MapperUtil.getMapper(CopyMapper.class);

        BeanToCopy output = mapper.toOutput(input);

        assertNotNull(output);
        assertNotEquals(input, output);

        assertEquals(input.getId(), input.getId());
        assertEquals(input.getMessage(), output.getMessage());
    }
}
