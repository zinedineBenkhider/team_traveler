package com.example.teamtraveler.presentation.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teamtraveler.R;
import com.example.teamtraveler.Utils.BaseActivity;
import com.example.teamtraveler.data.api.services.ActivityService;
import com.example.teamtraveler.data.api.services.TripService;
import com.example.teamtraveler.data.api.services.UserService;
import com.example.teamtraveler.data.api.services.resultAsynchTaskActivity.ResultAsynchronTaskOneActivity;
import com.example.teamtraveler.data.api.services.resultAsynchTaskTrip.ResultAsynchronTaskOneTrip;
import com.example.teamtraveler.data.api.services.resultAsynchTaskUser.ResultAsynchronTaskUser;
import com.example.teamtraveler.data.entities.Activity;
import com.example.teamtraveler.data.entities.Trip;
import com.example.teamtraveler.data.entities.User;
import com.example.teamtraveler.presentation.activities.recyclerView.ParticipantAdapter;
import com.example.teamtraveler.presentation.activities.recyclerView.ParticipantViewModel;
import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DetailActivitiesActivity extends BaseActivity {
    public final static String ID_TRIP_DETAIL_ACTIVITY="com.example.teamtraveler.presentation.activities.ID_TRIP_DETAIL_ACTIVITY";
    public final static String ID_ACTIVITY_DETAIL_ACTIVITY="com.example.teamtraveler.presentation.activities.ID_ACTIVITY_DETAIL_ACTIVITY";
    public final static String NB_PARTICPANTS_DETAIL_ACTIVITY="com.example.teamtraveler.presentation.activities.NB_PARTICPANTS_DETAIL_ACTIVITY";
    private TextView entitledTextView;
    private TextView priceTextView;
    private TextView locationTextView;
    private TextView webSiteTextView;
    private TextView typeTextView;
    private String idTrip;
    private String idActivity;
    private String nbParticipants;
    private ActivityService activityService;
    private TripService tripService;
    private UserService userService;
    private Chip chipInterested;
    private Chip chipParticipate;
    private Chip chipNotParticipate;
    private Activity currentActivity=null;
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private LinearLayout linearLayoutContentDetail;
    private LinearLayout linearLayoutProgressBar;
    private TextView  nameUserOwnerOfActivity;
    private ParticipantAdapter participantAdapter;
    private String oldOpinion,currentOpinion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView nameTripTextView;
        RecyclerView recyclerViewPartcipants;
        ImageButton backButton;
        String nameTrip;
        oldOpinion="";
        currentOpinion=oldOpinion;
        setContentView(R.layout.activity_detail_activity);
        nameTripTextView=findViewById(R.id.name_trip_detail_activity);
        entitledTextView=findViewById(R.id.entitled_activity_detail);
        priceTextView=findViewById(R.id.price_activity_detail);
        locationTextView=findViewById(R.id.location_activity_detail);
        webSiteTextView=findViewById(R.id.web_site_activity_detail);
        nameUserOwnerOfActivity=findViewById(R.id.name_user_owner_activity_detail);
        typeTextView=findViewById(R.id.type_activity_detail);
        linearLayoutContentDetail=findViewById(R.id.LinearLayout_content_detail_activity);
        linearLayoutProgressBar=findViewById(R.id.LinearLayout_progress_bar_detail_activity);
        linearLayoutProgressBar.setVisibility(View.VISIBLE);
        linearLayoutContentDetail.setVisibility(View.GONE);

        Toolbar toolbar = findViewById(R.id.list_activity_toolbar);
        setSupportActionBar(toolbar);
        chipInterested=findViewById(R.id.chip_interested);
        chipParticipate=findViewById(R.id.chip_participate);
        chipNotParticipate=findViewById(R.id.chip_not_participate);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        Intent intent=getIntent();
        idTrip=intent.getStringExtra(ID_TRIP_DETAIL_ACTIVITY);
        nameTrip=intent.getStringExtra(ListActivitiesActivity.NAME_TRIP_LIST_ACTIVITY);
        nameTripTextView.setText(nameTrip);
        idActivity=intent.getStringExtra(ID_ACTIVITY_DETAIL_ACTIVITY);
        nbParticipants=intent.getStringExtra(NB_PARTICPANTS_DETAIL_ACTIVITY);
        backButton = findViewById(R.id.backButton_detail_activity);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentOpinion.equals(oldOpinion)){
                    finish();
                }
                else{
                    Intent intent = getIntent();
                    intent.putExtra(ListActivitiesActivity.ID_TRIP_LIST_ACTIVITY,idTrip);
                    intent.putExtra(ListActivitiesActivity.NB_PARTICIPANTS_LIST_ACTIVITY,nbParticipants);
                    setResult(ListActivitiesActivity.RESULT_UPDATE_OPINION_ACTIVITY_LIST,intent);
                    finish();
                }

            }
        });
        activityService=new ActivityService();
        setListnersToChips();
        recyclerViewPartcipants=findViewById(R.id.recycler_view_particpants_detail_activity);
        participantAdapter=new ParticipantAdapter();
        recyclerViewPartcipants.setAdapter(participantAdapter);
        recyclerViewPartcipants.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        setupRecyclerView();
    }


    /**
     * Finish the activity
     */
    @Override
    public void onBackPressed() {
        if (currentOpinion.equals(oldOpinion)){
            finish();
        }
        else{
            Intent intent = getIntent();
            intent.putExtra(ListActivitiesActivity.ID_TRIP_LIST_ACTIVITY,idTrip);
            intent.putExtra(ListActivitiesActivity.NB_PARTICIPANTS_LIST_ACTIVITY,nbParticipants);
            setResult(ListActivitiesActivity.RESULT_UPDATE_OPINION_ACTIVITY_LIST,intent);
            finish();
        }
    }


    /**
     * Setup the recycler view for the chip and call the service
     */
    public void setupRecyclerView(){
        activityService.getActivityByID(idActivity, new ResultAsynchronTaskOneActivity() {
            @Override
            public void onResponseReceived(final Activity activity) {

                participantAdapter.emptyDataSet();
                currentActivity=activity;
                entitledTextView.setText(activity.getEntitled());
                priceTextView.setText(activity.getPrice()+"¢");
                locationTextView.setText(activity.getLocation());
                webSiteTextView.setText(activity.getWebSite());
                typeTextView.setText(activity.getType());
                setBackgroundColorToChip();
                userService=new UserService();
                tripService=new TripService();
                Toolbar toolbar = findViewById(R.id.detail_activity_toolbar);
                setSupportActionBar(toolbar);

                tripService.getTripWithID(idTrip, new ResultAsynchronTaskOneTrip() {
                    @Override
                    public void onResponseReceived(Trip response) {

                        for (int i = 0; i < response.getParticipantsID().size(); i++) {
                            userService.getUserWithID(response.getParticipantsID().get(i), new ResultAsynchronTaskUser() {
                                @Override
                                public void onResponseReceived(User response) {
                                    if (response.getId().equals(activity.getIdUserOwner())){
                                        nameUserOwnerOfActivity.setText(response.getName());
                                    }
                                    String opinion=currentActivity.getUsersInterested().get(response.getId());
                                    if (opinion==null){
                                        opinion=getResources().getString(R.string.without_opinion);
                                        updateOpinionOfUser(response.getId(),opinion);
                                    }
                                    if(!response.getId().equals(firebaseUser.getUid())) {
                                        ParticipantViewModel participantViewModel = new ParticipantViewModel(response.getName(), opinion);
                                        participantAdapter.bindViewModel(participantViewModel);
                                    }
                                    linearLayoutProgressBar.setVisibility(View.GONE);
                                    linearLayoutContentDetail.setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    }

                    @Override
                    public void onAllResponseReceived() {
                        //do nothing when no response
                    }

                    @Override
                    public void onNoResponseReceived() {
                        //do nothing when no response
                    }
                });
            }

            @Override
            public void onNoResponseReceived() {
                //do nothing when no response
            }

            @Override
            public void onAllResponseReceived() {
                //do nothing when no response
            }
        });
    }

    /**
     * Allow the user to select an opinion
     */
    private void setListnersToChips(){
        chipNotParticipate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chipInterested.setChipBackgroundColor(getResources().getColorStateList(R.color.color_chip_without_opinion));
                chipParticipate.setChipBackgroundColor(getResources().getColorStateList(R.color.color_chip_without_opinion));

                String opinion=getResources().getString(R.string.opinion_not_participate);

                if (currentActivity!=null){
                    if(!opinion.equals(currentActivity.getUsersInterested().get(firebaseUser.getUid()))){
                        chipNotParticipate.setChipBackgroundColor(getResources().getColorStateList(R.color.color_chip_not_participate));
                    }
                    else {
                        chipNotParticipate.setChipBackgroundColor(getResources().getColorStateList(R.color.color_chip_without_opinion));
                        opinion=getResources().getString(R.string.without_opinion);
                    }
                    tripService.notifyParticipants( idTrip,firebaseAuth.getCurrentUser().getUid());//notification
                    activityService.addOrUpdateParticipantOpinion(currentActivity.getId(),firebaseUser.getUid(),opinion);
                }
                else {
                    updateOpinionOfUser(firebaseUser.getUid(),opinion);
                }
                currentOpinion=opinion;
                currentActivity.getUsersInterested().put(firebaseUser.getUid(),opinion);
            }
        });

        chipParticipate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chipInterested.setChipBackgroundColor(getResources().getColorStateList(R.color.color_chip_without_opinion));
                chipNotParticipate.setChipBackgroundColor(getResources().getColorStateList(R.color.color_chip_without_opinion));
                String opinion=getResources().getString(R.string.opinion_participate);

                if (currentActivity!=null){
                    if(!opinion.equals(currentActivity.getUsersInterested().get(firebaseUser.getUid()))){
                        chipParticipate.setChipBackgroundColor(getResources().getColorStateList(R.color.color_chip_participate));
                    }
                    else {
                        chipParticipate.setChipBackgroundColor(getResources().getColorStateList(R.color.color_chip_without_opinion));
                        opinion=getResources().getString(R.string.without_opinion);
                    }
                    tripService.notifyParticipants( idTrip,firebaseAuth.getCurrentUser().getUid());//notification
                    activityService.addOrUpdateParticipantOpinion(currentActivity.getId(),firebaseUser.getUid(),opinion);
                }
                else {
                    updateOpinionOfUser(firebaseUser.getUid(),opinion);
                }
                currentOpinion=opinion;
                currentActivity.getUsersInterested().put(firebaseUser.getUid(),opinion);
            }
        });

        chipInterested.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chipParticipate.setChipBackgroundColor(getResources().getColorStateList(R.color.color_chip_without_opinion));
                chipNotParticipate.setChipBackgroundColor(getResources().getColorStateList(R.color.color_chip_without_opinion));
                chipInterested.setChipBackgroundColor(getResources().getColorStateList(R.color.color_chip_interested));
                String opinion=getResources().getString(R.string.opinion_interested);

                if (currentActivity!=null){
                    if(!opinion.equals(currentActivity.getUsersInterested().get(firebaseUser.getUid()))){
                        chipInterested.setChipBackgroundColor(getResources().getColorStateList(R.color.color_chip_interested));
                    }
                    else {
                        chipInterested.setChipBackgroundColor(getResources().getColorStateList(R.color.color_chip_without_opinion));
                        opinion=getResources().getString(R.string.without_opinion);
                    }

                    tripService.notifyParticipants( idTrip,firebaseAuth.getCurrentUser().getUid());//notification
                    activityService.addOrUpdateParticipantOpinion(currentActivity.getId(),firebaseUser.getUid(),opinion);
                }
                else {
                    updateOpinionOfUser(firebaseUser.getUid(),opinion);
                }
                currentOpinion=opinion;
                currentActivity.getUsersInterested().put(firebaseUser.getUid(),opinion);
            }
        });
    }


    /**
     * Give a background color to the chip in fonction of the selected opinion
     */
    private void setBackgroundColorToChip(){
        if(currentActivity.getUsersInterested().get(firebaseUser.getUid())!=null) {
            String opinionOfUser = currentActivity.getUsersInterested().get(firebaseUser.getUid());
            oldOpinion=opinionOfUser;
            currentOpinion=oldOpinion;
            if (opinionOfUser.equals(chipInterested.getText().toString())) {
                chipInterested.setChipBackgroundColor(getResources().getColorStateList(R.color.color_chip_interested));
            } else if (opinionOfUser.equals(chipParticipate.getText().toString())) {
                chipParticipate.setChipBackgroundColor(getResources().getColorStateList(R.color.color_chip_participate));
            } else if (opinionOfUser.equals(chipNotParticipate.getText().toString())) {
                chipNotParticipate.setChipBackgroundColor(getResources().getColorStateList(R.color.color_chip_not_participate));
            }
        }
    }

    /**
     * Notify the service of the user opinion
     * @param userId
     * @param opinion
     */
    private void updateOpinionOfUser(final String userId,final String opinion){
        activityService.getActivityByID(idActivity, new ResultAsynchronTaskOneActivity() {
            @Override
            public void onResponseReceived(Activity response) {
                activityService.addOrUpdateParticipantOpinion(response.getId(),userId,opinion);
                tripService.notifyParticipants( idTrip,firebaseUser.getUid());//notification
            }

            @Override
            public void onNoResponseReceived() {
                //do nothing when no response
            }

            @Override
            public void onAllResponseReceived() {
                //do nothing when no response
            }
        });
    }



}
