package sk.annotation.library.jam.example.ex17;

import sk.annotation.library.jam.annotations.DisableMapperFeature;
import sk.annotation.library.jam.annotations.Mapper;
import sk.annotation.library.jam.annotations.enums.MapperFeature;

import java.util.*;

@Mapper

@DisableMapperFeature({MapperFeature.PREVENT_CYCLIC_MAPPING, MapperFeature.METHOD_SUPPORTS_CONTEXT_PARAMETERS, MapperFeature.PERSISTED_DATA_IN_LOCAL_THREAD})
public interface CollectionsMapper {
	List<String> l1(List<String> in);
	List<String> l2(List<Integer> in);
	List<Obj1> l3(List<Obj1> in);

	Set<String> s1(Set<String> in);
	Set<String> s2(Set<Integer> in);
	Set<Obj1> s3(Set<Obj1> in);

	List<String> spec1(Set<String> in);
	List<String> spec2(HashSet<String> in);
	ArrayList<String> spec3(HashSet<String> in);

	Map<String, String> m1(Map<String, String> in);
	Map<String, String> m2(Map<Integer, Integer> in);
	Map<String, Obj1> m3(Map<Integer, Obj1> in);
}
