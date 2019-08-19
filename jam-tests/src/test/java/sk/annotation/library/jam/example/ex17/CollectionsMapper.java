package sk.annotation.library.jam.example.ex17;

import sk.annotation.library.jam.annotations.DisableMapperFeature;
import sk.annotation.library.jam.annotations.FieldMapping;
import sk.annotation.library.jam.annotations.Mapper;
import sk.annotation.library.jam.annotations.MapperFieldConfig;
import sk.annotation.library.jam.annotations.enums.MapperFeature;
import sk.annotation.library.jam.example.ex15.Data2;
import sk.annotation.library.jam.example.ex15.IData;
import sk.annotation.library.jam.example.ex16.Data2b;

import java.util.*;

@Mapper

@DisableMapperFeature({MapperFeature.PREVENT_CYCLIC_MAPPING, MapperFeature.METHOD_SUPPORTS_CONTEXT_PARAMETERS, MapperFeature.PERSISTED_DATA_IN_LOCAL_THREAD})
public interface CollectionsMapper {
	List<String> l1(List<String> in);
	List<String> l2(List<Integer> in);
	List<Data2b> l3(List<Data2b> in);

	Set<String> s1(Set<String> in);
	Set<String> s2(Set<Integer> in);
	Set<Data2b> s3(Set<Data2b> in);

	List<String> spec1(Set<String> in);
	List<String> spec2(HashSet<String> in);
	ArrayList<String> spec3(HashSet<String> in);

	Map<String, String> m1(Map<String, String> in);
	Map<String, String> m2(Map<Integer, Integer> in);
	Map<String, Data2b> m3(Map<Integer, Data2b> in);
}
