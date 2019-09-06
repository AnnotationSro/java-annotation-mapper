package sk.annotation.library.jam.example.ex17;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import sk.annotation.library.jam.utils.MapperUtil;

import java.util.*;

public class CollectionsMapperTest {

	private CollectionsMapper mapper = MapperUtil.getMapper(CollectionsMapper.class);

	@Test
	public void test_l1() {
		List<String> strNumbersIn = new ArrayList<>(Arrays.asList("1", "2", "2", "13", "31"));
		List<String> strNumbersOut = mapper.l1(strNumbersIn);

		Assertions.assertNotNull(strNumbersOut);
		Assertions.assertEquals(strNumbersIn.size(), strNumbersOut.size());
		for (int i = 0; i < strNumbersIn.size(); i++) {
			Assertions.assertEquals(strNumbersIn.get(i), strNumbersOut.get(i));
		}

		Assertions.assertNull(mapper.l1(null));
	}

	@Test
	public void test_l2() {
		List<Integer> numbersIn = new ArrayList<>(Arrays.asList(1, 2, 2, 13, 31));
		List<String> strNumbersOut = mapper.l2(numbersIn);

		Assertions.assertNotNull(strNumbersOut);
		Assertions.assertEquals(numbersIn.size(), strNumbersOut.size());
		for (int i = 0; i < numbersIn.size(); i++) {
			Assertions.assertEquals(numbersIn.get(i)+"", strNumbersOut.get(i));
		}
		Assertions.assertNull(mapper.l2(null));
	}

	@Test
	public void test_l3() {
		List<Obj1> objIn = new ArrayList<>(Arrays.asList(
			new Obj1("1"),
			new Obj1("2"),
			new Obj1("3"),
			new Obj1("4"),
			new Obj1("11")
		));
		List<Obj1> objOut = mapper.l3(objIn);

		Assertions.assertNotNull(objOut);

		Assertions.assertEquals(objIn.size(), objOut.size());
		for (int i = 0; i < objIn.size(); i++) {
			Assertions.assertNotSame(objIn.get(i), objOut.get(i));
			Assertions.assertEquals(objIn.get(i).getVal(), objOut.get(i).getVal());
		}

		Assertions.assertNull(mapper.l3(null));
	}


	@Test
	public void test_s1() {
		Set<String> strNumbersIn = new HashSet<>(Arrays.asList("1", "2", "2", "13", "31"));
		Set<String> strNumbersOut = mapper.s1(strNumbersIn);

		Assertions.assertNotNull(strNumbersOut);
		Assertions.assertEquals(strNumbersIn.size(), strNumbersOut.size());
		for (String sIn : strNumbersIn) {
			Assertions.assertTrue(strNumbersOut.contains(sIn));
		}

		Assertions.assertNull(mapper.s1(null));
	}

	@Test
	public void test_s2() {
		Set<Integer> numbersIn = new HashSet<>(Arrays.asList(1, 2, 2, 13, 31));
		Set<String> strNumbersOut = mapper.s2(numbersIn);

		Assertions.assertNotNull(strNumbersOut);
		Assertions.assertEquals(numbersIn.size(), strNumbersOut.size());
		for (Integer sIn : numbersIn) {
			Assertions.assertTrue(strNumbersOut.contains(sIn+""));
		}

		Assertions.assertNull(mapper.l1(null));
	}

	@Test
	public void test_s3() {
		Set<Obj1> objIn = new HashSet<>(Arrays.asList(
				new Obj1("1"),
				new Obj1("2"),
				new Obj1("3"),
				new Obj1("4"),
				new Obj1("11")
		));
		Set<Obj1> objOut = mapper.s3(objIn);

		Assertions.assertNotNull(objOut);
		Assertions.assertEquals(objIn.size(), objOut.size());
		for (Obj1 oIn : objIn) {
			boolean foundInOutputSet = false;
			for (Obj1 oOut : objOut) {
				if (oIn.getVal().equals(oOut.getVal())){
					foundInOutputSet = true;
					break;
				}
			}
			Assertions.assertTrue(foundInOutputSet);
		}

		Assertions.assertNull(mapper.l3(null));
	}


