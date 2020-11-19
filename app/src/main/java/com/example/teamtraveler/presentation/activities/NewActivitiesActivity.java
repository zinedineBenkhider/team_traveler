package com.example.teamtraveler.presentation.activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teamtraveler.R;
import com.example.teamtraveler.Utils.LockScreenActivity;
import com.example.teamtraveler.Utils.ParseWebActivity;
import com.example.teamtraveler.data.api.services.ActivityService;
import com.example.teamtraveler.data.api.services.TripService;
import com.example.teamtraveler.data.api.services.UserService;
import com.example.teamtraveler.data.api.services.resultAsynchTaskActivity.ResultAsynchTaskActivityAdded;
import com.example.teamtraveler.data.api.services.resultAsynchTaskActivity.ResultAsynchTaskActivityUpdated;
import com.example.teamtraveler.data.api.services.resultAsynchTaskActivity.ResultAsynchronTaskOneActivity;
import com.example.teamtraveler.data.api.services.resultAsynchTaskTrip.ResultAsynchronTaskOneTrip;
import com.example.teamtraveler.data.api.services.resultAsynchTaskUser.ResultAsynchronTaskUser;
import com.example.teamtraveler.data.entities.Activity;
import com.example.teamtraveler.data.entities.Trip;
import com.example.teamtraveler.data.entities.User;
import com.example.teamtraveler.presentation.activities.recyclerView.ParticipantAdapter;
import com.example.teamtraveler.presentation.activities.recyclerView.ParticipantViewModel;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewActivitiesActivity extends LockScreenActivity {
    public final static String ID_TRIP_NEW_ACTIVITY="com.example.teamtraveler.presentation.activities.ID_TRIP_NEW_ACTIVITY";
    public final static String ID_TRIP_UPDATE_ACTIVITY="com.example.teamtraveler.presentation.activities.ID_TRIP_UPDATE_ACTIVITY";
    public static final String NB_PARTICPANTS_NEW_ACTIVITY = "com.example.teamtraveler.presentation.activities.NB_PARTICPANTS_NEW_ACTIVITY";
    private ActivityService activityService;
    private EditText entitledEditText,priceEditText, locationEditText,webSiteEditText;
    private TextView titleTextView;
    private MaterialBetterSpinner typeSpinner, tripSpinner;
    private TextInputLayout entitledWrapper,priceWrapper;
    private Button btnCreate,btnUpdate;
    private String idTrip,nameTrip;
    private String oldOpinion,currentOpinion;
    final static int LENGTH_MAX_FIELD_ENTITLED=20;
    private TripService tripService;
    public static final String ID_ACTIVITY_LIST_ACTIVITY ="com.example.teamtraveler.presentation.activities.ID_ACTIVITY_LIST_ACTIVITY" ;
    private static final String[] TYPES_SPINNER_DATA = new String[] {"Sport", "Jeu", "Spectacle", "Visite","Restaurant","Autre",""};

    final List<String> idsOfTripsNames=new ArrayList<>();
    final List<String> TYPES_DATA = new ArrayList<>();

    private FirebaseUser currentUser;
    private String idActivity;
    private int nbParticipants;
    private Chip chipInterested,chipParticipate,chipNotParticipate;
    private ChipGroup chipGroupParticipations;
    private LinearLayout linearLayoutFormCreateUpdate,linearLayoutProgressBar,linearLayoutParticipants, linearLayoutFormCreateTrip,LinearLayoutOwnerOfActivity;
    private String pathWeb=null;
    private TextView nameTriptextView;
    private Activity currentActivity=null;
    private ImageButton backButton;
    private RecyclerView recyclerViewParticipants;
    private ParticipantAdapter participantAdapter;
    private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        oldOpinion="";
        currentOpinion="";
        setContentView(R.layout.activity_new_activity);

        Intent intentExtra=getIntent();
        pathWeb = intentExtra.getStringExtra(Intent.EXTRA_TEXT);

        chipInterested=findViewById(R.id.chip_interested);
        chipParticipate=findViewById(R.id.chip_participate);
        chipNotParticipate=findViewById(R.id.chip_not_participate);
        entitledEditText=findViewById(R.id.entitled_form_activity);
        typeSpinner=findViewById(R.id.type_spinner_form_activity);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(NewActivitiesActivity.this, android.R.layout.simple_dropdown_item_1line, TYPES_SPINNER_DATA);
        typeSpinner.setAdapter(spinnerAdapter);
        locationEditText=findViewById(R.id.location_form_activity);
        webSiteEditText=findViewById(R.id.web_site_form_activity);
        priceEditText=findViewById(R.id.price_form_activity);
        entitledWrapper=findViewById(R.id.entitled_form_activity_wrapper);
        priceWrapper=findViewById(R.id.price_form_activity_wrapper);
        titleTextView=findViewById(R.id.title_new_update_activity);
        nameTriptextView=findViewById(R.id.name_trip_new_activity);
        LinearLayoutOwnerOfActivity=findViewById(R.id.LinearLayout_owner_of_activity);
        tripSpinner = findViewById(R.id.type_spinner_form_trip_for_activity);
        tripSpinner.setVisibility(View.GONE);

        Intent intent=getIntent();
        nameTrip=intent.getStringExtra(ListActivitiesActivity.NAME_TRIP_LIST_ACTIVITY);
        nameTriptextView.setText(nameTrip);
        currentUser=FirebaseAuth.getInstance().getCurrentUser();
        chipGroupParticipations=findViewById(R.id.participations_chips_new_activity);
        linearLayoutFormCreateUpdate=findViewById(R.id.LinearLayoutFormCreateActivity);
        linearLayoutProgressBar=findViewById(R.id.LinearLayout_progress_bar_update_activity);
        linearLayoutParticipants=findViewById(R.id.LinearLayout_participants_new_activity);
        linearLayoutFormCreateTrip=findViewById(R.id.LinearLayout_new_housing);
        linearLayoutFormCreateTrip.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(entitledEditText.getWindowToken(), 0);
                return true;
            }
        });
        userService=new UserService();
        setListnerToEditTexts();
        activityService =new ActivityService();
        tripService=new TripService();
        btnCreate=findViewById(R.id.btn_create_form_activity);
        btnUpdate=findViewById(R.id.btn_update_form_activity);
        backButton = findViewById(R.id.button_back_new_activity);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( currentOpinion.equals(oldOpinion)){
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


        nbParticipants=intent.getIntExtra(NB_PARTICPANTS_NEW_ACTIVITY,0);
        idActivity=intent.getStringExtra(ID_ACTIVITY_LIST_ACTIVITY);
        //If the activity is created from a shared website
        if(pathWeb!=null) {
            tripSpinner.setVisibility(View.VISIBLE);
            linearLayoutFormCreateUpdate.setVisibility(View.GONE);
            chipGroupParticipations.setVisibility(View.GONE);
            linearLayoutParticipants.setVisibility(View.GONE);
            LinearLayoutOwnerOfActivity.setVisibility(View.GONE);
            btnUpdate.setVisibility(View.GONE);
            linearLayoutProgressBar.setVisibility(View.VISIBLE);
            webSiteEditText.setText(pathWeb);
            nameTriptextView.setText("Partager via un site");


            initFields();
        }
        else if (intent.getStringExtra(ID_TRIP_NEW_ACTIVITY)!=null){
            idTrip=intent.getStringExtra(ID_TRIP_NEW_ACTIVITY);
            btnUpdate.setVisibility(View.GONE);
            linearLayoutParticipants.setVisibility(View.GONE);
            titleTextView.setText(getResources().getString(R.string.create_activity_title));
            chipGroupParticipations.setVisibility(View.GONE);
            LinearLayoutOwnerOfActivity.setVisibility(View.GONE);
        }
        else {
            idTrip=intent.getStringExtra(ID_TRIP_UPDATE_ACTIVITY);
            initFieldsWhenUpdate();
            btnCreate.setVisibility(View.GONE);
            titleTextView.setText(getResources().getString(R.string.update_activity_title));
            linearLayoutFormCreateUpdate.setVisibility(View.GONE);
            linearLayoutProgressBar.setVisibility(View.VISIBLE);
            chipGroupParticipations.setVisibility(View.VISIBLE);
        }

        setListersToBtns();
        recyclerViewParticipants=findViewById(R.id.recycler_view_particpants_new_activity);
        participantAdapter=new ParticipantAdapter();
        recyclerViewParticipants.setAdapter(participantAdapter);
        recyclerViewParticipants.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));

    }

    @Override
    public void onBackPressed(){
        if( currentOpinion.equals(oldOpinion)){
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

    public void setListersToBtns(){
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Activity activity=checkFields();
                if (activity!=null) {

                    activityService.addActivity(activity, new ResultAsynchTaskActivityAdded() {
                        @Override
                        public void onActivityAdded() {
                            tripService.getTripWithID(idTrip, new ResultAsynchronTaskOneTrip() {
                                @Override
                                public void onResponseReceived(Trip response) {
                                    activityService.initParticipantOpinions(activity.getId(), response.getParticipantsID(), getResources(), new ResultAsynchTaskActivityUpdated() {
                                        @Override
                                        public void onActivityUpdated() {
                                            Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.msg_activity_saved_new_activity), Toast.LENGTH_LONG);
                                            toast.show();
                                            tripService.notifyParticipants( idTrip,currentUser.getUid());//notification
                                            tripService.addActivityToTrip(idTrip,activity.getId());

                                            Intent intent = new Intent(getApplicationContext(), ListActivitiesActivity.class);
                                            intent.putExtra(ListActivitiesActivity.ID_TRIP_LIST_ACTIVITY,idTrip);
                                            intent.putExtra(ListActivitiesActivity.NAME_TRIP_LIST_ACTIVITY,nameTrip);

                                            startActivity(intent);
                                            finish();
                                            //}
                                        }
                                    });
                                }

                                @Override
                                public void onAllResponseReceived() {

                                }

                                @Override
                                public void onNoResponseReceived() {

                                }
                            });
                        }
                    });

                }


            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Activity activity=checkFields();
                if (activity!=null) {
                    activityService.updateActivity(idActivity,activity);
                    Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.msg_activity_updated_new_activity), Toast.LENGTH_LONG);
                    toast.show();
                    tripService.notifyParticipants( idTrip,currentUser.getUid());//notification
                    Intent intent = getIntent();
                    intent.putExtra(ListActivitiesActivity.ID_TRIP_LIST_ACTIVITY,idTrip);
                    intent.putExtra(ListActivitiesActivity.NB_PARTICIPANTS_LIST_ACTIVITY,nbParticipants);
                    setResult(android.app.Activity.RESULT_OK,intent);
                    finish();
                }
            }
        });
    }


    public void initFieldsWhenUpdate(){
        activityService.getActivityByID(idActivity, new ResultAsynchronTaskOneActivity() {
            @Override
            public void onResponseReceived(Activity response) {
                currentActivity = response;
                setListnersToChips();
                setBackgroundColorToChip();
                entitledEditText.setText(response.getEntitled());
                typeSpinner.setText(response.getType());
                locationEditText.setText(response.getLocation());
                webSiteEditText.setText(response.getWebSite());
                priceEditText.setText(String.valueOf(response.getPrice()));
                tripService.getTripWithID(idTrip, new ResultAsynchronTaskOneTrip() {
                    @Override
                    public void onResponseReceived(Trip response) {
                        if (response.getParticipantsID().size() == 1 ) {
                            linearLayoutParticipants.setVisibility(View.GONE);
                            linearLayoutFormCreateUpdate.setVisibility(View.VISIBLE);
                            linearLayoutProgressBar.setVisibility(View.GONE);
                        } else {
                            for (int i = 0; i < response.getParticipantsID().size(); i++) {

                                userService.getUserWithID(response.getParticipantsID().get(i), new ResultAsynchronTaskUser() {
                                    @Override
                                    public void onResponseReceived(User response) {
                                        String opinion = currentActivity.getUsersInterested().get(response.getId());
                                        if (opinion == null) {
                                            opinion = getResources().getString(R.string.without_opinion);
                                            updateOpinionOfUser(response.getId(), opinion);
                                        }
                                        if (!response.getId().equals(currentUser.getUid())) {
                                            ParticipantViewModel participantViewModel = new ParticipantViewModel(response.getName(), opinion);
                                            participantAdapter.bindViewModel(participantViewModel);
                                        }
                                        linearLayoutFormCreateUpdate.setVisibility(View.VISIBLE);
                                        linearLayoutProgressBar.setVisibility(View.GONE);
                                    }
                                });
                            }
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

            @Override
            public void onNoResponseReceived() {

            }

            @Override
            public void onAllResponseReceived() {

            }
        });}


            public void setListnerToEditTexts() {
                entitledEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean b) {
                        if (!b && TextUtils.isEmpty(entitledEditText.getText())) {
                            entitledWrapper.setErrorEnabled(true);
                            entitledWrapper.setError(getResources().getString(R.string.error_entitled_required_create_activity));
                        }
                        else{
                            entitledWrapper.setErrorEnabled(false);
                        }
                    }
                });
                tripSpinner.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        idTrip=idsOfTripsNames.get(TYPES_DATA.indexOf(charSequence.toString()));
                        nameTrip=charSequence.toString();
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
                entitledEditText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if (charSequence.length() > LENGTH_MAX_FIELD_ENTITLED) {
                            entitledWrapper.setErrorEnabled(true);
                            entitledWrapper.setError(getResources().getString(R.string.error_length_entitled_housing_create_activity));
                        } else {
                            entitledWrapper.setErrorEnabled(false);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                    }
                });
                priceEditText.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        try {
                            if (!TextUtils.isEmpty(charSequence.toString())) {
                                Integer.valueOf(charSequence.toString());
                            }
                            priceWrapper.setErrorEnabled(false);
                        } catch (Exception e) {
                            priceWrapper.setErrorEnabled(true);
                            priceWrapper.setError(getResources().getString(R.string.msg_field_must_be_integer_activity));
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                    }
                });

            }

    /**
     * Return the activity if all the fields are correct
     * @return
     */
    public Activity checkFields() {
                String entitled = entitledEditText.getText().toString();
                if (TextUtils.isEmpty(entitled)) {
                    Toast toast = Toast.makeText(this.getApplicationContext(), getResources().getString(R.string.error_entitled_required_create_activity), Toast.LENGTH_LONG);
                    toast.show();
                    entitledEditText.requestFocus(); //donner le focus au champ Nom
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); //afficher le clavier
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    return null;
                } else if (entitled.length() > LENGTH_MAX_FIELD_ENTITLED) {
                    Toast toast = Toast.makeText(this.getApplicationContext(), getResources().getString(R.string.msg_activity_entitled_length_exceed), Toast.LENGTH_LONG);
                    toast.show();
                    entitledEditText.requestFocus(); //donner le focus au champ Nom
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); //afficher le clavier
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    return null;
                }
                if (TextUtils.isEmpty(tripSpinner.getText()) && pathWeb!=null){
                    Toast toast = Toast.makeText(this.getApplicationContext(), getResources().getString(R.string.name_trip_is_required_create_housing), Toast.LENGTH_LONG);
                    toast.show();
                    tripSpinner.requestFocus();
                    return null;
                }
                String price = "";
                try {
                    price = priceEditText.getText().toString();
                    if (!TextUtils.isEmpty(price)) {
                        Integer.valueOf(priceEditText.getText().toString());
                    }
                } catch (Exception e) {
                    Toast toast = Toast.makeText(this.getApplicationContext(), getResources().getString(R.string.msg_field_must_be_integer_activity), Toast.LENGTH_LONG);
                    toast.show();
                    priceEditText.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); //afficher le clavier
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    return null;
                }
                String location = locationEditText.getText().toString();
                String type = typeSpinner.getText().toString();
                String webSite = webSiteEditText.getText().toString();


                Activity activity = new Activity(entitled, idTrip, type, location, webSite, currentUser.getUid());
                if (!TextUtils.isEmpty(price)) {
                    activity.setPrice(Integer.valueOf(price));
                }
                return activity;
            }

    /**
     * call the service on click on a chip and the opinion is updated
     */
    private void setListnersToChips() {
                chipNotParticipate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        chipInterested.setChipBackgroundColor(getResources().getColorStateList(R.color.color_chip_without_opinion));
                        chipParticipate.setChipBackgroundColor(getResources().getColorStateList(R.color.color_chip_without_opinion));

                        String opinion = getResources().getString(R.string.opinion_not_participate);

                        if (currentActivity != null) {
                            if (!currentActivity.getUsersInterested().get(currentUser.getUid()).equals(opinion)) {
                                chipNotParticipate.setChipBackgroundColor(getResources().getColorStateList(R.color.color_chip_not_participate));
                            } else {
                                chipNotParticipate.setChipBackgroundColor(getResources().getColorStateList(R.color.color_chip_without_opinion));
                                opinion = getResources().getString(R.string.without_opinion);
                            }
                            tripService.notifyParticipants(idTrip, currentUser.getUid());//notification
                            activityService.addOrUpdateParticipantOpinion(currentActivity.getId(), currentUser.getUid(), opinion);
                        } else {
                            updateOpinionOfUser(currentUser.getUid(), opinion);
                        }
                        currentOpinion = opinion;
                        currentActivity.getUsersInterested().put(currentUser.getUid(), opinion);
                    }
                });

                chipParticipate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        chipInterested.setChipBackgroundColor(getResources().getColorStateList(R.color.color_chip_without_opinion));
                        chipNotParticipate.setChipBackgroundColor(getResources().getColorStateList(R.color.color_chip_without_opinion));
                        String opinion = getResources().getString(R.string.opinion_participate);

                        if (currentActivity != null) {
                            if (!currentActivity.getUsersInterested().get(currentUser.getUid()).equals(opinion)) {
                                chipParticipate.setChipBackgroundColor(getResources().getColorStateList(R.color.color_chip_participate));
                            } else {
                                chipParticipate.setChipBackgroundColor(getResources().getColorStateList(R.color.color_chip_without_opinion));
                                opinion = getResources().getString(R.string.without_opinion);
                            }
                            tripService.notifyParticipants(idTrip, currentUser.getUid());//notification
                            activityService.addOrUpdateParticipantOpinion(currentActivity.getId(), currentUser.getUid(), opinion);
                        } else {
                            updateOpinionOfUser(currentUser.getUid(), opinion);
                        }
                        currentOpinion = opinion;
                        currentActivity.getUsersInterested().put(currentUser.getUid(), opinion);
                    }
                });

                chipInterested.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        chipParticipate.setChipBackgroundColor(getResources().getColorStateList(R.color.color_chip_without_opinion));
                        chipNotParticipate.setChipBackgroundColor(getResources().getColorStateList(R.color.color_chip_without_opinion));
                        chipInterested.setChipBackgroundColor(getResources().getColorStateList(R.color.color_chip_interested));
                        String opinion = getResources().getString(R.string.opinion_interested);

                        if (currentActivity != null) {
                            if (!currentActivity.getUsersInterested().get(currentUser.getUid()).equals(opinion)) {
                                chipInterested.setChipBackgroundColor(getResources().getColorStateList(R.color.color_chip_interested));
                            } else {
                                chipInterested.setChipBackgroundColor(getResources().getColorStateList(R.color.color_chip_without_opinion));
                                opinion = getResources().getString(R.string.without_opinion);
                            }
                            tripService.notifyParticipants(idTrip, currentUser.getUid());//notification
                            activityService.addOrUpdateParticipantOpinion(currentActivity.getId(), currentUser.getUid(), opinion);
                        } else {
                            updateOpinionOfUser(currentUser.getUid(), opinion);
                        }
                        currentOpinion = opinion;
                        currentActivity.getUsersInterested().put(currentUser.getUid(), opinion);
                    }
                });
            }


            private void setBackgroundColorToChip() {
                if (currentActivity.getUsersInterested().get(currentUser.getUid()) != null) {
                    String opinionOfUser = currentActivity.getUsersInterested().get(currentUser.getUid());
                    oldOpinion = opinionOfUser;
                    currentOpinion = oldOpinion;
                    if (opinionOfUser.equals(chipInterested.getText().toString())) {
                        chipInterested.setChipBackgroundColor(getResources().getColorStateList(R.color.color_chip_interested));
                    } else if (opinionOfUser.equals(chipParticipate.getText().toString())) {
                        chipParticipate.setChipBackgroundColor(getResources().getColorStateList(R.color.color_chip_participate));
                    } else if (opinionOfUser.equals(chipNotParticipate.getText().toString())) {
                        chipNotParticipate.setChipBackgroundColor(getResources().getColorStateList(R.color.color_chip_not_participate));
                    }
                }
            }

            private void updateOpinionOfUser(final String userId, final String opinion) {
                activityService.getActivityByID(idActivity, new ResultAsynchronTaskOneActivity() {
                    @Override
                    public void onResponseReceived(Activity response) {
                        activityService.addOrUpdateParticipantOpinion(response.getId(), userId, opinion);
                        tripService.notifyParticipants(idTrip, currentUser.getUid());//notification
                    }

                    @Override
                    public void onNoResponseReceived() {

                    }

                    @Override
                    public void onAllResponseReceived() {

                    }
                });
            }





    public void initFields() {
        tripService.getTripsInProgressOfParticipant(currentUser.getUid(), new ResultAsynchronTaskOneTrip() {
            @Override
            public void onResponseReceived(Trip response) {
                TYPES_DATA.add(response.getName());
                idsOfTripsNames.add(response.getId());

            }

            @Override
            public void onAllResponseReceived() {
                Object[] data = TYPES_DATA.toArray();
                String[] strArray = new String[data.length];
                System.arraycopy(data, 0, strArray, 0, data.length);
                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(NewActivitiesActivity.this, android.R.layout.simple_dropdown_item_1line, strArray);
                tripSpinner.setAdapter(spinnerAdapter);
            }

            @Override
            public void onNoResponseReceived() {


            }

        });
        new GetTitleOfActivityTask().execute();

    }

    /**
     * Get the title of an activity if it's a website shared with teamtraveler
     */
    class GetTitleOfActivityTask extends AsyncTask<Void, String, String> {
                @Override
                protected String doInBackground(Void... arg0) {
                    String title = "";
                    try {
                        ParseWebActivity parseWebActivity = new ParseWebActivity(pathWeb);

                        title = parseWebActivity.getTitle();
                    } catch (Exception e) {
                    }
                    return title;
                }

                @Override
                protected void onPostExecute(String result) {
                    super.onPostExecute(result);
                    entitledEditText.setText(result);
                    linearLayoutFormCreateUpdate.setVisibility(View.VISIBLE);
                    linearLayoutProgressBar.setVisibility(View.GONE);
                }
            }



    }



