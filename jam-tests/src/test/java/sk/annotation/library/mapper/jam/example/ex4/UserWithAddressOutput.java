package sk.annotation.library.mapper.jam.example.ex4;

public class UserWithAddressOutput {
    private String name;
    private String surname;

    private AddressOutput address;

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

    public AddressOutput getAddress() {
        return address;
    }

    public void setAddress(AddressOutput address) {
        this.address = address;
    }
}
