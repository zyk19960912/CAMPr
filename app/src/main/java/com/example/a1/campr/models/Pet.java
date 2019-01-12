package com.example.a1.campr.models;

import android.text.BoringLayout;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Pet {
    private List<String> feeRanges;
    private String name;
    private String gender;
    private String info;
    private String id;
    private String picUrl;
    private String listerId;
    private String species;
    private String age;
    private String color;
    private String size;
    private Integer fee;
    private String feeRange;
    private String city;
    private int numOfApplicants;
    private HashMap<String, Boolean> possibleAdopters;
    private HashMap<String, Boolean> impossibleAdopters;
    public Pet() {}
    public Pet(String name, String gender, String info, String id, String picUrl, String listerId,String species, String age,String color,String size,Integer fee, String city) {
        this.name = name;
        this.gender = gender;
        this.info = info;
        this.id = id;
        this.picUrl = picUrl;
        this.listerId = listerId;
        this.species = species;
        this.age = age;
        this.color = color;
        this.size = size;
        this.fee = fee;
        if(0  <= fee && fee < 50){
            this.feeRange = "Below $50";
        }
        else if(fee >= 50 && fee <100){
            this.feeRange = "$50 to $100";
        }
        else{
            this.feeRange = "Above $100";
        }
        this.city = city;
        this.numOfApplicants = 0;
    }

    public int getNumOfApplicants() {
        return numOfApplicants;
    }

    public void setNumOfApplicants(int numOfApplicants) {
        this.numOfApplicants = numOfApplicants;
    }
    
    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Integer getFee() {
        return fee;
    }

    public void setFee(Integer fee) {
        this.fee = fee;
    }

    public String getFeeRange() {
        return feeRange;
    }

    public void setFeeRange(String feeRange) {
        this.feeRange = feeRange;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) { this.name = name; }

    public String getGender() {
        return gender;
    }
    public void setGender(String gender) { this.gender = gender; }

    public String getInfo() {
        return info;
    }
    public void setInfo(String info) { this.info = info; }

    public String getId() {
        return id;
    }
    public void setId(String id) { this.id = id; }

    public String getPicUrl() {
        return picUrl;
    }
    public void setPicUrl(String picUrl) { this.picUrl = picUrl; }

    public String getListerId() {
        return listerId;
    }
    public void setListerId(String listerId) { this.listerId = listerId; }

    public HashMap<String, Boolean> getPossibleAdopters() {
        return possibleAdopters;
    }

    public void setPossibleAdopters(HashMap<String, Boolean> possibleAdopters) {
        this.possibleAdopters = possibleAdopters;
    }

    public HashMap<String, Boolean> getImpossibleAdopters() {
        return impossibleAdopters;
    }

    public void setImpossibleAdopters(HashMap<String, Boolean> impossibleAdopters) {
        this.impossibleAdopters = impossibleAdopters;
    }

    public List<String> getFeeRanges() {
        return feeRanges;
    }

    public void setFeeRanges(List<String> feeRanges) {
        this.feeRanges = feeRanges;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
