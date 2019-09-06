package sk.annotation.library.jam.example.ex12;

import sk.annotation.library.jam.annotations.Mapper;
import sk.annotation.library.jam.annotations.MapperConfig;

@Mapper
@MapperConfig(immutable = {Obj1.class})
public interface ExampleImmputableMapper {
	public Obj to2(Obj obj);
}
