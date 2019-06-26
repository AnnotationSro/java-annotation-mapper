package sk.annotation.library.mapper.jam.example.ex9;

import sk.annotation.library.mapper.jam.annotations.JamMapper;

//@JamMapper TODO: compile errors in generated Impl class (problem with enum instantiation)
public interface BeanWithEnumMapper {
    UserWithRoleOutput toOutput(UserWithRoleInput input);
}
