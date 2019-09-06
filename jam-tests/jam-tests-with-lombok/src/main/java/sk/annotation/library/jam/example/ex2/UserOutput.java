package sk.annotation.library.jam.example.ex2;

import lombok.Data;

@Data
public class UserOutput {
    public Long id;
    private String firstName;
    private String lastName;

    private UserDetailOutput detail;
}
