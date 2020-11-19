package com.example.teamtraveler.presentation.housing;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.example.teamtraveler.R;
import com.example.teamtraveler.Utils.BaseActivity;
import com.example.teamtraveler.Utils.NoteAppreciationMapping;
import com.example.teamtraveler.data.api.services.HousingService;
import com.example.teamtraveler.data.api.services.TripService;
import com.example.teamtraveler.data.api.services.resultAsynchTaskHousing.ResultAsynchronTaskOneHousing;
import com.example.teamtraveler.data.entities.Housing;
import com.google.firebase.auth.FirebaseAuth;

public class DetailHousingActivity extends BaseActivity {
    public final static String ID_HOUSING_DETAIL_HOUSING="com.example.teamtraveler.presentation.housing.DetailHousingActivity.ID_HOUSING_DETAIL_HOUSING";
    public static final String ID_TRIP_DETAIL_HOUSING = "com.example.teamtraveler.presentation.housing.DetailHousingActivity.ID_TRIP_DETAIL_HOUSING";
    private TextView nameTextView,priceTextView,nbRoomTextView,nbBathRoomTextView,descriptionTextView,averageTextView,noteTextView, nbBedTextView,linkTextView;
    private CheckBox wifiCheckBox,garageCheckBox,gardinCheckBox,swimmingPoolCheckBox,climatisationCheckBox,chickenCheckBox;
    private HousingService housingService;
    private FirebaseAuth firebaseAuth;
    private TripService tripService;
    private RatingBar ratingBarAverage,ratingBarNote;
    private ImageButton backButton;
    private LinearLayout linearLayoutProgressBar,linearLayoutDetail;
    private double oldNote,currentNote;
    private TextView nameTripTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_housing_detail);
        Intent intent=getIntent();
        final String housingId=intent.getStringExtra(ID_HOUSING_DETAIL_HOUSING);
        final String idTrip=intent.getStringExtra(ID_TRIP_DETAIL_HOUSING);
        final String nameTrip=intent.getStringExtra(ListHousingActivity.NAME_TRIP_LIST_HOUSING);

        housingService=new HousingService();
        tripService=new TripService();
        firebaseAuth=FirebaseAuth.getInstance();

        linearLayoutProgressBar=findViewById(R.id.LinearLayout_progress_bar_detail_housing);
        linearLayoutProgressBar.setVisibility(View.VISIBLE);

        linearLayoutDetail=findViewById(R.id.LinearLayoutDetailsHousing);
        linearLayoutDetail.setVisibility(View.GONE);
        nameTripTextView=findViewById(R.id.name_trip_detail_housing);
        nameTripTextView.setText(nameTrip);
        ratingBarAverage=findViewById(R.id.rating_bar_average_housing_detail);
        ratingBarNote=findViewById(R.id.rating_bar_note_housing_detail);
        ratingBarAverage.setEnabled(false);

        nameTextView=findViewById(R.id.name_housing_detail);
        priceTextView=findViewById(R.id.price_housing_detail);
        nbBathRoomTextView=findViewById(R.id.nb_bathroom_housing_detail);
        nbRoomTextView=findViewById(R.id.nb_room_housing_detail);
        descriptionTextView=findViewById(R.id.description_housing_detail);
        averageTextView=findViewById(R.id.average_housing_detail);
        noteTextView=findViewById(R.id.note_housing_detail);
        nbBedTextView=findViewById(R.id.nb_bed_housing_detail);
        linkTextView=findViewById(R.id.link_form_detail_housing);
        linkTextView.setTextIsSelectable(true);
        wifiCheckBox=findViewById(R.id.checkbox_wifi_detail_housing);
        garageCheckBox=findViewById(R.id.checkbox_garage_detail_housing);
        gardinCheckBox=findViewById(R.id.checkbox_gardin_detail_housing);
        swimmingPoolCheckBox=findViewById(R.id.checkbox_swimming_pool_detail_housing);
        climatisationCheckBox=findViewById(R.id.checkbox_climatisation_detail_housing);
        chickenCheckBox=findViewById(R.id.checkbox_chicken_detail_housing);

        Toolbar toolbar = findViewById(R.id.detail_housing_toolbar);
        setSupportActionBar(toolbar);
        backButton = findViewById(R.id.backButton_housing_detail);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentNote==oldNote){
                    finish();
                }
                else {
                    Intent intent = getIntent();
                    setResult(Activity.RESULT_OK,intent);
                    finish();
                }

            }
        });

        housingService.getHousingWithID(housingId, new ResultAsynchronTaskOneHousing() {
            @Override
            public void onResponseReceived(Housing housing) {
                if(!TextUtils.isEmpty(housing.getName())) {
                    nameTextView.setText(housing.getName());
                }

                priceTextView.setText(String.valueOf(housing.getPrice()));

                if (!TextUtils.isEmpty(housing.getNbBathRoom())){
                    nbBathRoomTextView.setText(housing.getNbBathRoom());
                }

                if (!TextUtils.isEmpty(housing.getNbRoom())) {
                    nbRoomTextView.setText(housing.getNbRoom());
                }

                if (!TextUtils.isEmpty(housing.getDescription())) {
                    descriptionTextView.setText(housing.getDescription());
                }
                if(!TextUtils.isEmpty(housing.getNbBed())){
                    nbBedTextView.setText(housing.getNbBed());
                }
                linkTextView.setText(housing.getLink());

                try {
                    float noteOfUser=housing.getUsersNotes().get(firebaseAuth.getCurrentUser().getUid());
                    oldNote=noteOfUser;
                    currentNote=oldNote;
                    ratingBarNote.setRating(noteOfUser);
                    noteTextView.setText("Note: "+noteOfUser+"/5 - "+ NoteAppreciationMapping.getAppreciationOfNote(noteOfUser));

                }
                catch (Exception e){
                    noteTextView.setText(getResources().getString(R.string.note_housing_not_specified));
                }
                wifiCheckBox.setEnabled(true);
                wifiCheckBox.setChecked(housing.isHaveWifi());
                wifiCheckBox.setEnabled(false);
                garageCheckBox.setEnabled(true);
                garageCheckBox.setChecked(housing.isHaveGarage());
                garageCheckBox.setEnabled(false);
                gardinCheckBox.setEnabled(true);
                gardinCheckBox.setChecked(housing.isHaveGardin());
                gardinCheckBox.setEnabled(false);
                swimmingPoolCheckBox.setEnabled(true);
                swimmingPoolCheckBox.setChecked(housing.isHaveSwimmingPool());
                swimmingPoolCheckBox.setEnabled(false);
                climatisationCheckBox.setEnabled(true);
                climatisationCheckBox.setChecked(housing.isHaveClimatisation());
                climatisationCheckBox.setEnabled(false);
                chickenCheckBox.setEnabled(true);
                chickenCheckBox.setChecked(housing.isHaveClimatisation());
                chickenCheckBox.setEnabled(false);

                ratingBarAverage.setRating(housing.getAverageNote());
                if(housing.getUsersNotes().size()==0){
                    averageTextView.setText("Moyenne: "+housing.getAverageNote()+"/5 sur "+housing.getUsersNotes().size() +" note(s)");
                }
                else{
                    averageTextView.setText("Moyenne: "+housing.getAverageNote()+"/5 sur "+housing.getUsersNotes().size() +" note(s) - "+NoteAppreciationMapping.getAppreciationOfNote(housing.getAverageNote()));
                }

                linearLayoutProgressBar.setVisibility(View.GONE);
                linearLayoutDetail.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNoResponseReceived() {

            }
        });

        ratingBarNote.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(final RatingBar ratingBar, float v, boolean b) {
                currentNote=ratingBarNote.getRating();
                housingService.updateNoteOfHousing(housingId, firebaseAuth.getCurrentUser().getUid(), ratingBar.getRating(), new ResultAsynchronTaskOneHousing() {
                    @Override
                    public void onResponseReceived(Housing housing) {
                        ratingBarAverage.setEnabled(true);
                        ratingBarAverage.setRating(housing.getAverageNote());
                        ratingBarAverage.setEnabled(false);
                        tripService.notifyParticipants( idTrip,firebaseAuth.getCurrentUser().getUid());//notification
                        noteTextView.setText("Note: "+ratingBar.getRating()+"/5 - "+ NoteAppreciationMapping.getAppreciationOfNote(ratingBar.getRating()));
                        averageTextView.setText("Moyenne: "+housing.getAverageNote()+"/5 sur "+housing.getUsersNotes().size() +" note(s) - "+NoteAppreciationMapping.getAppreciationOfNote(housing.getAverageNote()));
                    }

                    @Override
                    public void onNoResponseReceived() {

                    }
                });
            }
        });
    }
    @Override
    public void onBackPressed() {
        if (currentNote==oldNote){
            finish();
        }
        else {
            Intent intent = getIntent();
            setResult(Activity.RESULT_OK,intent);
            finish();
        }
    }


}
