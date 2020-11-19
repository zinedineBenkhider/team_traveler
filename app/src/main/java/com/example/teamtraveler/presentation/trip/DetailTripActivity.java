package com.example.teamtraveler.presentation.trip;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teamtraveler.R;
import com.example.teamtraveler.Utils.LockScreenActivity;
import com.example.teamtraveler.Utils.ManipulateDate;
import com.example.teamtraveler.data.api.services.ActivityService;
import com.example.teamtraveler.data.api.services.HousingService;
import com.example.teamtraveler.data.api.services.UserService;
import com.example.teamtraveler.data.api.services.resultAsynchTaskHousing.ResultAsynchronTaskOneHousing;
import com.example.teamtraveler.data.api.services.resultAsynchTaskTrip.ResultAsynchronTaskOneTrip;
import com.example.teamtraveler.data.api.services.TripService;
import com.example.teamtraveler.data.api.services.resultAsynchTaskUser.ResultAsynchronTaskUser;
import com.example.teamtraveler.data.entities.Housing;
import com.example.teamtraveler.data.entities.Trip;
import com.example.teamtraveler.data.entities.User;
import com.example.teamtraveler.presentation.activities.ListActivitiesActivity;
import com.example.teamtraveler.presentation.activities.recyclerView.ParticipantAdapter;
import com.example.teamtraveler.presentation.activities.recyclerView.ParticipantViewModel;
import com.example.teamtraveler.presentation.housing.DetailHousingActivity;
import com.example.teamtraveler.presentation.housing.EditHousingActivity;
import com.example.teamtraveler.presentation.housing.ListHousingActivity;
import com.example.teamtraveler.presentation.housing.recyclerView.HousingActionInterface;
import com.example.teamtraveler.presentation.housing.recyclerView.HousingAdapter;
import com.example.teamtraveler.presentation.login.LoginActivity;
import com.example.teamtraveler.presentation.profile.ProfileActivity;
import com.example.teamtraveler.presentation.transport.ListTransportActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DetailTripActivity extends LockScreenActivity implements HousingActionInterface{
    public final static String ID_TRIP_DETAIL="com.example.teamtraveler.presentation.displayTrip.DetailTripActivity.ID_TRIP_DETAIL";
    public final static String FROM_AIR_BNB="com.example.teamtraveler.presentation.displayTrip.DetailTripActivity.FROM_AIR_BNB";
    private FirebaseAuth firebaseAuth;
    private TextView tripNameTxtView,tripPlaceTxtView,tripDateTxtView,nbParticipantsTextView;
    private Button btnHousing,btnActivity,btnTransport;
    private String idTrip;
    private RecyclerView recyclerView;
    private int nbParticipantsCharged;
    private LinearLayout viewContener;
    private LinearLayout progressBarContener;
    private HousingService housingService;
    private ImageButton backButton,btnShowMoreHousings,btnEditTrip;
    private RecyclerView recyclerViewHousings;
    private int nbPartcipantsOfTrip;
    private String nameTrip;
    private HousingAdapter housingAdapter;
    private TripService tripService;
    private ActivityService activityService;
    private UserService userService;
    private static final int REQUEST_CODE_EDIT_TRIP = 2;
    private ParticipantAdapter participantAdapter;
    private Boolean refreshData=false;
    private Uri data=null;
    private Boolean fromAirBnb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_trip);
        firebaseAuth=FirebaseAuth.getInstance();
        Toolbar toolbar = findViewById(R.id.toolbar_detail_trip);
        setSupportActionBar(toolbar);
        tripNameTxtView=findViewById(R.id.name_trip_detail);
        tripPlaceTxtView=findViewById(R.id.place_trip_detail);
        tripDateTxtView=findViewById(R.id.date_trip_detail);
        nbParticipantsTextView=findViewById(R.id.nb_participants_trip_detail);
        Intent intent=getIntent();
        recyclerView=findViewById(R.id.recycler_view_particpants_detail_trip);
        participantAdapter=new ParticipantAdapter();
        recyclerView.setAdapter(participantAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        progressBarContener=findViewById(R.id.LinearLayout_progress_bar_detail_trip);

        viewContener=findViewById(R.id.LinearLayout_content_detail_trip);

        this.idTrip=intent.getStringExtra(ID_TRIP_DETAIL);
         tripService=new TripService();
         activityService=new ActivityService();
        userService=new UserService();
        housingService=new HousingService();
        btnHousing=findViewById(R.id.btn_housing_detail);
        btnActivity=findViewById(R.id.btn_activity_detail);
        btnTransport=findViewById(R.id.btn_transport_detail);
        backButton = findViewById(R.id.backButton);
        btnShowMoreHousings=findViewById(R.id.btn_show_some_housing_trip_detail);
        btnEditTrip=findViewById(R.id.btn_edit_detail_trip);
        btnEditTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), NewTripActivity.class);
                intent.putExtra(NewTripActivity.ID_TRIP_NEW_TRIP,idTrip);
                startActivityForResult(intent,REQUEST_CODE_EDIT_TRIP);
            }
        });
        Intent intentExtra = getIntent();
       fromAirBnb=intent.getBooleanExtra(FROM_AIR_BNB,false);

        data= intentExtra.getData();
        if (data!=null) {
            if (firebaseAuth.getCurrentUser() == null) {
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                i.setData(data);
                startActivity(i);
            } else {
                idTrip = data.getPath().split("/")[1];
            }
        }
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (data != null||fromAirBnb){
                    if(ListTripActivity.getInstance()!=null){
                        ListTripActivity.getInstance().finish();
                    }
                    Intent newIntent = new Intent(view.getContext(), ListTripActivity.class);
                    startActivity(newIntent);
                }
                else if(refreshData){
                    Intent intent = new Intent();
                    intent.putExtra(NewTripActivity.RECHARGE_DATA_EDIT_TRIP,true);
                    setResult(RESULT_OK,intent);
                }
                finish();
            }
        });
        btnHousing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ListHousingActivity.class);
                intent.putExtra(ListHousingActivity.ID_TRIP_LIST_HOUSING,idTrip);
                intent.putExtra(ListHousingActivity.NAME_TRIP_LIST_HOUSING,nameTrip);
                startActivity(intent);
            }
        });

        btnActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ListActivitiesActivity.class);
                intent.putExtra(ListActivitiesActivity.ID_TRIP_LIST_ACTIVITY,idTrip);
                intent.putExtra(ListActivitiesActivity.NAME_TRIP_LIST_ACTIVITY,nameTrip);
                intent.putExtra(ListActivitiesActivity.NB_PARTICIPANTS_LIST_ACTIVITY,nbPartcipantsOfTrip);
                startActivity(intent);
            }
        });

        btnTransport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ListTransportActivity.class);
                intent.putExtra(ListTransportActivity.ID_TRIP_LIST_TRANSPORT,idTrip);
                intent.putExtra(ListTransportActivity.NAME_TRIP_LIST_TRANSPORT,nameTrip);
                startActivity(intent);
            }
        });
        if (firebaseAuth.getCurrentUser() != null) {
            setupFields();
        }
    }


    /**
     * Call the service and set the fields of the selected trip
     * Also setup the recycler for the partcipants chip
     */
    public void setupFields(){
        progressBarContener.setVisibility(View.VISIBLE);
        viewContener.setVisibility(View.GONE);
        final HousingActionInterface housingActionInterface=this;

        tripService.getTripWithID(idTrip, new ResultAsynchronTaskOneTrip() {
            @Override
            public void onResponseReceived(final Trip trip) {
                nbPartcipantsOfTrip=trip.getParticipantsID().size();
                housingAdapter=new HousingAdapter(housingActionInterface,"DetailTrip", nbPartcipantsOfTrip);
                recyclerViewHousings=findViewById(R.id.recycler_view_housings_detail_trip);
                recyclerViewHousings.setAdapter(housingAdapter);
                recyclerViewHousings.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                nameTrip=trip.getName();
                nbParticipantsCharged=0;
                if(data!=null){
                    if(!trip.getParticipantsID().contains(firebaseAuth.getCurrentUser().getUid())){
                    for (String idActivity: trip.getActivitiesId()){
                        activityService.addOrUpdateParticipantOpinion(idActivity,firebaseAuth.getCurrentUser().getUid(),getResources().getString(R.string.without_opinion));
                        }
                    tripService.addParticipantToTrip(firebaseAuth.getCurrentUser().getUid(),idTrip);
                    }
                }
                String tripStartDate=getResources().getString(R.string.trip_start_date_not_spicified);
                String tripEndDate=getResources().getString(R.string.trip_end_date_not_spicified);
                if(trip.getStartDate()!=null){
                    tripStartDate=ManipulateDate.getDateFormatFrench(trip.getStartDate().toString());
                }
                if(trip.getEndDate()!=null) {
                    tripEndDate = ManipulateDate.getDateFormatFrench(trip.getEndDate().toString());
                }
                tripNameTxtView.setText(trip.getName());
                if(! "".equals(trip.getLocation())){
                    tripPlaceTxtView.setText(trip.getLocation());
                }
                else{
                    tripPlaceTxtView.setText(getResources().getString(R.string.location_not_specified_detail_trip));
                }
                if(! getResources().getString(R.string.trip_start_date_not_spicified).equals(tripStartDate)){
                    tripDateTxtView.setText("Du "+ tripStartDate+ " au "+tripEndDate);
                }
                else {
                    tripDateTxtView.setText(getResources().getString(R.string.date_not_specified));
                }
                nbParticipantsTextView.setText(trip.getParticipantsID().size()+" "+getString(R.string.nb_participants_detail_trip));
                participantAdapter.emptyDataSet();
                Intent intentExtra = getIntent();
                final Uri data = intentExtra.getData();
                for (int i=0;i<trip.getParticipantsID().size();i++){
                    userService.getUserWithID(trip.getParticipantsID().get(i), new ResultAsynchronTaskUser() {
                        @Override
                        public void onResponseReceived(User user) {
                            ParticipantViewModel participantViewModel;
                            if (user.getId().equals(firebaseAuth.getCurrentUser().getUid())){
                                participantViewModel=new ParticipantViewModel(getResources().getString(R.string.trip_proposed_by_you),"");
                            }
                            else{
                                participantViewModel=new ParticipantViewModel(user.getName(),"");
                            }
                            participantAdapter.bindViewModel(participantViewModel);
                            nbParticipantsCharged++;
                            if (nbParticipantsCharged==trip.getParticipantsID().size()){
                                viewContener.setVisibility(View.VISIBLE);
                                progressBarContener.setVisibility(View.GONE);
                                if(data!=null) {
                                    participantAdapter.bindViewModelJoinTrip();
                                }
                            }
                        }
                    });
                }
            }

            @Override
            public void onAllResponseReceived() {

            }

            @Override
            public void onNoResponseReceived() {
                Toast toast = Toast.makeText(getApplicationContext(),getResources().getString(R.string.msg_trip_dont_exist) , Toast.LENGTH_LONG);
                toast.show();
                if(ListTripActivity.getInstance()!=null){
                    ListTripActivity.getInstance().finish();
                }
                Intent newIntent = new Intent(getApplicationContext(), ListTripActivity.class);
                startActivity(newIntent);
            }
        });
    }
    @Override
    public void onBackPressed() {
        if (data != null||fromAirBnb){
            if(ListTripActivity.getInstance()!=null){
                ListTripActivity.getInstance().finish();
            }
            Intent newIntent = new Intent(getApplicationContext(), ListTripActivity.class);
            startActivity(newIntent);
        }
        else if(refreshData){
            Intent intent = new Intent();
            intent.putExtra(NewTripActivity.RECHARGE_DATA_EDIT_TRIP,true);
            setResult(RESULT_OK,intent);
        }
        finish();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REQUEST_CODE_EDIT_TRIP && resultCode== Activity.RESULT_OK){
            if(data==null){
                throw new NullPointerException();
            }
                    idTrip=data.getStringExtra(DetailTripActivity.ID_TRIP_DETAIL);
                     refreshData=data.getBooleanExtra(NewTripActivity.RECHARGE_DATA_EDIT_TRIP,false);
                    if(refreshData){
                        setupFields();
                    }
        }
    }


    @Override
    public void housingClick(Housing housing) {
        if(firebaseAuth.getCurrentUser().getUid().equals(housing.getIdUserOwner())){
            Intent intent = new Intent(getApplicationContext(), EditHousingActivity.class);
            intent.putExtra(EditHousingActivity.ID_HOUSING_EDIT_HOUSING,housing.getId());
            intent.putExtra(EditHousingActivity.ID_TRIP_EDIT_HOUSING,housing.getIdTrip());
            startActivity(intent);
        }
        else {
            Intent intent = new Intent(getApplicationContext(), DetailHousingActivity.class);
            intent.putExtra(DetailHousingActivity.ID_HOUSING_DETAIL_HOUSING,housing.getId());
            intent.putExtra(DetailHousingActivity.ID_TRIP_DETAIL_HOUSING,housing.getIdTrip());
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        FirebaseUser currentUser=firebaseAuth.getCurrentUser();
        if(currentUser!=null){
            getMenuInflater().inflate(R.menu.menu_user_online_detail_trip, menu);
        }else {
            getMenuInflater().inflate(R.menu.menu_user_offline, menu);
        }
        return true;
    }

    /**
     * Our menu with a share button added to share the trip with others
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)  {
        int id = item.getItemId();
        if (id == R.id.action_connexion) {
            Intent intent = new Intent(this, LoginActivity.class);
            this.startActivity(intent);
        }
        else if(id == R.id.action_profil){
            Intent intent = new Intent(this, ProfileActivity.class);
            this.startActivity(intent);
        }
        else if(id == R.id.action_share){
            try {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                String shareMessage= getResources().getString(R.string.link_app_detail_trip)+idTrip;
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.chose_type_of_share)));
            } catch(Exception e) {

            }
        }

        else if(id == R.id.action_log_off){
            firebaseAuth.signOut();
            Toast toast = Toast.makeText(this, "Vous etes déconnecté", Toast.LENGTH_LONG);
            toast.show();
            Intent intent = new Intent(this, ListTripActivity.class);
            this.startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

}
