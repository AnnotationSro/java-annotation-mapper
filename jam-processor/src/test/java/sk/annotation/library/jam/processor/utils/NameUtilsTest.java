package sk.annotation.library.jam.processor.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NameUtilsTest {
	@Test
	public void testPackage() {
		String packageName = "sk.daco.TestDaco.Nieco";
		String objName = "Abcd";
		Assertions.assertEquals(packageName, NameUtils.getUpperPackage(packageName + "." + objName));
	}
	@Test
	public void testSimpleName() {
		String packageName = "sk.daco.TestDaco.Nieco";
		String objName = "Abcd";
		Assertions.assertEquals(objName, NameUtils.getClassSimpleName(packageName + "." + objName));
	}
}
