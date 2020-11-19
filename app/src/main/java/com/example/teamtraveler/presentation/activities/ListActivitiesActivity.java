package com.example.teamtraveler.presentation.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;
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
import com.example.teamtraveler.data.api.services.resultAsynchTaskUser.ResultAsynchronTaskUserName;
import com.example.teamtraveler.data.entities.Activity;
import com.example.teamtraveler.data.entities.Trip;
import com.example.teamtraveler.data.entities.User;
import com.example.teamtraveler.presentation.activities.SwipeController.dialog.DialogDeleteActivity;
import com.example.teamtraveler.presentation.activities.recyclerView.ActivityActionInterface;
import com.example.teamtraveler.presentation.activities.recyclerView.ActivityAdapter;
import com.example.teamtraveler.presentation.trip.SwipeController.SwipeHelper;
import com.example.teamtraveler.presentation.trip.SwipeController.SwipeInterface;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class ListActivitiesActivity extends BaseActivity implements ActivityActionInterface {
    public final static String ID_TRIP_LIST_ACTIVITY = "com.example.teamtraveler.presentation.activities.ID_TRIP_LIST_ACTIVITY";
    public final static String NAME_TRIP_LIST_ACTIVITY = "com.example.teamtraveler.presentation.activities.NAME_TRIP_LIST_ACTIVITY";
    public static final String NB_PARTICIPANTS_LIST_ACTIVITY = "com.example.teamtraveler.presentation.activities.NB_PARTICIPANTS_LIST_ACTIVITY";

    private ProgressBar progressBarTop,progressBarButtom;
    private RecyclerView recyclerView;
    private ActivityAdapter activityAdapter;
    private ActivityService activityService;
    private TripService tripService;
    private UserService userService;
    private FirebaseAuth firebaseAuth;
    private String idTrip,nameTrip;

    private FloatingActionButton btnCreateActivity;
    private TextView nameTripTextView;
    private ImageButton backImageButton;
    private int REQUEST_UPDATE_ACTIVITY_LIST=6;
    public final static int RESULT_UPDATE_OPINION_ACTIVITY_LIST=6;
    int i;
    private TextView no_activity_yet;
    private SwipeHelper swipeHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_activity);
        recyclerView =findViewById(R.id.recycler_view_activity);
        nameTripTextView=findViewById(R.id.name_trip_list_activity);
        backImageButton=findViewById(R.id.backButton_list_activity);
        no_activity_yet = findViewById(R.id.no_activity_yet);

        Intent intent = getIntent();
        idTrip = intent.getStringExtra(ID_TRIP_LIST_ACTIVITY);
        nameTrip=intent.getStringExtra(NAME_TRIP_LIST_ACTIVITY);
        nameTripTextView.setText(nameTrip);
        tripService=new TripService();
        activityAdapter = new ActivityAdapter(this);
        recyclerView.setAdapter(activityAdapter);
        firebaseAuth=FirebaseAuth.getInstance();
        activityService=new ActivityService();

        userService=new UserService();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        Toolbar toolbar = findViewById(R.id.list_activity_toolbar);
        setSupportActionBar(toolbar);
        progressBarTop=findViewById(R.id.progress_bar_top_activity);

        progressBarButtom=findViewById(R.id.progress_bar_buttom_activity);

        if (currentUser != null) {
            initRecyclerView(idTrip);
        }
        btnCreateActivity=findViewById(R.id.btn_new_activity_list_activity);
        btnCreateActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), NewActivitiesActivity.class);
                intent.putExtra(NewActivitiesActivity.ID_TRIP_NEW_ACTIVITY,idTrip);
                intent.putExtra(NAME_TRIP_LIST_ACTIVITY,nameTrip);
                startActivityForResult(intent,REQUEST_UPDATE_ACTIVITY_LIST);
                finish();
            }
        });
        backImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode,data);
        if (requestCode==REQUEST_UPDATE_ACTIVITY_LIST && resultCode== android.app.Activity.RESULT_OK){
            if(data==null){
                throw new NullPointerException();
            }
            idTrip = data.getStringExtra(ID_TRIP_LIST_ACTIVITY);
            initRecyclerView(idTrip);
        }
        else if(requestCode==REQUEST_UPDATE_ACTIVITY_LIST && resultCode==RESULT_UPDATE_OPINION_ACTIVITY_LIST){
            activityAdapter = new ActivityAdapter(this);
            recyclerView.setAdapter(activityAdapter);
            initRecyclerView(idTrip);
        }
    }


    /**
     * Init the recyclerview and call the service
     * @param idTrip
     */
    public void initRecyclerView(final String idTrip){
        activityAdapter.emptyDataSet();
        showTopProgressBar();
        activityService.getActivitiesOfTrip(idTrip, new ResultAsynchronTaskOneActivity() {
            @Override
            public void onResponseReceived(Activity response){
                activityAdapter.bindViewModel(response);///iciii refraichier le recycler view des participantsi
            }

            @Override
            public void onNoResponseReceived(){
                hideTopProgressBar();
                hideButtomProgressBar();
                no_activity_yet.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAllResponseReceived() {
                hideTopProgressBar();
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        final FragmentActivity activity = this;
        if(swipeHelper==null){
            swipeHelper = new SwipeHelper(this, recyclerView) {
                @Override
                public void instantiateUnderlayButton(final RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons) {
                    underlayButtons.add(new SwipeHelper.UnderlayButton(
                            getResources().getString(R.string.txt_delete),
                            R.drawable.ic_delete,
                            R.color.color_supprimer_activity,
                            getResources(),
                            new SwipeInterface() {
                                @Override
                                public void onClickQuitter(int pos) {

                                    new DialogDeleteActivity(activity,idTrip,activityAdapter,((ActivityAdapter.ActivityViewHolder)viewHolder).getId(),((ActivityAdapter.ActivityViewHolder)viewHolder).getName()).show();

                                }
                            }
                    ));
                }
            };
        }
    }


    /**
     * Si on clique sur une activité qu'on a créer on peut la modifier si on ne l'a pas créer on ouvre le detail
     * @param activity
     */
    @Override
    public void activityClick(Activity activity) {
        if (firebaseAuth.getCurrentUser().getUid().equals(activity.getIdUserOwner())) {
            Intent intent = new Intent(getApplicationContext(), NewActivitiesActivity.class);
            intent.putExtra(NewActivitiesActivity.ID_TRIP_UPDATE_ACTIVITY, idTrip);
            intent.putExtra(NewActivitiesActivity.ID_ACTIVITY_LIST_ACTIVITY, activity.getId());
            intent.putExtra(NAME_TRIP_LIST_ACTIVITY,nameTrip);
            startActivityForResult(intent,REQUEST_UPDATE_ACTIVITY_LIST);
        }
        else {
            Intent intent = new Intent(getApplicationContext(), DetailActivitiesActivity.class);
            intent.putExtra(DetailActivitiesActivity.ID_TRIP_DETAIL_ACTIVITY, idTrip);
            intent.putExtra(NAME_TRIP_LIST_ACTIVITY,nameTrip);
            intent.putExtra(DetailActivitiesActivity.ID_ACTIVITY_DETAIL_ACTIVITY, activity.getId());
            startActivityForResult(intent,REQUEST_UPDATE_ACTIVITY_LIST);
        }
    }

    @Override
    public void showTopProgressBar(){
        progressBarTop.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideTopProgressBar(){
        progressBarTop.setVisibility(View.GONE);
    }

    @Override
    public void showButtomProgressBar() {
        progressBarButtom.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideButtomProgressBar() {
        progressBarButtom.setVisibility(View.GONE);
    }

    @Override
    public void changeMarginButtomOfRecyclerView(int marginButtom) {
        if (recyclerView.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) recyclerView.getLayoutParams();
            p.setMargins(0, 0, 0, marginButtom);
            recyclerView.requestLayout();
        }
    }


    /**
     * Call the service to get the list of participant (with opinion) of each activity
     * @param idOwnerOfActivity
     * @param resultAsynchronTaskUserName
     */
    @Override
    public void getParticipants(final String idOwnerOfActivity,final ResultAsynchronTaskUserName resultAsynchronTaskUserName) {
            tripService.getTripWithID(idTrip, new ResultAsynchronTaskOneTrip() {
                @Override
                public void onResponseReceived(Trip response) {
                    final int participantsIdSize=response.getParticipantsID().size();

                    i=0;
                    while( i<participantsIdSize) {
                        userService.getUserWithID(response.getParticipantsID().get(i), new ResultAsynchronTaskUser() {
                            @Override
                            public void onResponseReceived(User response) {
                                String currentUserIsOwner=getResources().getString(R.string.activity_proposed_by_you);
                                if (response.getId().equals(idOwnerOfActivity)){
                                    if (response.getId().equals(firebaseAuth.getCurrentUser().getUid())) {
                                        response.setName(currentUserIsOwner);
                                        resultAsynchronTaskUserName.onResonseReceived(response);
                                        resultAsynchronTaskUserName.onResonseReceivedNameOwnerOfActivity(currentUserIsOwner);
                                    }
                                    else{
                                        resultAsynchronTaskUserName.onResonseReceived(response);
                                        resultAsynchronTaskUserName.onResonseReceivedNameOwnerOfActivity(response.getName());
                                    }
                                }
                                else {
                                    if (!response.getId().equals(firebaseAuth.getCurrentUser().getUid())) {
                                        resultAsynchronTaskUserName.onResonseReceived(response);
                                    }
                                    else {
                                        User userWithNameOfCurrentUser=response;
                                        userWithNameOfCurrentUser.setName(currentUserIsOwner);
                                        resultAsynchronTaskUserName.onResonseReceived(userWithNameOfCurrentUser);
                                    }

                                }

                                if (i==participantsIdSize){
                                    resultAsynchronTaskUserName.onResonseReceivedAllUsers();
                                }

                            }
                        });

                        i++;
                    }
                }

                @Override
                public void onAllResponseReceived() {

                }

                @Override
                public void onNoResponseReceived() {

                }
            });
    }



}
