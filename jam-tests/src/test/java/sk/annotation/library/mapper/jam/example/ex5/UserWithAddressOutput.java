package sk.annotation.library.mapper.jam.example.ex5;

import sk.annotation.library.mapper.jam.example.ex4.AddressOutput;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class UserWithAddressOutput {
    private String name;
    private String surname;

    private List<AddressOutput> addresses;

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

    public List<AddressOutput> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<AddressOutput> addresses) {
        this.addresses = addresses;
    }
}
