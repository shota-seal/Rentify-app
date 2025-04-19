package com.example.project;

import java.io.Serializable;

public class Categorie implements Serializable {
    private String _id;
    private String _categoriename;
    private String _description;

    public Categorie() {
    }

    public Categorie(String id, String categoriename, String description) {
        _id = id;
        _categoriename = categoriename;
        _description = description;
    }

    public Categorie(String categoriename, String description) {
        _categoriename = categoriename;
        _description = description;
    }

    public void setId(String id) {
        _id = id;
    }

    public String getId() {
        return _id;
    }

    public void setCategorieName(String categoriename) {
        _categoriename = categoriename;
    }

    public String getCategorieName() {
        return _categoriename;
    }

    public void setDescription(String description) {
        _description = description;
    }

    public String getDescription() {
        return _description;
    }

    @Override
    public String toString() {
        return _categoriename; // Ensure the category name is displayed
    }
}
