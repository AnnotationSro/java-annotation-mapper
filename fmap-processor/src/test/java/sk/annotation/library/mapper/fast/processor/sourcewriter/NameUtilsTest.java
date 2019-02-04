package sk.annotation.library.mapper.fast.processor.sourcewriter;

import org.junit.Assert;
import org.junit.Test;
import sk.annotation.library.mapper.fast.processor.utils.NameUtils;

public class NameUtilsTest {
	@Test
	public void testPackage() {
		String packageName = "sk.daco.TestDaco.Nieco";
		String objName = "Abcd";
		Assert.assertEquals(packageName, NameUtils.getUpperPackage(packageName + "." + objName));
	}
	@Test
	public void testSimpleName() {
		String packageName = "sk.daco.TestDaco.Nieco";
		String objName = "Abcd";
		Assert.assertEquals(objName, NameUtils.getClassSimpleName(packageName + "." + objName));
	}
}
