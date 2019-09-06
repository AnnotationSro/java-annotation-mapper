package sk.annotation.library.jam.example.ex16;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import sk.annotation.library.jam.example.ex15.AbstractData;
import sk.annotation.library.jam.example.ex15.Data2;
import sk.annotation.library.jam.utils.MapperUtil;

public class CustomFieldMapperTest {

	private CustomFieldMapper mapper = MapperUtil.getMapper(CustomFieldMapper.class);

	@Test
	public void test_Data2b_to_Data2 () {
		Data2b i = Data2b.createOutData2();
		Data2 o = mapper.transf1(i);

		Assertions.assertNotNull(i);
		Assertions.assertNotNull(o);

		//@FieldMapping(s = "a", d = "b", ignoreDirectionD2S = false, ignoreDirectionS2D = false),
		assertTransform(i.getA(), o.getB(), false);
		assertTransform(i.getB(), o.getA(), false);

		//@FieldMapping(s = "c", d = "d", ignoreDirectionD2S = true, ignoreDirectionS2D = false),
		assertTransform(i.getC(), o.getD(), false);
		assertTransform(i.getD(), o.getC(), true);

		//@FieldMapping(s = "e", d = "f", ignoreDirectionD2S = false, ignoreDirectionS2D = true)
		assertTransform(i.getE(), o.getF(), true);
		assertTransform(i.getF(), o.getE(), false);

		//@FieldMapping(s = "g", d = "h", ignoreDirectionD2S = true, ignoreDirectionS2D = true),
		assertTransform(i.getG(), o.getH(), true);
		assertTransform(i.getH(), o.getG(), true);

		//@FieldMapping(
		//	srcObj = Data2.class, s = "d1.a", ignoreDirectionS2D = false,
		//	dstObj = Data2b.class, d = "d1_a", ignoreDirectionD2S = false
		//),
		assertTransform(i.getD1_a(), o.getD1().getA(), false);

		//@FieldMapping(
		//	srcObj = IData.class, s = "d1.b", ignoreDirectionS2D = false,
		//	dstObj = Data2b.class, d = "d1_b", ignoreDirectionD2S = true
		//),
		assertTransform(i.getD1_b(), o.getD1().getB(), true);

		//@FieldMapping(
		//	srcObj = Data2.class, s = "d1.c", ignoreDirectionS2D = true
		//	dstObj = Data2b.class, d = "d1_c", ignoreDirectionD2S = false
		//),
		assertTransform(i.getD1_c(), o.getD1().getC(), false);
	}

	@Test
	public void test_Data2_to_Data2b () {
		Data2 i = AbstractData.createData2();
		Data2b o = mapper.transf2(i);

		Assertions.assertNotNull(i);
		Assertions.assertNotNull(o);

		//@FieldMapping(s = "a", d = "b", ignoreDirectionD2S = false, ignoreDirectionS2D = false),
		assertTransform(i.getA(), o.getB(), false);
		assertTransform(i.getB(), o.getA(), false);

		//@FieldMapping(s = "c", d = "d", ignoreDirectionD2S = true, ignoreDirectionS2D = false),
		assertTransform(i.getC(), o.getD(), false);
		assertTransform(i.getD(), o.getC(), true);

		//@FieldMapping(s = "e", d = "f", ignoreDirectionD2S = false, ignoreDirectionS2D = true)
		assertTransform(i.getE(), o.getF(), true);
		assertTransform(i.getF(), o.getE(), false);

		//@FieldMapping(s = "g", d = "h", ignoreDirectionD2S = true, ignoreDirectionS2D = true),
		assertTransform(i.getG(), o.getH(), true);
		assertTransform(i.getH(), o.getG(), true);

		//@FieldMapping(
		//	srcObj = Data2.class, s = "d1.a", ignoreDirectionS2D = false,
		//	dstObj = Data2b.class, d = "d1_a", ignoreDirectionD2S = false
		//),
		assertTransform( i.getD1().getA(), o.getD1_a(), false);

		//@FieldMapping(
		//	srcObj = IData.class, s = "d1.b", ignoreDirectionS2D = false,
		//	dstObj = Data2b.class, d = "d1_b", ignoreDirectionD2S = true
		//),
		assertTransform(i.getD1().getB(),o.getD1_b(),  false);

		//@FieldMapping(
		//	srcObj = Data2.class, s = "d1.c", ignoreDirectionS2D = true
		//	dstObj = Data2b.class, d = "d1_c", ignoreDirectionD2S = false
		//),
		assertTransform(i.getD1().getC(), o.getD1_c(),  true);
	}

	private static void assertTransform(String from, String to, boolean wasIgnored) {
		Assertions.assertNotNull(from);
		if (wasIgnored) {
			Assertions.assertNull(to);
		}
		else {
			Assertions.assertEquals(from, to);
		}
	}
}
