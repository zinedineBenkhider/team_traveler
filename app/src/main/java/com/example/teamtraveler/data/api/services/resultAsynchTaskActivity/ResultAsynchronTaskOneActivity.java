package com.example.teamtraveler.data.api.services.resultAsynchTaskActivity;

import com.example.teamtraveler.data.entities.Activity;

public interface ResultAsynchronTaskOneActivity {
    void onResponseReceived(Activity response);
    void onNoResponseReceived();

    void onAllResponseReceived();
}
