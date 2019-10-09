package sk.annotation.library.jam.example.ex2;

import lombok.Data;

@Data
public class UserOutput {
    public Long id;
    public Long id2;
    private String firstName;
    private String lastName;

    private UserDetailOutput detail;
}
