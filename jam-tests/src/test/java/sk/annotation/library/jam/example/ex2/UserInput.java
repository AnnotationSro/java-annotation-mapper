package sk.annotation.library.jam.example.ex2;

public class UserInput {
    public Long id;
    private String name;
    private String surname;

    private UserDetailInput detail;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public UserDetailInput getDetail() {
        return detail;
    }

    public void setDetail(UserDetailInput detail) {
        this.detail = detail;
    }
}
