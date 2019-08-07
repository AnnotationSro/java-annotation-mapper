package sk.annotation.library.jam.example.ex2;

public class UserOutput {
    public Long id;
    private String firstName;
    private String lastName;

    private UserDetailOutput detail;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public UserDetailOutput getDetail() {
        return detail;
    }

    public void setDetail(UserDetailOutput detail) {
        this.detail = detail;
    }
}
