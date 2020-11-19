package com.example.teamtraveler.presentation.activities.recyclerView;

import com.example.teamtraveler.data.api.services.resultAsynchTaskUser.ResultAsynchronTaskUser;
import com.example.teamtraveler.data.api.services.resultAsynchTaskUser.ResultAsynchronTaskUserName;
import com.example.teamtraveler.data.entities.Activity;

public interface ActivityActionInterface {
    void showTopProgressBar();
    void hideTopProgressBar();
    void showButtomProgressBar();
    void hideButtomProgressBar();
    void activityClick(Activity activity);
    void getParticipants(final String idOwnerOfActivity,final ResultAsynchronTaskUserName resultAsynchronTaskUserName);
    void changeMarginButtomOfRecyclerView(int i);
}
