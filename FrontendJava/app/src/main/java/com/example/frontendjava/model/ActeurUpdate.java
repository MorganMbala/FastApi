package com.example.frontendjava.model;

public class ActeurUpdate {
    private String name;
    private String bio;
    private String picture;

    public ActeurUpdate(String name, String bio, String picture) {
        this.name = name;
        this.bio = bio;
        this.picture = picture;
    }

    public String getName() { return name; }
    public String getBio() { return bio; }
    public String getPicture() { return picture; }

    public void setName(String name) { this.name = name; }
    public void setBio(String bio) { this.bio = bio; }
    public void setPicture(String picture) { this.picture = picture; }
}

