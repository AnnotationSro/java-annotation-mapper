package sk.annotation.library.jam.processor.sourcewriter;

import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.annotation.processing.ProcessingEnvironment;

public class ImportsTypeDefinitionsTest {

	@Test
	public void testAmbiguousReference() {
		ImportsTypeDefinitions val = new ImportsTypeDefinitions("test.package.name") {
			protected ResolveImportStatus resolveImportStatus(ProcessingEnvironment processingEnv, String simpleName) {
				int i = simpleName.lastIndexOf(".");
				String shortName = StringUtils.substring(simpleName, i + 1);
				return new ResolveImportStatus(simpleName, shortName);
			}
		};

		val.resolveType(null, "java.util.Date", true);
		Assertions.assertEquals(1, val.realNames.size());
		val.resolveType(null, "java.sql.Date", true);
		Assertions.assertEquals(1, val.realNames.size());

	}
}
