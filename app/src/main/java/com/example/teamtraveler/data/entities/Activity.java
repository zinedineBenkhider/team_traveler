package com.example.teamtraveler.data.entities;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Activity {
    private String id;
    private String entitled;
    private String idTrip;
    private int price;
    private String type;
    private String location;
    private String webSite;
    private String idUserOwner;
    private Map<String,String> usersInterested;

    public Activity(String entitled, String idTrip, String type, String location, String webSite, String idUserOwner){
        this.idTrip = idTrip;
        this.type = type;
        this.id= UUID.randomUUID().toString();
        this.entitled = entitled;
        this.location = location;
        this.webSite = webSite;
        this.idUserOwner = idUserOwner;
        usersInterested=new HashMap<>();
    }

    public Activity(){

    }

    public String getEntitled() {
        return entitled;
    }

    public void setEntitled(String entitled) {
        this.entitled = entitled;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getWebSite() {
        return webSite;
    }

    public void setWebSite(String webSite) {
        this.webSite = webSite;
    }

    public String getIdUserOwner() {
        return idUserOwner;
    }

    public void setIdUserOwner(String idUserOwner) {
        this.idUserOwner = idUserOwner;
    }

    public String getId() {
        return id;
    }

    public String getIdTrip() {
        return idTrip;
    }

    public void setIdTrip(String idTrip) {
        this.idTrip = idTrip;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, String> getUsersInterested() {
        return usersInterested;
    }

    public void setUsersInterested(Map<String, String> usersInterested) {
        this.usersInterested = usersInterested;
    }
}
