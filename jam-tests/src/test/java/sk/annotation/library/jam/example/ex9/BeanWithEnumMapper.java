package sk.annotation.library.jam.example.ex9;

//@JamMapper TODO: compile errors in generated Impl class (problem with enum instantiation)
public interface BeanWithEnumMapper {
    UserWithRoleOutput toOutput(UserWithRoleInput input);
}
