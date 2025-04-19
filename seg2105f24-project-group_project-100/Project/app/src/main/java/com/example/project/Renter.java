package com.example.project;

public class Renter extends User {
    public Renter(String username, String password,String id, String firstName, String lastName, String email, Address address) {
        super(username, password,id, firstName, lastName, email, address);
    }
    @Override
    public String getRole() {
        return "Renter";
    }
}
