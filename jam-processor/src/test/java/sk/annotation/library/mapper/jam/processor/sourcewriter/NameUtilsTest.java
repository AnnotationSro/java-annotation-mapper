package sk.annotation.library.mapper.jam.processor.sourcewriter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import sk.annotation.library.mapper.jam.processor.utils.NameUtils;

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
