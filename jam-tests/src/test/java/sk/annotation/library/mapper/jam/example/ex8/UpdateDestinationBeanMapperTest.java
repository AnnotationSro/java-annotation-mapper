package sk.annotation.library.mapper.jam.example.ex8;

import org.junit.Test;
import sk.annotation.library.mapper.jam.example.ex1.UserInput;
import sk.annotation.library.mapper.jam.example.ex1.UserOutput;
import sk.annotation.library.mapper.jam.example.ex4.AddressInput;
import sk.annotation.library.mapper.jam.example.ex5.NestedClassListMapper;
import sk.annotation.library.mapper.jam.example.ex5.UserWithAddressInput;
import sk.annotation.library.mapper.jam.example.ex5.UserWithAddressOutput;
import sk.annotation.library.mapper.jam.utils.MapperInstanceUtil;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class UpdateDestinationBeanMapperTest {
    @Test
    public void testNestedClassMapper(){
        final String name = "TesterName";
        final String surname = "TesterSurname";

        UserInput input = new UserInput();
        input.setName(name);
        input.setSurname(surname);

        UpdateDestinationBeanMapper mapper = MapperInstanceUtil.getMapper(UpdateDestinationBeanMapper.class);

        UserOutput output = new UserOutput();

        UserOutput returnedOutput = mapper.toOutput(input, output);

        assertNotNull(output);

        assertEquals(output, returnedOutput);

        assertEquals(name, output.getName());
        assertEquals(surname, output.getSurname());
    }
}
