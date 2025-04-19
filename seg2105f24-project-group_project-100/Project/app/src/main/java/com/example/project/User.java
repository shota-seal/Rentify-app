package com.example.project;

public class User extends Account {
    private String firstName;
    private String lastName;
    private String email;
    private Address address;

    public User(String username, String password, String id, String firstName, String lastName, String email, Address address) {
        super(username,password, id);
        this.firstName=firstName;
        this.lastName=lastName;
        this.email=email;
        this.address=address;
    }

    public User(){} //default constructor

    public static boolean emailTaken(String email) {
        for (Account account : Account.getAccounts()) {
            if (account instanceof User && ((User) account).getEmail().equals(email)) {
                return true;
            }
        }
        return false;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

