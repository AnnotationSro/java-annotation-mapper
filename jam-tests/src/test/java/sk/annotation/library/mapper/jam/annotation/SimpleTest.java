package sk.annotation.library.mapper.jam.annotation;

import java.lang.annotation.Annotation;

//@JamMapper - find problem
public interface SimpleTest {
 	public DataType1b toDataType(DataType2 val);


	default public DataType1b toDataType2(DataType2 val) {
		DataType1b ret = new DataType1b();
		ret.setId(val.getId());
		val.setId(ret.getId());
		return ret;
	}



	public static void main(String[] args) {
		System.out.println(SimpleTest.class.getAnnotations().length + "x runtime Annotation for " + SimpleTest.class.getCanonicalName() + ":");
		for (Annotation annotation : SimpleTest.class.getAnnotations()) {
			System.out.println(" - " + annotation);
		}

	}

}
