package sk.annotation.library.jam.example.ex15;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import sk.annotation.library.jam.example.ex15.subpackage.Data1;
import sk.annotation.library.jam.utils.MapperUtil;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;


public class FieldIgnoresDirectionMapperTest {
	FieldIgnoresDirectionWithTypesMapper mapper1 = MapperUtil.getMapper(FieldIgnoresDirectionWithTypesMapper.class);
	FieldIgnoresDirectionWithPackageMapper mapper2 = MapperUtil.getMapper(FieldIgnoresDirectionWithPackageMapper.class);

	@TestFactory
	public List<DynamicTest> test_clone1a2() {
		List<DynamicTest> ret = new LinkedList<>();

		createDynamicTest(ret, "clone1a2", AbstractData::createData1, "a", "a");
		createDynamicTest(ret, "clone1a2", AbstractData::createData1, "b", "b");
		createDynamicTest(ret, "clone1a2", AbstractData::createData1, null, "c");
		createDynamicTest(ret, "clone1a2", AbstractData::createData1, null, "d");
		createDynamicTest(ret, "clone1a2", AbstractData::createData1, null, "e");
		createDynamicTest(ret, "clone1a2", AbstractData::createData1, null, "f");
		createDynamicTest(ret, "clone1a2", AbstractData::createData1, null, "g");
		createDynamicTest(ret, "clone1a2", AbstractData::createData1, "h", "h");

		return ret;
	}
	@TestFactory
	public List<DynamicTest> test_clone2a1() {
		List<DynamicTest> ret = new LinkedList<>();

		createDynamicTest(ret, "clone2a1", AbstractData::createData2, "a", "a");
		createDynamicTest(ret, "clone2a1", AbstractData::createData2, "b", "b");
		createDynamicTest(ret, "clone2a1", AbstractData::createData2, null, "c");
		createDynamicTest(ret, "clone2a1", AbstractData::createData2, null, "d");
		createDynamicTest(ret, "clone2a1", AbstractData::createData2, null, "e");
		createDynamicTest(ret, "clone2a1", AbstractData::createData2, null, "f");
		createDynamicTest(ret, "clone2a1", AbstractData::createData2, "g", "g");
		createDynamicTest(ret, "clone2a1", AbstractData::createData2, null, "h");

		return ret;
	}
	@TestFactory
	public List<DynamicTest> test_clone1b2() {
		List<DynamicTest> ret = new LinkedList<>();

		createDynamicTest(ret, "clone1b2", AbstractData::createData1, "a", "a");
		createDynamicTest(ret, "clone1b2", AbstractData::createData1, "b", "b");
		createDynamicTest(ret, "clone1b2", AbstractData::createData1, null, "c");
		createDynamicTest(ret, "clone1b2", AbstractData::createData1, null, "d");
		createDynamicTest(ret, "clone1b2", AbstractData::createData1, "f", "e");
		createDynamicTest(ret, "clone1b2", AbstractData::createData1, null, "f");
		createDynamicTest(ret, "clone1b2", AbstractData::createData1, null, "g");
		createDynamicTest(ret, "clone1b2", AbstractData::createData1, "h", "h");

		return ret;
	}
	@TestFactory
	public List<DynamicTest> test_clone2b1() {
		List<DynamicTest> ret = new LinkedList<>();

		createDynamicTest(ret, "clone2b1", AbstractData::createData2, "a", "a");
		createDynamicTest(ret, "clone2b1", AbstractData::createData2, "b", "b");
		createDynamicTest(ret, "clone2b1", AbstractData::createData2, null, "c");
		createDynamicTest(ret, "clone2b1", AbstractData::createData2, null, "d");
		createDynamicTest(ret, "clone2b1", AbstractData::createData2, "f", "e");
		createDynamicTest(ret, "clone2b1", AbstractData::createData2, null, "f");
		createDynamicTest(ret, "clone2b1", AbstractData::createData2, "g", "g");
		createDynamicTest(ret, "clone2b1", AbstractData::createData2, null, "h");

		return ret;
	}


