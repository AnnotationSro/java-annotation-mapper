package sk.annotation.library.jam.example.ex6;

public class CustomBeanMapperImpl {
    public CustomBeanDataOutput toOutput(CustomBeanDataInput input){
        if (input == null) return null;

        CustomBeanDataOutput output = new CustomBeanDataOutput();
        output.setDataOutput1(input.getDataInput1());
        output.setDataOutput2(input.getDataInput2());
        output.setDataOutput3(input.getDataInput3());
        output.setDataOutput4(input.getDataInput4());

        return output;
    }
}
