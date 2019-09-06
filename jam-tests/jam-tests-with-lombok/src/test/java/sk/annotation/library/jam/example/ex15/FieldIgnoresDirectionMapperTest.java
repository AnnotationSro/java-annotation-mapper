package sk.annotation.library.jam.example.ex15;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import sk.annotation.library.jam.utils.MapperUtil;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;


public class FieldIgnoresDirectionMapperTest {
	FieldIgnoresDirectionMapper mapper = MapperUtil.getMapper(FieldIgnoresDirectionMapper.class);

	@TestFactory
	public List<DynamicTest> test_clone1a2() {
		List<DynamicTest> ret = new LinkedList<>();

		ret.add(createDynamicTest("clone1a2", AbstractData::createData1, "a", "a"));
		ret.add(createDynamicTest("clone1a2", AbstractData::createData1, "b", "b"));
		ret.add(createDynamicTest("clone1a2", AbstractData::createData1, null, "c"));
		ret.add(createDynamicTest("clone1a2", AbstractData::createData1, null, "d"));
		ret.add(createDynamicTest("clone1a2", AbstractData::createData1, null, "e"));
		ret.add(createDynamicTest("clone1a2", AbstractData::createData1, null, "f"));
		ret.add(createDynamicTest("clone1a2", AbstractData::createData1, null, "g"));
		ret.add(createDynamicTest("clone1a2", AbstractData::createData1, "h", "h"));

		return ret;
	}
	@TestFactory
	public List<DynamicTest> test_clone2a1() {
		List<DynamicTest> ret = new LinkedList<>();

		ret.add(createDynamicTest("clone2a1", AbstractData::createData2, "a", "a"));
		ret.add(createDynamicTest("clone2a1", AbstractData::createData2, "b", "b"));
		ret.add(createDynamicTest("clone2a1", AbstractData::createData2, null, "c"));
		ret.add(createDynamicTest("clone2a1", AbstractData::createData2, null, "d"));
		ret.add(createDynamicTest("clone2a1", AbstractData::createData2, null, "e"));
		ret.add(createDynamicTest("clone2a1", AbstractData::createData2, null, "f"));
		ret.add(createDynamicTest("clone2a1", AbstractData::createData2, "g", "g"));
		ret.add(createDynamicTest("clone2a1", AbstractData::createData2, null, "h"));

		return ret;
	}
	@TestFactory
	public List<DynamicTest> test_clone1b2() {
		List<DynamicTest> ret = new LinkedList<>();

		ret.add(createDynamicTest("clone1b2", AbstractData::createData1, "a", "a"));
		ret.add(createDynamicTest("clone1b2", AbstractData::createData1, "b", "b"));
		ret.add(createDynamicTest("clone1b2", AbstractData::createData1, null, "c"));
		ret.add(createDynamicTest("clone1b2", AbstractData::createData1, null, "d"));
		ret.add(createDynamicTest("clone1b2", AbstractData::createData1, "f", "e"));
		ret.add(createDynamicTest("clone1b2", AbstractData::createData1, null, "f"));
		ret.add(createDynamicTest("clone1b2", AbstractData::createData1, null, "g"));
		ret.add(createDynamicTest("clone1b2", AbstractData::createData1, "h", "h"));

		return ret;
	}
	@TestFactory
	public List<DynamicTest> test_clone2b1() {
		List<DynamicTest> ret = new LinkedList<>();

		ret.add(createDynamicTest("clone2b1", AbstractData::createData2, "a", "a"));
		ret.add(createDynamicTest("clone2b1", AbstractData::createData2, "b", "b"));
		ret.add(createDynamicTest("clone2b1", AbstractData::createData2, null, "c"));
		ret.add(createDynamicTest("clone2b1", AbstractData::createData2, null, "d"));
		ret.add(createDynamicTest("clone2b1", AbstractData::createData2, "f", "e"));
		ret.add(createDynamicTest("clone2b1", AbstractData::createData2, null, "f"));
		ret.add(createDynamicTest("clone2b1", AbstractData::createData2, "g", "g"));
		ret.add(createDynamicTest("clone2b1", AbstractData::createData2, null, "h"));

		return ret;
	}


	protected <T extends AbstractData,F extends AbstractData> DynamicTest createDynamicTest(String methodName, Supplier<T> creator, String sourceField, String destinationField) {
		Function<AbstractData,Object> getterDestField = findGetter(destinationField);
		Function<AbstractData,Object> getterSourceField = findGetter(sourceField);

		if ("clone2a1".equals(methodName)) {
			Function<Data2, Data1> clone2a1 = mapper::clone2a1;
			return createDynamicTestIn("public Data1 clone2a1(Data2 in) - field " + destinationField, creator, (Function) clone2a1, getterDestField, getterSourceField);
		}
		if ("clone1a2".equals(methodName)) {
			Function<Data1, Data2> clone1a2 = mapper::clone1a2;
			return createDynamicTestIn("public Data2 clone1a2(Data1 in) - field " + destinationField, creator, (Function) clone1a2, getterDestField, getterSourceField);
		}
		if ("clone2b1".equals(methodName)) {
			Function<Data2, Data1> clone2b1 = mapper::clone2b1;
			return createDynamicTestIn("public Data1 clone2b1(Data2 in) - field " + destinationField, creator, (Function) clone2b1, getterDestField, getterSourceField);
		}
		if ("clone1b2".equals(methodName)) {
			Function<Data1, Data2> clone1b2 = mapper::clone1b2;
			return createDynamicTestIn("public Data2 clone1b2(Data1 in) - field " + destinationField, creator, (Function) clone1b2, getterDestField, getterSourceField);
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
