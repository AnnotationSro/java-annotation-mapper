package sk.annotation.library.mapper.jam.example.ex1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import sk.annotation.library.mapper.jam.utils.MapperInstanceUtil;

public class SimpleMapperTest {
    @Test
    public void testSimpleMapper(){
        final String name = "TesterName";
        final String surname = "TesterSurname";

        UserInput input = new UserInput();
        input.setName(name);
        input.setSurname(surname);

        SimpleMapper mapper = MapperInstanceUtil.getMapper(SimpleMapper.class);

        UserOutput output = mapper.toOutput(input);

        assertNotNull(output);

        assertEquals(name, output.getName());
        assertEquals(surname, output.getSurname());
    }
}
