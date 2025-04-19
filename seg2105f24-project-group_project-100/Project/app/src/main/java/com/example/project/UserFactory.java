package com.example.project;

public class UserFactory {
    public static User createUser(User user,String role){
        if (role.equals("Renter")){
            String username = user.getUsername();
            String password = user.getPassword();
            String id = user.getID();
            String firstName = user.getFirstName();
            String lastName = user.getLastName();
            String email = user.getEmail();
            Address address = user.getAddress();
            return new Renter(username, password,id,firstName,lastName, email, address);
        }
        else{
            String username = user.getUsername();
            String password = user.getPassword();
            String id = user.getID();
            String firstName = user.getFirstName();
            String lastName = user.getLastName();
            String email = user.getEmail();
            Address address = user.getAddress();
            return new Lessor(username, password,id,firstName,lastName, email, address);
        }
    }
}