	@Test
	public void test_spec1() {
		Set<String> strNumbersIn = new HashSet<>(Arrays.asList("1", "2", "2", "13", "31"));
		List<String> strNumbersOut = mapper.spec1(strNumbersIn);

		Assertions.assertNotNull(strNumbersOut);
		Assertions.assertEquals(strNumbersIn.size(), strNumbersOut.size());
		for (String strOut : strNumbersOut) {
			strNumbersIn.contains(strOut);
		}

		Assertions.assertNull(mapper.spec1(null));
	}

	@Test
	public void test_spec2() {
		HashSet<String> strNumbersIn = new HashSet<>(Arrays.asList("1", "2", "2", "13", "31"));
		List<String> strNumbersOut = mapper.spec2(strNumbersIn);

		Assertions.assertNotNull(strNumbersOut);
		Assertions.assertEquals(strNumbersIn.size(), strNumbersOut.size());
		for (String strOut : strNumbersOut) {
			strNumbersIn.contains(strOut);
		}

		Assertions.assertNull(mapper.spec2(null));
	}

	@Test
	public void test_spec3() {
		HashSet<String> strNumbersIn = new HashSet<>(Arrays.asList("1", "2", "2", "13", "31"));
		ArrayList<String> strNumbersOut = mapper.spec3(strNumbersIn);

		Assertions.assertNotNull(strNumbersOut);
		Assertions.assertEquals(strNumbersIn.size(), strNumbersOut.size());
		for (String strOut : strNumbersOut) {
			strNumbersIn.contains(strOut);
		}

		Assertions.assertNull(mapper.spec3(null));
	}


	@Test
	public void test_m1() {
		Map<String, String> mapIn = new HashMap<String, String>(){{
			put("k1", "v1");
			put("k2", "v2");
			put("k3", "v3");
			put("k4", null);
		}};

		Map<String, String> mapOut = mapper.m1(mapIn);
		Assertions.assertNotNull(mapOut);
		Assertions.assertEquals(mapIn.size(), mapOut.size());
		for (String inKey : mapIn.keySet()) {
			Assertions.assertTrue(mapOut.containsKey(inKey));
			Assertions.assertEquals(mapIn.get(inKey), mapOut.get(inKey));
		}

		Assertions.assertNull(mapper.m1(null));
	}

	@Test
	public void test_m2() {
		Map<Integer, Integer> mapIn = new HashMap<Integer, Integer>(){{
			put(1, 11);
			put(2, 22);
			put(3, 33);
			put(4, null);
		}};

		Map<String, String> mapOut = mapper.m2(mapIn);
		Assertions.assertNotNull(mapOut);
		Assertions.assertEquals(mapIn.size(), mapOut.size());
		for (Integer inKey : mapIn.keySet()) {
			Assertions.assertTrue(mapOut.containsKey(inKey+""));
			Integer inValue = mapIn.get(inKey);
			String expected = inValue == null ? null : inValue + "";
			Assertions.assertEquals(expected, mapOut.get(inKey+""));
		}

		Assertions.assertNull(mapper.m2(null));
	}

	@Test
	public void test_m3() {

		Map<Integer, Obj1> mapIn = new HashMap<Integer, Obj1>(){{
			put(1, new Obj1("o1"));
			put(2, new Obj1("o2"));
			put(3, new Obj1("o3"));
			put(4, null);
		}};

		Map<String, Obj1> mapOut = mapper.m3(mapIn);
		Assertions.assertNotNull(mapOut);
		Assertions.assertEquals(mapIn.size(), mapOut.size());
		for (Integer inKey : mapIn.keySet()) {
			Assertions.assertTrue(mapOut.containsKey(inKey+""));
			Assertions.assertEquals(mapIn.get(inKey), mapOut.get(inKey+""));
		}

		Assertions.assertNull(mapper.m3(null));

	}
}
