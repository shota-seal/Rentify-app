package com.example.project;

public class Admin extends Account {
    private static Admin admin;

    static {
        admin = new Admin("admin", "XPI76SZUqyCjVxgnUjm0","s1");
        Account.addAccount(admin);
    }

    // Constructor
    private Admin(String username, String password, String id) {
        super(username, password, id);
    }

    public String getRole() {
        return "Admin";
    }
    public static void start() {
    }
}
