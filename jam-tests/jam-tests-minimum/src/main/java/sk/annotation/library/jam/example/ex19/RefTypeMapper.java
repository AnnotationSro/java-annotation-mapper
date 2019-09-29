package sk.annotation.library.jam.example.ex19;

import sk.annotation.library.jam.annotations.Mapper;

@Mapper
public abstract class RefTypeMapper {
    abstract public RefType1b<String, String> to(SubType1aLong tst);

    public boolean used_test_interceptor1 = false;
    protected void test_interceptor1(SubType1aLong from, RefType1b<String, String> to) {
        used_test_interceptor1 = true;
    }

    public boolean used_test_interceptor2 = false;
    protected void test_interceptor2(SubType1aLong from, RefType1b<String, Object> to) {
        used_test_interceptor2 = true;
    }

    public boolean used_test_interceptor3 = false;
    protected void test_interceptor3(RefType1a<Long> from, RefType1b<String, String> to) {
        used_test_interceptor3 = true;
    }
}
