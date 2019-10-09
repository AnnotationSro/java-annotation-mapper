package sk.annotation.library.jam.example.ex2;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserInput {
	@Getter @Setter
    public Long id;
    public Long id2;
    private String name;
    private String surname;

    private UserDetailInput detail;
}
