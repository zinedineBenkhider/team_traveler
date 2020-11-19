package com.example.teamtraveler.presentation.transport;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teamtraveler.R;
import com.example.teamtraveler.Utils.LockScreenActivity;
import com.example.teamtraveler.Utils.ManipulateDate;
import com.example.teamtraveler.data.api.services.TransportService;
import com.example.teamtraveler.data.api.services.TripService;
import com.example.teamtraveler.data.api.services.UserService;
import com.example.teamtraveler.data.api.services.resultAsynchTaskTransport.ResultAsynchTaskTransportAdded;
import com.example.teamtraveler.data.api.services.resultAsynchTaskTransport.ResultAsynchronTaskOneTransport;
import com.example.teamtraveler.data.api.services.resultAsynchTaskUser.ResultAsynchronTaskUser;
import com.example.teamtraveler.data.entities.Transport;
import com.example.teamtraveler.data.entities.User;
import com.example.teamtraveler.presentation.activities.recyclerView.ParticipantAdapter;
import com.example.teamtraveler.presentation.activities.recyclerView.ParticipantViewModel;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;


public class NewTransportActivity extends LockScreenActivity {
    public final static String ID_TRIP_NEW_TRANSPORT="com.example.teamtraveler.presentation.transport.ID_TRIP_NEW_TRANSPORT";
    public final static String ID_TRIP_UPDATE_TRANSPORT="com.example.teamtraveler.presentation.transport.ID_TRIP_UPDATE_TRANSPORT";
    public static final String NB_PARTICPANTS_NEW_TRANSPORT = "com.example.teamtraveler.presentation.transport.NB_PARTICPANTS_NEW_TRANSPORT";
    public static final String ID_TRANSPORT_NEW_TRANSPORT="com.example.teamtraveler.presentation.transport.ID_TRANSPORT_NEW_TRANSPORT" ;
    private static final String SECONDS_TO_ADD=":00";
    private TransportService transportService;
    private UserService userService;
    private EditText nameEditText, departureLocationEditText, reachLocationEditText,dateDepartureEditText, timeDepartureEditText,timeReachEditText;
    private TextInputLayout nameWrapper,departureWrapper,reachWrapper;
    private TextView titleTextView,nameTripTextView;
    private MaterialBetterSpinner typeSpinner;
    private Button btnCreate,btnUpdate;
    private String idTrip, idTransport,nameTrip,currentOpinionOfUser,oldOpinionOfUser;
    final static int LENGTH_MAX_FIELD_NAME=20;
    final static int LENGTH_MAX_FIELD_DEPARTURE=30;
    final static int LENGTH_MAX_FIELD_REACH=30;
    private LinearLayout linearLayoutProgressBar,linearLayoutForm,linearLayoutParticipants;
    private TripService tripService;
    private ImageButton btnExitTransport;
    private Button joinExitTransportBtn;
    private ParticipantAdapter participantAdapter;
    private int nbUsers;
    private FirebaseUser currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String typeCar=getResources().getString(R.string.type_transport_car);
        String typeTrain=getResources().getString(R.string.type_transport_train);
        String typeBus=getResources().getString(R.string.type_transport_bus);
        String typeAirPlane=getResources().getString(R.string.type_transport_airplane);
        final String[] TYPES_SPINNER_DATA = new String[] {typeCar, typeTrain, typeBus,typeAirPlane,""};
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_transport);
        currentOpinionOfUser="";
        oldOpinionOfUser=currentOpinionOfUser;
        userService=new UserService();
        nameEditText = findViewById(R.id.name_form_transport);
        typeSpinner = findViewById(R.id.type_spinner_form_transport);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(NewTransportActivity.this, android.R.layout.simple_dropdown_item_1line, TYPES_SPINNER_DATA);
        btnExitTransport=findViewById(R.id.button_exit_new_transport);
        joinExitTransportBtn=findViewById(R.id.join_exit_update_transport);
        btnExitTransport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentOpinionOfUser.equals(oldOpinionOfUser)){
                    finish();
                }
                else {
                    Intent intent = getIntent();
                    setResult(Activity.RESULT_OK,intent);
                    finish();
                }

            }
        });
        typeSpinner.setAdapter(spinnerAdapter);
        departureLocationEditText = findViewById(R.id.departure_form_transport);
        reachLocationEditText = findViewById(R.id.reach_form_transport);

        dateDepartureEditText = findViewById(R.id.date_departure_form_transport);

        timeDepartureEditText = findViewById(R.id.time_departure_form_transport);
        timeReachEditText = findViewById(R.id.time_reach_form_transport);

        titleTextView = findViewById(R.id.title_new_update_transport);
        nameTripTextView=findViewById(R.id.name_trip_new_update_transport);

        nameWrapper=findViewById(R.id.name_form_transport_wrapper);
        departureWrapper=findViewById(R.id.departure_form_transport_wrapper);
        reachWrapper=findViewById(R.id.reach_form_transport_wrapper);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        View linearLayoutFormCreateTrip = findViewById(R.id.LinearLayoutFormCreateTransport);
        linearLayoutParticipants=findViewById(R.id.LinearLayout_participants_update_transport);
        linearLayoutFormCreateTrip.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(nameEditText.getWindowToken(), 0);
                return true;
            }
        });

        linearLayoutForm=findViewById(R.id.LinearLayoutFormCreateTransport);
        linearLayoutProgressBar=findViewById(R.id.LinearLayoutProgressBarCreateTransport);

        transportService = new TransportService();
        tripService = new TripService();
        btnCreate = findViewById(R.id.btn_create_form_transport);
        btnUpdate = findViewById(R.id.btn_update_form_transport);
        setListnerToEditTexts();
        setEventsToFieldDepartureDate();
        setEventsToFieldDepartureTime();
        final Intent intent = getIntent();

        nameTrip=intent.getStringExtra(ListTransportActivity.NAME_TRIP_LIST_TRANSPORT);
        nameTripTextView.setText(nameTrip);
        if (intent.getStringExtra(ID_TRIP_NEW_TRANSPORT) != null) {
            linearLayoutParticipants.setVisibility(View.GONE);
            idTrip = intent.getStringExtra(ID_TRIP_NEW_TRANSPORT);
            btnUpdate.setVisibility(View.GONE);
            linearLayoutProgressBar.setVisibility(View.GONE);
            linearLayoutForm.setVisibility(View.VISIBLE);
            joinExitTransportBtn.setVisibility(View.GONE);
            titleTextView.setText(getResources().getString(R.string.create_transport_title));
        } else {
            idTrip = intent.getStringExtra(ID_TRIP_UPDATE_TRANSPORT);
            final RecyclerView recyclerView=findViewById(R.id.recycler_view_particpants_update_transport);
            participantAdapter=new ParticipantAdapter();
            recyclerView.setAdapter(participantAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));

            joinExitTransportBtn.setVisibility(View.VISIBLE);
            btnCreate.setVisibility(View.GONE);
            idTransport=intent.getStringExtra(ID_TRANSPORT_NEW_TRANSPORT);
            setListnerToBtnJoinExitTransport();
            titleTextView.setText(getResources().getString(R.string.update_transport_title));
            fillFields();
        }

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Transport transport = checkFields();
                if (transport != null) {
                    transportService.addTransport(transport, new ResultAsynchTaskTransportAdded() {
                        @Override
                        public void onTransportAdded() {
                            tripService.notifyParticipants( idTrip,currentUser.getUid());
                            Intent intent = getIntent();
                            setResult(Activity.RESULT_OK,intent);
                            finish();
                        }
                    });
                }
            }
        });
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Transport transport=checkFields();
                transportService.updateTransport(idTransport,transport);
                tripService.notifyParticipants( idTrip,currentUser.getUid());

                Intent intent = getIntent();
                setResult(Activity.RESULT_OK,intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed(){
        if(currentOpinionOfUser.equals(oldOpinionOfUser)){
            finish();
        }
        else {
            Intent intent = getIntent();
            setResult(Activity.RESULT_OK,intent);
            finish();
        }
    }

    private void fillFields() {
        linearLayoutForm.setVisibility(View.GONE);
        linearLayoutProgressBar.setVisibility(View.VISIBLE);

        transportService.getTransportWithID(idTransport, new ResultAsynchronTaskOneTransport() {
            @Override
            public void onResponseReceived(Transport transport) {
                nameEditText.setText(transport.getName());
                departureLocationEditText.setText(transport.getDeparture());
                reachLocationEditText.setText(transport.getReach());
                String dateStr="";
                if(transport.getDepartureDate()!=null){
                    dateStr=ManipulateDate.getDateFormatFrench(transport.getDepartureDate().toGMTString());
                }
                String timeDepartureStr="";
                if(transport.getTimeDeparture()!=null){
                    timeDepartureStr=ManipulateDate.getFormatTime(transport.getTimeDeparture().getHours(),transport.getTimeDeparture().getMinutes());
                }
                String timeReachStr="";
                if(transport.getTimeReach()!=null){
                    timeReachStr=ManipulateDate.getFormatTime(transport.getTimeReach().getHours(),transport.getTimeReach().getMinutes());
                }

                dateDepartureEditText.setText(dateStr);
                timeDepartureEditText.setText(timeDepartureStr);
                timeReachEditText.setText(timeReachStr);
                typeSpinner.setText(transport.getType());
                linearLayoutProgressBar.setVisibility(View.GONE);
                linearLayoutForm.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNoResponseReceived() {

            }
        });
    }

    public void setListnerToBtnJoinExitTransport(){
        final String joinTransport=getResources().getString(R.string.join_transport_opinion);
        final String exitTransport=getResources().getString(R.string.exit_transport);
        transportService.getTransportWithID(idTransport, new ResultAsynchronTaskOneTransport() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onResponseReceived(final Transport transport) {
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
                                    linearLayoutForm.setVisibility(View.VISIBLE);
                                }
                            }
                        });
                        nbUsers++;
                    }
                }
                else {
                    linearLayoutParticipants.setVisibility(View.GONE);
                    linearLayoutProgressBar.setVisibility(View.GONE);
                    linearLayoutForm.setVisibility(View.VISIBLE);
                }

                if(transport.getUsersParticipations().containsKey(currentUser.getUid())){
                    joinExitTransportBtn.setText(transport.getUsersParticipations().get(currentUser.getUid()));
                    currentOpinionOfUser=transport.getUsersParticipations().get(currentUser.getUid());
                    oldOpinionOfUser=currentOpinionOfUser;
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
                    currentOpinionOfUser=joinTransport;
                    oldOpinionOfUser=currentOpinionOfUser;
                    joinExitTransportBtn.setText(joinTransport);
                    joinExitTransportBtn.setBackgroundColor(getResources().getColor(R.color.color_participer_transport));
                }
                joinExitTransportBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(transport.getUsersParticipations().containsKey(currentUser.getUid())){
                            if(transport.getUsersParticipations().get(currentUser.getUid()).equals(joinTransport)){
                                transport.getUsersParticipations().put(currentUser.getUid(),exitTransport);
                                joinExitTransportBtn.setText(joinTransport);
                                joinExitTransportBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.color_participer_transport)));
                                currentOpinionOfUser=exitTransport;

                            }
                            else {
                                joinExitTransportBtn.setText(exitTransport);
                                joinExitTransportBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.color_quitter_transport)));
                                transport.getUsersParticipations().put(currentUser.getUid(),joinTransport);
                                currentOpinionOfUser=joinTransport;
                            }
                        }
                        else {
                            joinExitTransportBtn.setText(exitTransport);
                            joinExitTransportBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.color_quitter_transport)));
                            currentOpinionOfUser=joinTransport;
                            transport.getUsersParticipations().put(currentUser.getUid(),joinTransport);
                        }
                        transportService.updateOpinionOfParticipant(transport.getId(),transport.getUsersParticipations());
                    }
                });

            }

            @Override
            public void onNoResponseReceived() {

            }
        });
    }

    public void setListnerToEditTexts(){
        /*typeSpinner.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b && TextUtils.isEmpty(typeSpinner.getText())){
                    nameWrapper.setErrorEnabled(true);
                    nameWrapper.setError(getResources().getString(R.string.error_entitled_required_create_activity));
                }
            }
        });*/

        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length()>LENGTH_MAX_FIELD_NAME){
                    nameWrapper.setErrorEnabled(true);
                    nameWrapper.setError(getResources().getString(R.string.error_length_entitled_housing_create_activity));
                }
                else {
                    nameWrapper.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        departureLocationEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length()>LENGTH_MAX_FIELD_DEPARTURE){
                    departureWrapper.setErrorEnabled(true);
                    departureWrapper.setError(getResources().getString(R.string.error_length_entitled_housing_create_activity));
                }
                else {
                    departureWrapper.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        reachLocationEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length()>LENGTH_MAX_FIELD_REACH){
                    reachWrapper.setErrorEnabled(true);
                    reachWrapper.setError(getResources().getString(R.string.error_length_entitled_housing_create_activity));
                }
                else {
                    reachWrapper.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

    }

    public Transport checkFields(){
        String name=nameEditText.getText().toString();
        /*if (TextUtils.isEmpty(name)) {
            Toast toast = Toast.makeText(this.getApplicationContext(), getResources().getString(R.string.error_entitled_required_create_activity), Toast.LENGTH_LONG);
            toast.show();
            nameEditText.requestFocus(); //donner le focus au champ Nom
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); //afficher le clavier
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
            return null;
        }*/
        if (TextUtils.isEmpty(typeSpinner.getText())) {
            Toast toast = Toast.makeText(this.getApplicationContext(), getResources().getString(R.string.error_type_required), Toast.LENGTH_LONG);
            toast.show();
            typeSpinner.requestFocus(); //donner le focus au champ Nom
            //InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); //afficher le clavier
            //imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
            return null;
        }
        else if(name.length()>LENGTH_MAX_FIELD_NAME){
            Toast toast = Toast.makeText(this.getApplicationContext(), getResources().getString(R.string.msg_activity_entitled_length_exceed), Toast.LENGTH_LONG);
            toast.show();
            nameEditText.requestFocus(); //donner le focus au champ Nom
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); //afficher le clavier
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
            return null;
        }
        String departureLocation=departureLocationEditText.getText().toString();
        if(departureLocation.length()>LENGTH_MAX_FIELD_DEPARTURE){
            Toast toast = Toast.makeText(this.getApplicationContext(), getResources().getString(R.string.msg_activity_entitled_length_exceed), Toast.LENGTH_LONG);
            toast.show();
            departureLocationEditText.requestFocus(); //donner le focus au champ Nom
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); //afficher le clavier
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
            return null;
        }
        String reachLocation=reachLocationEditText.getText().toString();
        if(reachLocation.length()>LENGTH_MAX_FIELD_REACH){
            Toast toast = Toast.makeText(this.getApplicationContext(), getResources().getString(R.string.msg_activity_entitled_length_exceed), Toast.LENGTH_LONG);
            toast.show();
            reachLocationEditText.requestFocus(); //donner le focus au champ Nom
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); //afficher le clavier
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
            return null;
        }
        Date dateDeparture=null;
        if(!TextUtils.isEmpty(dateDepartureEditText.getText())) {
            dateDeparture= ManipulateDate.strTodDate(dateDepartureEditText.getText().toString());
        }
        String type=typeSpinner.getText().toString();
        Time timeDeparture=null;
        if(!TextUtils.isEmpty(timeDepartureEditText.getText())) {
            timeDeparture = Time.valueOf(timeDepartureEditText.getText().toString() + SECONDS_TO_ADD);
        }
        Time timeReach=null;
        if (!TextUtils.isEmpty(timeReachEditText.getText())) {
            timeReach = Time.valueOf(timeReachEditText.getText().toString() + SECONDS_TO_ADD);
        }

        Transport transport=new Transport(name,departureLocation,reachLocation,dateDeparture,timeDeparture,timeReach,idTrip,type,currentUser.getUid());
        return transport;
    }


    private void setEventsToFieldDepartureDate(){
        final Calendar calendar = Calendar.getInstance();
        final long currentTime=calendar.getTimeInMillis();
        dateDepartureEditText.setInputType(InputType.TYPE_NULL);
        dateDepartureEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    showCalender(dateDepartureEditText, currentTime,onStartDateChangeLinstener);
                }
            }
        });
        dateDepartureEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCalender(dateDepartureEditText,currentTime,onStartDateChangeLinstener);
            }
        });
    }

    DatePickerDialog.OnDateSetListener onStartDateChangeLinstener=new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateDepartureEditText.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
        }
    };

    private void showCalender(final EditText editText, long minDate, DatePickerDialog.OnDateSetListener onDateSetListener){
        DatePickerDialog picker;
        int day;
        int month;
        int year;

        int[] date = ManipulateDate.parseDate(editText.getText().toString());
        day = date[0];
        month =date[1];
        year = date[2];
        picker = new DatePickerDialog(NewTransportActivity.this,R.style.DialogDateTheme, onDateSetListener, year, month, day);
        picker.getDatePicker().setMinDate(minDate);
        picker.show();
    }


    private void showTime(TimePickerDialog.OnTimeSetListener listener,EditText editText){

        TimePickerDialog timePicker;
        if (TextUtils.isEmpty(editText.getText())) {
            timePicker= new TimePickerDialog(NewTransportActivity.this,listener,0,0,true);
        }
        else {
            Time time=Time.valueOf(editText.getText().toString()+SECONDS_TO_ADD);
            timePicker= new TimePickerDialog(NewTransportActivity.this,listener,time.getHours(),time.getMinutes(),true);
        }

        timePicker.show();
    }

    TimePickerDialog.OnTimeSetListener onTimeDepartureSetListener=new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

           timeDepartureEditText.setText(ManipulateDate.getFormatTime(hourOfDay,minute));
        }
    };

    TimePickerDialog.OnTimeSetListener onTimeReachSetListener=new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            timeReachEditText.setText(ManipulateDate.getFormatTime(hourOfDay,minute));
        }
    };

    private void setEventsToFieldDepartureTime(){

        timeDepartureEditText.setInputType(InputType.TYPE_NULL);
        timeDepartureEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    showTime(onTimeDepartureSetListener,timeDepartureEditText);
                }
            }
        });
        timeDepartureEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTime(onTimeDepartureSetListener,timeDepartureEditText);
            }
        });


        timeReachEditText.setInputType(InputType.TYPE_NULL);
        timeReachEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    showTime(onTimeReachSetListener,timeReachEditText);
                }
            }
        });
        timeReachEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTime(onTimeReachSetListener,timeReachEditText);
            }
        });
    }




}
