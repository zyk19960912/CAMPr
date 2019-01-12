package com.example.a1.campr.models;

import java.util.ArrayList;
import java.util.HashMap;

public class Lister {
    private String id;
    private String firstname;
    private String lastname;
    private String email;
    private String phoneNumber;
    private String city;
    private String state;
    private String picUrl;

    public Lister() {}

    public Lister(String firstname, String lastname, String email, String phoneNumber, String city, String state, String picUrl) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.city = city;
        this.state = state;
        this.picUrl = picUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public void setListedPets(HashMap<String, Boolean> listedPets) {
        this.listedPets = listedPets;
    }

    private HashMap<String, Boolean> listedPets;
}
