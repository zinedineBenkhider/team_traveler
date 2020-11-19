package com.example.teamtraveler.data.api.services.resultAsynchTaskTrip;

import com.example.teamtraveler.data.entities.Trip;


public interface ResultAsynchronTaskOneTrip  {
    void onResponseReceived(Trip response);
    void onAllResponseReceived();
    void onNoResponseReceived();
}
