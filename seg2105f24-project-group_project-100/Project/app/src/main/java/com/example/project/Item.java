package com.example.project;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Item implements Serializable {
    private String id;
    private String name;
    private float fee;
    private String timePeriod;
    private String description;
    private String databaseUserID;
    private String lessorName;
    private Categorie category;
    private Boolean isAvailable;
    private List<String> interestedUsers;
    private String onLoanTo;

    // Default constructor (required for Firebase)
    public Item() {
    }

    // Constructor
    public Item(String id, String name, float fee, String timePeriod, String description, String lessorName, String databaseUserID, Categorie category) {
        this.id = id;
        this.name = name;
        this.fee = fee;
        this.timePeriod = timePeriod;
        this.description = description;
        this.lessorName = lessorName;
        this.databaseUserID = databaseUserID;
        this.category = category;
        this.isAvailable = true;
        this.onLoanTo = null;
    }


    // Getters and setters for all fields
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getFee() {
        return fee;
    }

    public void setFee(float fee) {
        this.fee = fee;
    }

    public String getTimePeriod() {
        return timePeriod;
    }

    public void setTimePeriod(String timePeriod) {
        this.timePeriod = timePeriod;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDatabaseUserID() {
        return databaseUserID;
    }

    public void setDatabaseUserID(String databaseUserID) {
        this.databaseUserID = databaseUserID;
    }

    public Categorie getCategory() {
        return category;
    }

    public void setCategory(Categorie category) {
        this.category = category;
    }

    public Boolean getAvailable() {
        return isAvailable;
    }

    public void setAvailable(Boolean available) {
        isAvailable = available;
    }

    public String getLessorName() {
        return lessorName;
    }

    public void setLessorName(String lessorName) {
        this.lessorName = lessorName;
    }

    public String getOnLoanTo() {
        return onLoanTo;
    }

    public void setOnLoanTo(String onLoanTo) {
        this.onLoanTo = onLoanTo;
    }

    public List<String> getInterestedUsers() {
        return interestedUsers;
    }

    public void setInterestedUsers(List<String> interestedUsers) {
        this.interestedUsers = interestedUsers;
    }

    public void addInterestedUser(String username) {
        if (interestedUsers == null) {
            interestedUsers = new ArrayList<>(); // Initialize the list if it's null
        }
        if (!interestedUsers.contains(username)) {
            interestedUsers.add(username);
        }
    }

    public void removeInterestedUser(String username) {
        this.interestedUsers.remove(username);
    }

    public int getNumberInterestedUsers(){
        if(interestedUsers == null){
            return 0;
        }
        return interestedUsers.size();
    }
}