	protected <T extends AbstractData,F extends AbstractData> void createDynamicTest(List<DynamicTest> ret, String methodName, Supplier<T> creator, String sourceField, String destinationField) {
		Function<AbstractData,Object> getterDestField = findGetter(destinationField);
		Function<AbstractData,Object> getterSourceField = findGetter(sourceField);


		if ("clone2a1".equals(methodName)) {
			Function<Data2, Data1> clone2a1 = mapper1::clone2a1;
			ret.add(createDynamicTestIn("public Data1 mapper1.clone2a1(Data2 in) - field " + destinationField, creator, (Function) clone2a1, getterDestField, getterSourceField));
			clone2a1 = mapper2::clone2a1;
			ret.add(createDynamicTestIn("public Data1 mapper2.clone2a1(Data2 in) - field " + destinationField, creator, (Function) clone2a1, getterDestField, getterSourceField));
			return;
		}
		if ("clone1a2".equals(methodName)) {
			Function<Data1, Data2> clone1a2 = mapper1::clone1a2;
			ret.add(createDynamicTestIn("public Data2 mapper1.clone1a2(Data1 in) - field " + destinationField, creator, (Function) clone1a2, getterDestField, getterSourceField));
			clone1a2 = mapper2::clone1a2;
			ret.add(createDynamicTestIn("public Data2 mapper2.clone1a2(Data1 in) - field " + destinationField, creator, (Function) clone1a2, getterDestField, getterSourceField));
			return;
		}
		if ("clone2b1".equals(methodName)) {
			Function<Data2, Data1> clone2b1 = mapper1::clone2b1;
			ret.add(createDynamicTestIn("public Data1 mapper1.clone2b1(Data2 in) - field " + destinationField, creator, (Function) clone2b1, getterDestField, getterSourceField));
			clone2b1 = mapper2::clone2b1;
			ret.add(createDynamicTestIn("public Data1 mapper2.clone2b1(Data2 in) - field " + destinationField, creator, (Function) clone2b1, getterDestField, getterSourceField));
			return;
		}
		if ("clone1b2".equals(methodName)) {
			Function<Data1, Data2> clone1b2 = mapper1::clone1b2;
			ret.add(createDynamicTestIn("public Data2 mapper1.clone1b2(Data1 in) - field " + destinationField, creator, (Function) clone1b2, getterDestField, getterSourceField));
			clone1b2 = mapper2::clone1b2;
			ret.add(createDynamicTestIn("public Data2 mapper2.clone1b2(Data1 in) - field " + destinationField, creator, (Function) clone1b2, getterDestField, getterSourceField));
			return;
		}
		throw new IllegalStateException("Unknown method");
	}
	protected <T extends AbstractData,F extends AbstractData> DynamicTest createDynamicTestIn(String name, Supplier<T> creator, Function<T,F> mapper, Function<AbstractData,Object> getterDst, Function<AbstractData,Object> getterSrc) {
		return DynamicTest.dynamicTest(name + (getterSrc==null ? " - IGNORED" : " - MAPPED"), () -> {
			T in = creator.get();
			assertNotNull(in);
			F out = mapper.apply(in);
			assertNotNull(out);

			if (getterSrc==null) {
				assertNotNull(getterDst.apply(in));
				assertNull(getterDst.apply(out));
			}
			else {
				Object valIn = getterSrc.apply(in);
				assertNotNull(valIn);

				Object valOut = getterDst.apply(out);
				assertNotNull(valOut);

				assertEquals(valIn, valOut);
			}
		});
	}

	protected Function<AbstractData, Object> findGetter(String fieldSource) {
		if (fieldSource == null) return null;

		if ("a".equals(fieldSource)) return AbstractData::getA;
		if ("b".equals(fieldSource)) return AbstractData::getB;
		if ("c".equals(fieldSource)) return AbstractData::getC;
		if ("d".equals(fieldSource)) return AbstractData::getD;
		if ("e".equals(fieldSource)) return AbstractData::getE;
		if ("f".equals(fieldSource)) return AbstractData::getF;
		if ("g".equals(fieldSource)) return AbstractData::getG;
		if ("h".equals(fieldSource)) return AbstractData::getH;

		throw new IllegalStateException("Unknown field");
	}

}
