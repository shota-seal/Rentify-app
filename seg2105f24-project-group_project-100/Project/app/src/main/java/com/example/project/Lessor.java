package com.example.project;

public class Lessor extends User {
    public Lessor(String username, String password,String id, String firstName, String lastName, String email, Address address) {
        super(username, password, id, firstName, lastName, email, address);
    }

    @Override
    public String getRole() {
        return "Lessor";
    }
}
