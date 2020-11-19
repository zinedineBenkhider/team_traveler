package com.example.teamtraveler.presentation.transport;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teamtraveler.R;
import com.example.teamtraveler.Utils.LockScreenActivity;
import com.example.teamtraveler.Utils.ManipulateDate;
import com.example.teamtraveler.data.api.services.TransportService;
import com.example.teamtraveler.data.api.services.TripService;
import com.example.teamtraveler.data.api.services.UserService;
import com.example.teamtraveler.data.api.services.resultAsynchTaskTransport.ResultAsynchronTaskOneTransport;
import com.example.teamtraveler.data.api.services.resultAsynchTaskUser.ResultAsynchronTaskUser;
import com.example.teamtraveler.data.entities.Transport;
import com.example.teamtraveler.data.entities.User;
import com.example.teamtraveler.presentation.activities.ListActivitiesActivity;
import com.example.teamtraveler.presentation.activities.recyclerView.ParticipantAdapter;
import com.example.teamtraveler.presentation.activities.recyclerView.ParticipantViewModel;
import com.example.teamtraveler.presentation.trip.ListTripActivity;
import com.example.teamtraveler.presentation.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Iterator;
import java.util.Set;

public class DetailTransportActivity extends LockScreenActivity {
    public final static String ID_TRIP_DETAIL_TRANSPORT = "com.example.teamtraveler.presentation.transport.ID_TRIP_DETAIL_TRANSPORT";
    public static final String ID_TRANSPORT_DETAIL_TRANSPORT = "com.example.teamtraveler.presentation.transport.ID_TRANSPORT_DETAIL_TRANSPORT";
    private TextView nameTextView, typeTextView, departureLocationTextView, reachLocationTextView, dateDepartureTextView, timeDepartureTextView, timeReachTextView,titlePartcipants;
    private Button joinExitTransportBtn;
    private String idTrip, idTransport,nameTrip;
    private LinearLayout linearLayoutProgressBar, linearLayoutDetail;
    private TripService tripService;
    private TransportService transportService;
    private UserService userService;
    private FirebaseAuth firebaseAuth;
    private int nbUsers;
    private ImageButton backButton;
    private FirebaseUser currentUser;
    private TextView nameTripTextView;
    private String oldOpinionOfCurrentUser,currentOpinionOfUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentOpinionOfUser="";
        oldOpinionOfCurrentUser=currentOpinionOfUser;
        setContentView(R.layout.activity_detail_transport);
        nameTripTextView=findViewById(R.id.name_trip_detail_transport);
        nameTextView = findViewById(R.id.name_transport_detail);
        departureLocationTextView = findViewById(R.id.location_departure_transport_detail);
        reachLocationTextView = findViewById(R.id.location_reach_transport_detail);
        dateDepartureTextView = findViewById(R.id.date_departure_transport_detail);
        timeDepartureTextView = findViewById(R.id.time_departure_transport_detail);
        timeReachTextView = findViewById(R.id.time_reach_transport_detail);
        typeTextView = findViewById(R.id.type_transport_detail);
        titlePartcipants=findViewById(R.id.title_participants_detail_activity);
        linearLayoutDetail = findViewById(R.id.LinearLayoutDetailTransport);
        linearLayoutProgressBar = findViewById(R.id.LinearLayout_progress_bar_detail_transport);
        joinExitTransportBtn=findViewById(R.id.join_exit_detail_transport);
        backButton=findViewById(R.id.backButton_detail_transport);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentOpinionOfUser.equals(oldOpinionOfCurrentUser)){
                    finish();
                }
                else {
                    Intent intent = getIntent();
                    setResult(Activity.RESULT_OK,intent);
                    finish();
                }

            }
        });
        firebaseAuth=FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        transportService = new TransportService();
        tripService = new TripService();
        userService=new UserService();
        Toolbar toolbar = findViewById(R.id.detail_transport_toolbar);
        setSupportActionBar(toolbar);
        final Intent intent = getIntent();
        nameTrip=intent.getStringExtra(ListTransportActivity.NAME_TRIP_LIST_TRANSPORT);
        nameTripTextView.setText(nameTrip);
        idTrip = intent.getStringExtra(ID_TRIP_DETAIL_TRANSPORT);
        idTransport = intent.getStringExtra(ID_TRANSPORT_DETAIL_TRANSPORT);

        linearLayoutProgressBar.setVisibility(View.VISIBLE);
        linearLayoutDetail.setVisibility(View.GONE);
        setupFields();
        
    }

    public void setupFields(){
        final ParticipantAdapter participantAdapter=new ParticipantAdapter();
        final RecyclerView recyclerView=findViewById(R.id.recycler_view_particpants_detail_transport);
        recyclerView.setAdapter(participantAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        transportService.getTransportWithID(idTransport, new ResultAsynchronTaskOneTransport() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onResponseReceived(final Transport transport) {
                final String joinTransport=getResources().getString(R.string.join_transport_opinion);
                final String exitTransport=getResources().getString(R.string.exit_transport);
                joinExitTransportBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(transport.getUsersParticipations().containsKey(currentUser.getUid())){
                            if(transport.getUsersParticipations().get(currentUser.getUid()).equals(joinTransport)){
                                transport.getUsersParticipations().put(currentUser.getUid(),exitTransport);
                                joinExitTransportBtn.setText(joinTransport);
                                joinExitTransportBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.color_participer_transport)));
                                currentOpinionOfUser=joinTransport;

                            }
                            else {
                                joinExitTransportBtn.setText(exitTransport);
                                joinExitTransportBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.color_quitter_transport)));
                                transport.getUsersParticipations().put(currentUser.getUid(),joinTransport);
                                currentOpinionOfUser=exitTransport;
                            }
                        }
                        else {
                            joinExitTransportBtn.setText(exitTransport);
                            joinExitTransportBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.color_quitter_transport)));
                            currentOpinionOfUser=exitTransport;
                            transport.getUsersParticipations().put(currentUser.getUid(),joinTransport);
                        }
                        transportService.updateOpinionOfParticipant(transport.getId(),transport.getUsersParticipations());
                    }
                });

                nameTextView.setText(transport.getName());
                String dateStr="";
                String timeDepartureStr="";
                String timeReachStr="";
                String departureLocation="";
                String reachLocation="";
                String type="";

                if(!TextUtils.isEmpty(transport.getDeparture())){
                    departureLocation=transport.getDeparture();
                }

                if(!TextUtils.isEmpty(transport.getReach())){
                    reachLocation=transport.getReach();
                }

                if(!TextUtils.isEmpty(transport.getType())) {
                    type=transport.getType();
                }

                if(transport.getDepartureDate()!=null){
                    dateStr= ManipulateDate.getDateFormatFrench(transport.getDepartureDate().toGMTString());
                }

                if(transport.getTimeDeparture()!=null){
                    timeDepartureStr=ManipulateDate.getFormatTime(transport.getTimeDeparture().getHours(),transport.getTimeDeparture().getMinutes());
                }

                if(transport.getTimeReach()!=null){
                    timeReachStr=ManipulateDate.getFormatTime(transport.getTimeReach().getHours(),transport.getTimeReach().getMinutes());
                }

                typeTextView.setText(type);
                reachLocationTextView.setText(reachLocation);
                departureLocationTextView.setText(departureLocation);
                dateDepartureTextView.setText(dateStr);
                timeDepartureTextView.setText(timeDepartureStr);
                timeReachTextView.setText(timeReachStr);
                if(transport.getUsersParticipations().containsKey(currentUser.getUid())){
                    joinExitTransportBtn.setText(transport.getUsersParticipations().get(currentUser.getUid()));
                    if(transport.getUsersParticipations().get(currentUser.getUid()).equals(joinTransport)){

                        joinExitTransportBtn.setText(exitTransport);
                        joinExitTransportBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.color_quitter_transport)));

                    }
                    else{
                        joinExitTransportBtn.setText(joinTransport);
                        joinExitTransportBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.color_participer_transport)));
                    }
                }
                else {
                    joinExitTransportBtn.setText(joinTransport);
                    joinExitTransportBtn.setBackgroundColor(getResources().getColor(R.color.color_participer_transport));
                }
                oldOpinionOfCurrentUser=joinExitTransportBtn.getText().toString();
                currentOpinionOfUser=joinExitTransportBtn.getText().toString();

                final Set<String> usersId=transport.getUsersParticipations().keySet();
                Iterator<String> iterator=usersId.iterator();
                nbUsers=0;
                if (transport.getUsersParticipations().containsValue(joinTransport)) {
                    while (iterator.hasNext()) {
                        final String userId = iterator.next();
                        userService.getUserWithID(userId, new ResultAsynchronTaskUser() {
                            @Override
                            public void onResponseReceived(User user) {
                                if (transport.getUsersParticipations().get(userId).equals(joinTransport)) {
                                    if(!user.getId().equals(currentUser.getUid())){
                                        participantAdapter.bindViewModel(new ParticipantViewModel(user.getName(), ""));
                                    }
                                    else {
                                        String currentUser=getResources().getString(R.string.you_join_transport);
                                        participantAdapter.bindViewModel(new ParticipantViewModel(currentUser, ""));
                                    }
                                }
                                if (nbUsers == usersId.size()) {
                                    linearLayoutProgressBar.setVisibility(View.GONE);
                                    linearLayoutDetail.setVisibility(View.VISIBLE);
                                }
                            }
                        });
                        nbUsers++;
                    }
                }
                else {
                    titlePartcipants.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                    linearLayoutProgressBar.setVisibility(View.GONE);
                    linearLayoutDetail.setVisibility(View.VISIBLE);
                }



            }

            @Override
            public void onNoResponseReceived() {

            }
        });
    }
    @Override
    public void onBackPressed(){
        if (currentOpinionOfUser.equals(oldOpinionOfCurrentUser)){
            finish();
        }
        else {
            Intent intent = getIntent();
            setResult(Activity.RESULT_OK,intent);
            finish();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_connexion) {
            Intent intent = new Intent(this, LoginActivity.class);
            this.startActivity(intent);
        }
        else if(id == R.id.action_log_off){
            firebaseAuth.signOut();
            Toast toast = Toast.makeText(this, getResources().getString(R.string.msg_user_sign_out), Toast.LENGTH_LONG);
            toast.show();
            Intent intent = new Intent(this, ListTripActivity.class);
            this.startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        FirebaseUser currentUser=firebaseAuth.getCurrentUser();
        if(currentUser!=null){
            getMenuInflater().inflate(R.menu.menu_user_online, menu);
        }else {
            getMenuInflater().inflate(R.menu.menu_user_offline, menu);
        }
        return true;
    }
}