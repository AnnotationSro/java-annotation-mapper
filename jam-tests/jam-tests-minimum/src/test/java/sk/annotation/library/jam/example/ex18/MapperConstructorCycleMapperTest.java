package sk.annotation.library.jam.example.ex18;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import sk.annotation.library.jam.utils.MapperUtil;

public class MapperConstructorCycleMapperTest {
	@Test
	public void testMapper() {
		MapperConstructorCycleMapper mapper = MapperUtil.getMapper(MapperConstructorCycleMapper.class);
		Assertions.assertNotNull(mapper);
		Assertions.assertNotNull(mapper.mapper);
		Assertions.assertEquals(mapper, mapper.mapper);
	}
}
