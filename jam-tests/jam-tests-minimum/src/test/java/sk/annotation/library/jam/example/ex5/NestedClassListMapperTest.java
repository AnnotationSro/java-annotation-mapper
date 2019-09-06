package sk.annotation.library.jam.example.ex5;

import org.junit.Test;
import sk.annotation.library.jam.example.ex4.AddressInput;
import sk.annotation.library.jam.utils.MapperUtil;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class NestedClassListMapperTest {
    @Test
    public void testNestedClassMapper(){
        final String name = "TesterName";
        final String surname = "TesterSurname";

        final String street = "TestStreet";
        final String city = "TestCity";
        final String zip = "TestZip";

        UserWithAddressInput input = new UserWithAddressInput();
        input.setName(name);
        input.setSurname(surname);
        input.setAddresses(new ArrayList<>());

        AddressInput a = new AddressInput();
        a.setStreet(street);
        a.setCity(city);
        a.setZipCode(zip);
        input.getAddresses().add(a);

        NestedClassListMapper mapper = MapperUtil.getMapper(NestedClassListMapper.class);

        UserWithAddressOutput output = mapper.toOutput(input);

        assertNotNull(output);

        assertEquals(name, output.getName());
        assertEquals(surname, output.getSurname());
        assertEquals(street, output.getAddresses().get(0).getStreet());
        assertEquals(city, output.getAddresses().get(0).getCity());
        assertEquals(zip, output.getAddresses().get(0).getZip());
    }
}
