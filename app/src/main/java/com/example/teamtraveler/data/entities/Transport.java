package com.example.teamtraveler.data.entities;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Transport {
    private String name;
    private String departure;
    private String reach;
    private Date departureDate;
    private Date timeDeparture;
    private Date timeReach;
    private String idTrip;
    private String id;
    private String type;
    private String idUserOwner;
    private Map<String,String> usersParticipations;


    public Transport(String name, String departure, String reach, Date departureDate, Date timeDeparture, Date timeReach, String idTrip, String type, String idUserOwner) {
        this.name = name;
        this.departure = departure;
        this.reach = reach;
        this.departureDate = departureDate;
        this.timeDeparture = timeDeparture;
        this.timeReach = timeReach;
        this.type = type;
        this.idUserOwner = idUserOwner;
        this.id = UUID.randomUUID().toString();
        this.idTrip=idTrip;
        this.usersParticipations=new HashMap<>();
    }

    public Transport(){

    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDeparture() {
        return departure;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public String getReach() {
        return reach;
    }

    public void setReach(String reach) {
        this.reach = reach;
    }

    public Date getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(Date departureDate) {
        this.departureDate = departureDate;
    }

    public String getIdTrip() {
        return idTrip;
    }

    public void setIdTrip(String idTrip) {
        this.idTrip = idTrip;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, String> getUsersParticipations() {
        return usersParticipations;
    }

    public void setUsersParticipations(Map<String, String> usersParticipations) {
        this.usersParticipations = usersParticipations;
    }

    public Date getTimeDeparture() {
        return timeDeparture;
    }

    public void setTimeDeparture(Date timeDeparture) {
        this.timeDeparture = timeDeparture;
    }

    public Date getTimeReach() {
        return timeReach;
    }

    public void setTimeReach(Date timeReach) {
        this.timeReach = timeReach;
    }

    public String getIdUserOwner() {
        return idUserOwner;
    }

    public void setIdUserOwner(String idUserOwner) {
        this.idUserOwner = idUserOwner;
    }
}
