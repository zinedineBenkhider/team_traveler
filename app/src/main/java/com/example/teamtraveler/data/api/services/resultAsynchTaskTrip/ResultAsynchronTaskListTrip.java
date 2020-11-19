package com.example.teamtraveler.data.api.services.resultAsynchTaskTrip;

import com.example.teamtraveler.data.entities.Trip;

import java.util.List;

public interface ResultAsynchronTaskListTrip {
    void onResponseReceived(List<Trip> response);
    }

