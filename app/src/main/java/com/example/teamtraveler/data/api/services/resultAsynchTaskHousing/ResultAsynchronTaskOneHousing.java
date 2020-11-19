package com.example.teamtraveler.data.api.services.resultAsynchTaskHousing;

import com.example.teamtraveler.data.entities.Housing;
import com.example.teamtraveler.data.entities.Trip;


public interface ResultAsynchronTaskOneHousing {
    void onResponseReceived(Housing response);
    void onNoResponseReceived();
}
