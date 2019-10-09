package sk.annotation.library.jam.example.ex2;

import org.junit.Test;
import sk.annotation.library.jam.utils.MapperUtil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CustomFieldMapperTest {
    @Test
    public void testCustomFieldMapper(){
        final Long id = 42L;
        final Long id2 = 43L;
        final String name = "TesterName";
        final String surname = "TesterSurname";

        UserInput input = new UserInput();
        input.setId(id);
        input.setId2(id2);
        input.setName(name);
        input.setSurname(surname);

        CustomFieldMapper mapper = MapperUtil.getMapper(CustomFieldMapper.class);

        UserOutput output = mapper.toOutput(input);

        assertNotNull(output);

        assertEquals(id2, output.getId());
        assertEquals(id, output.getId2());
        assertEquals(name, output.getFirstName());
        assertEquals(surname, output.getLastName());
    }
}
