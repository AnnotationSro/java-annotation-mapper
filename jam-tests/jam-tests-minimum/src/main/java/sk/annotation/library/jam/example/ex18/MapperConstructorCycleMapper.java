package sk.annotation.library.jam.example.ex18;

import sk.annotation.library.jam.annotations.Mapper;
import sk.annotation.library.jam.utils.MapperUtil;

@Mapper
abstract public class MapperConstructorCycleMapper  {
	public MapperConstructorCycleMapper mapper = MapperUtil.getMapper(MapperConstructorCycleMapper.class, this);

	public abstract int toInt(String i);
	public abstract String toInt(int i);
}
