package com.example.a1.campr.models;

public class Application {
    private String applicationId;
    private String listerId;
    private String adopterId;
    private String petId;
    private boolean approval;
    private boolean rejection;
//    private String message;

    public Application() {}

    public Application(String listerId, String adopterId, String petId) {
        this.listerId = listerId;
        this.adopterId = adopterId;
        this.petId = petId;
        this.rejection = false;
        this.approval = false;
//        this.message = message;
    }
//
//    public String getMessage() {
//        return message;
//    }
//
//    public void setMessage(String message) {
//        this.message = message;
//    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getListerId() {
        return listerId;
    }

    public void setListerId(String listerId) {
        this.listerId = listerId;
    }

    public String getAdopterId() {
        return adopterId;
    }

    public void setAdopterId(String adopterId) {
        this.adopterId = adopterId;
    }

    public String getPetId() {
        return petId;
    }

    public void setPetId(String petId) {
        this.petId = petId;
    }

    public boolean isApproval() {
        return approval;
    }

    public void setApproval(boolean approval) {
        this.approval = approval;
    }

    public boolean isRejection() {
        return rejection;
    }

    public void setRejection(boolean rejection) {
        this.rejection = rejection;
    }
}

