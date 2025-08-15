package com.example.frontendjava.model;

public class Acteur {
    private int id;
    private String name;
    private String bio;
    private String picture;

    // Default constructor
    public Acteur() {}

    // Full-args constructor
    public Acteur(int id, String name, String bio, String picture) {
        this.id = id;
        this.name = name;
        this.bio = bio;
        this.picture = picture;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getBio() { return bio; }
    public String getPicture() { return picture; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setBio(String bio) { this.bio = bio; }
    public void setPicture(String picture) { this.picture = picture; }
}
