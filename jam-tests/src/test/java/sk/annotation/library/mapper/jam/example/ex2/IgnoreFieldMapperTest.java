package sk.annotation.library.mapper.jam.example.ex2;

import org.junit.Test;
import sk.annotation.library.mapper.jam.utils.MapperInstanceUtil;

import static org.junit.Assert.*;

public class IgnoreFieldMapperTest {
    @Test
    public void testIgnoreFieldMapper(){
        final Long id = 42L;
        final String name = "TesterName";
        final String surname = "TesterSurname";

        UserInput input = new UserInput();
        input.setId(id);
        input.setName(name);
        input.setSurname(surname);

        IgnoreFieldMapper mapper = MapperInstanceUtil.getMapper(IgnoreFieldMapper.class);

        UserOutput output = mapper.toOutput(input);

        assertNotNull(output);

        assertNull(output.getId());
        assertEquals(name, output.getFirstName());
        assertEquals(surname, output.getLastName());
    }
}
