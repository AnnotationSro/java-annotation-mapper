package sk.annotation.library.jam.example.ex2;

import org.junit.Test;
import sk.annotation.library.jam.utils.MapperUtil;

import static org.junit.Assert.*;

public class IgnoreFieldMapperTest {
    @Test
    public void testIgnoreFieldMapper(){
        UserInput input = createUserInput();

        IgnoreFieldMapper mapper = MapperUtil.getMapper(IgnoreFieldMapper.class);

        UserOutput output = mapper.toOutput(input);

        assertNotNull(output);

        assertNull(output.getId());
        assertEquals(input.getName(), output.getFirstName());
        assertEquals(input.getSurname(), output.getLastName());

        assertNotNull(output.getDetail());

        assertNull(output.getDetail().getId());
        assertEquals(input.getDetail().getDetail(), output.getDetail().getDetail());
    }


    @Test
    public void testIgnoreFieldMapperWithId(){
        UserInput input = createUserInput();

        IgnoreFieldMapper mapper = MapperUtil.getMapper(IgnoreFieldMapper.class);

        UserOutput output = mapper.toOutputWithId(input);

        assertNotNull(output);

        assertEquals(input.getId(), output.getId());
        assertEquals(input.getName(), output.getFirstName());
        assertEquals(input.getSurname(), output.getLastName());

        assertNotNull(output.getDetail());

        assertEquals(input.getDetail().getId(), output.getDetail().getId());
        assertEquals(input.getDetail().getDetail(), output.getDetail().getDetail());
    }

    private UserInput createUserInput() {
        Long id = 42L;
        String name = "TesterName";
        String surname = "TesterSurname";

        UserInput input = new UserInput();
        input.setId(id);
        input.setName(name);
        input.setSurname(surname);

        UserDetailInput detailInput = new UserDetailInput();
        detailInput.setDetail("detail ...");
        detailInput.setId(1337L);

        input.setDetail(detailInput);
        return input;
    }
}
