package sk.annotation.library.jam.processor.ex14;

import sk.annotation.library.jam.annotations.Mapper;

@Mapper
public interface SimpleTypesMapper {
	public int t1(Integer obj);
	public Integer t2(int obj);

}
