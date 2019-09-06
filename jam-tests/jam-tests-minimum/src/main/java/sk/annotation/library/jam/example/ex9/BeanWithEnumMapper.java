package sk.annotation.library.jam.example.ex9;

import sk.annotation.library.jam.annotations.Mapper;

@Mapper
public interface BeanWithEnumMapper {
    UserWithRoleOutput toOutput(UserWithRoleInput input);
}
