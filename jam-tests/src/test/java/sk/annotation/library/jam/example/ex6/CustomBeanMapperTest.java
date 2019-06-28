package sk.annotation.library.jam.example.ex6;

import org.junit.Test;
import sk.annotation.library.jam.utils.MapperInstanceUtil;

import java.lang.reflect.Field;
import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CustomBeanMapperTest {

    @Test
    public void customBeanMapper1Test() throws NoSuchFieldException, IllegalAccessException {
        CustomBeanMapper1 customBeanMapper = MapperInstanceUtil.getMapper(CustomBeanMapper1.class);
        Field f1 = customBeanMapper.getClass().getDeclaredField("customBeanMapperImpl");
        f1.setAccessible(true);
        f1.set(customBeanMapper, new CustomBeanMapperImpl());

        CustomBeanInput input = createInput();
        CustomBeanOutput output = customBeanMapper.toOutput(input);
        asserOutput(input, output);
    }


    @Test
    public void customBeanMapper2Test() {
        CustomBeanMapper2 customBeanMapper = MapperInstanceUtil.getMapper(CustomBeanMapper2.class);
        customBeanMapper.otherMapper = new CustomBeanMapperImpl();

        CustomBeanInput input = createInput();
        CustomBeanOutput output = customBeanMapper.toOutput(input);
        asserOutput(input, output);
    }

    protected CustomBeanInput createInput() {
        final String dataInput1 = "TestInput1";
        final String dataInput2 = "TestInput2";
        final BigDecimal dataInput3 = new BigDecimal("13.37");
        final Long dataInput4 = 42L;

        CustomBeanDataInput dataInput = new CustomBeanDataInput();
        dataInput.setDataInput1(dataInput1);
        dataInput.setDataInput2(dataInput2);
        dataInput.setDataInput3(dataInput3);
        dataInput.setDataInput4(dataInput4);

        CustomBeanInput input = new CustomBeanInput();
        input.setData(dataInput);
        return input;
    }

    public void asserOutput(CustomBeanInput dataInput, CustomBeanOutput output) {
        assertNotNull(output);
        assertEquals(dataInput.getData().getDataInput1(), output.getData().getDataOutput1());
        assertEquals(dataInput.getData().getDataInput2(), output.getData().getDataOutput2());
        assertEquals(dataInput.getData().getDataInput3(), output.getData().getDataOutput3());
        assertEquals(dataInput.getData().getDataInput4(), output.getData().getDataOutput4());

    }
}
