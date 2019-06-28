package sk.annotation.library.jam.example.ex2;

import org.junit.Test;
import sk.annotation.library.jam.utils.MapperInstanceUtil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CustomFieldMapperTest {
    @Test
    public void testCustomFieldMapper(){
        final Long id = 42L;
        final String name = "TesterName";
        final String surname = "TesterSurname";

        UserInput input = new UserInput();
        input.setId(id);
        input.setName(name);
        input.setSurname(surname);

        CustomFieldMapper mapper = MapperInstanceUtil.getMapper(CustomFieldMapper.class);

        UserOutput output = mapper.toOutput(input);

        assertNotNull(output);

        assertEquals(id, output.getId());
        assertEquals(name, output.getFirstName());
        assertEquals(surname, output.getLastName());
    }
}
