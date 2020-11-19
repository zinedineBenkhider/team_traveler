package com.example.teamtraveler.data.api.services.resultAsynchTaskUser;

import com.example.teamtraveler.data.entities.User;

public interface ResultAsynchronTaskUser {
    void onResponseReceived(User response);
}
