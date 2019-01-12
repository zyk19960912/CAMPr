package com.example.a1.campr.models;

import java.util.ArrayList;
import java.util.HashMap;

public class Adopter {
    private String id;
    private String firstname;
    private String lastname;
    private String email;
    private String phoneNumber;
    private String city;
    private String state;
    private String picUrl;
    private HashMap<String, Boolean> chosenPets;
    private HashMap<String, Boolean> applications;

    public Adopter() {}

    public Adopter(String firstname, String lastname, String email, String phoneNumber, String city, String state, String picUrl, String id) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.city = city;
        this.state = state;
        this.picUrl = picUrl;
        this.id = id;
    }

    public HashMap<String, Boolean> getApplications() {
        return applications;
    }

    public void setApplications(HashMap<String, Boolean> applications) {
        this.applications = applications;
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

    public HashMap<String, Boolean> getChosenPets() {
        return chosenPets;
    }

    public void setChosenPets(HashMap<String, Boolean> chosenPets) {
        this.chosenPets = chosenPets;
    }

}
