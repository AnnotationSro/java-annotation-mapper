package sk.annotation.library.jam.example.ex20;

public class RefType1<T> {
    private T num;
    private String name;

    public T getNum() {
        return num;
    }

    public void setNum(T num) {
        this.num = num;
    }

    public String getName() {
        return name;
    }

    public RefType1(){
        this(null, null);
    }
    public RefType1(String name){
        this.name= name;
    }
    public RefType1(T num, String name){
        this.num = num;
        this.name= name;
    }
}
