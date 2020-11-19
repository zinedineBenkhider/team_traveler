package com.example.teamtraveler.data.entities;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Housing {
    private String id;
    private String name;
    private int price;
    private String nbRoom;
    private String nbBathRoom;
    private String nbBed;
    private String description;
    private String idTrip;
    private float averageNote;
    private String link;
    private boolean haveSwimmingPool;
    private boolean haveGarage;
    private boolean haveGardin;
    private boolean haveWifi;
    private boolean haveClimatisation;
    private boolean haveChicken;

    private String idUserOwner;
    private Map<String,Float> usersNotes;
    public Housing(String idUserOwner, String name, String nbRoom, String nbBathRoom, String description, String idTrip, boolean haveSwimmingPool, boolean haveGarage, boolean haveGardin, boolean haveWifi) {
        this.name=name;
        this.nbRoom=nbRoom;
        this.nbBathRoom=nbBathRoom;
        this.description=description;
        this.idTrip=idTrip;
        this.haveSwimmingPool = haveSwimmingPool;
        this.haveGarage = haveGarage;
        this.haveGardin = haveGardin;
        this.haveWifi = haveWifi;
        this.id= UUID.randomUUID().toString();
        usersNotes=new HashMap<>();
        this.averageNote=0;
        this.idUserOwner=idUserOwner;
    }
    public Housing(String name, String nbRoom, String nbBathRoom, String description, boolean haveSwimmingPool, boolean haveGarage, boolean haveGardin, boolean haveWifi) {
        this.name=name;
        this.nbRoom=nbRoom;
        this.nbBathRoom=nbBathRoom;
        this.description=description;
        this.haveSwimmingPool = haveSwimmingPool;
        this.haveGarage = haveGarage;
        this.haveGardin = haveGardin;
        this.haveWifi = haveWifi;
        this.averageNote=0;
    }
    public Housing(){

    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNbRoom() {
        return nbRoom;
    }

    public void setNbRoom(String nbRoom) {
        this.nbRoom = nbRoom;
    }

    public String getNbBathRoom() {
        return nbBathRoom;
    }

    public void setNbBathRoom(String nbBathRoom) {
        this.nbBathRoom = nbBathRoom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getIdTrip() {
        return idTrip;
    }

    public void setIdTrip(String idTrip) {
        this.idTrip = idTrip;
    }

    public float getAverageNote() {
        return averageNote;
    }

    public void setAverageNote(float averageNote) {
        this.averageNote = averageNote;
    }

    public Map<String, Float> getUsersNotes() {
        return usersNotes;
    }

    public void setUsersNotes(Map<String, Float> usersNotes) {
        this.usersNotes = usersNotes;
    }

    public boolean isHaveSwimmingPool() {
        return haveSwimmingPool;
    }

    public void setHaveSwimmingPool(boolean haveSwimmingPool) {
        this.haveSwimmingPool = haveSwimmingPool;
    }

    public boolean isHaveGarage() {
        return haveGarage;
    }

    public void setHaveGarage(boolean haveGarage) {
        this.haveGarage = haveGarage;
    }

    public boolean isHaveGardin() {
        return haveGardin;
    }

    public void setHaveGardin(boolean haveGardin) {
        this.haveGardin = haveGardin;
    }


    public boolean isHaveWifi() {
        return haveWifi;
    }

    public void setHaveWifi(boolean haveWifi) {
        this.haveWifi = haveWifi;
    }

    public String getIdUserOwner() {
        return idUserOwner;
    }

    public void setIdUserOwner(String idUserOwner) {
        this.idUserOwner = idUserOwner;
    }

    public boolean isHaveClimatisation() {
        return haveClimatisation;
    }

    public void setHaveClimatisation(boolean haveClimatisation) {
        this.haveClimatisation = haveClimatisation;
    }

    public boolean isHaveChicken() {
        return haveChicken;
    }

    public void setHaveChicken(boolean haveChicken) {
        this.haveChicken = haveChicken;
    }

    public String getNbBed() {
        return nbBed;
    }

    public void setNbBed(String nbBed) {
        this.nbBed = nbBed;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

}
