package com.example.teamtraveler.data.api.services.resultAsynchTaskHousing;

import com.example.teamtraveler.data.entities.Housing;
import com.example.teamtraveler.data.entities.Trip;

import java.util.List;

public interface ResultAsynchronTaskListHousing {
    void onResponseReceived(List<Housing> response);
    }

