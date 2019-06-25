package sk.annotation.library.mapper.jam.example.ex4;

public class UserWithAddressInput {
    private String name;
    private String surname;

    private AddressInput address;

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

    public AddressInput getAddress() {
        return address;
    }

    public void setAddress(AddressInput address) {
        this.address = address;
    }
}
