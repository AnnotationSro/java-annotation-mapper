package sk.annotation.library.mapper.jam.example.ex6;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import sk.annotation.library.mapper.jam.utils.MapperInstanceUtil;

import java.math.BigDecimal;

public class CustomBeanMapperTest {
    @Test
    public void customBeanMapperTest(){
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

        CustomBeanMapper customBeanMapper = MapperInstanceUtil.getMapper(CustomBeanMapper.class);

        CustomBeanOutput output = customBeanMapper.toOutput(input);

        assertNotNull(output);
        assertEquals(dataInput1, output.getData().getDataOutput1());
        assertEquals(dataInput2, output.getData().getDataOutput2());
        assertEquals(dataInput3, output.getData().getDataOutput3());
        assertEquals(dataInput4, output.getData().getDataOutput4());
    }
}
