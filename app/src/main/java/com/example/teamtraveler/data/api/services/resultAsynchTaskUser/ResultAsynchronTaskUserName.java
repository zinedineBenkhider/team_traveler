package com.example.teamtraveler.data.api.services.resultAsynchTaskUser;

import com.example.teamtraveler.data.entities.User;

public interface ResultAsynchronTaskUserName {
     void onResonseReceived(User user);
    void onResonseReceivedNameOwnerOfActivity(String name);
    void onResonseReceivedAllUsers();
}
