package com.example.project;

import java.util.ArrayList;
import java.util.List;

public class Account {
    private static List<Account> accounts = new ArrayList<>();

    private boolean enabled;
    private String username;
    private String password;
    private String id;

    //default constructor needed for the database
    public Account(){}

    public Account(String username, String password, String id) {
        this.username = username;
        this.password = password;
        this.id = id;
        enabled = true;
    }

    public static void addAccount(Account account) {
        accounts.add(account);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static List<Account> getAccounts() {
        return accounts;
    }

    public static Account findAccount(String username, String password) {
        for (Account account : accounts) {
            if (account.getUsername().equals(username) && account.getPassword().equals(password)) {
                return account;
            }
        }
        return null;
    }

    public static boolean usernameTaken(String username) {
        for (Account account : accounts) {
            if (account.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public String getRole() {
        return "General Account";
    }

    public String getID() {
        return id;
    }

    public boolean getEnabled(){
        return enabled;
    }

    public void setEnable(boolean enable) {enabled = enable;}

}