package com.example.teamtraveler.presentation.trip;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.teamtraveler.R;
import com.example.teamtraveler.Utils.LockScreenActivity;
import com.example.teamtraveler.Utils.ManipulateDate;
import com.example.teamtraveler.data.api.services.resultAsynchTaskTrip.ResultAsynchronTaskOneTrip;
import com.example.teamtraveler.data.entities.Trip;
import com.example.teamtraveler.data.api.services.TripService;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.Calendar;
import java.util.Date;

public class NewTripActivity extends LockScreenActivity {

    private TripService tripService;
    private Button btnCreateTrip,btnUpdateTrip;
    private EditText tripNameEditTxt,tripPlaceEditTxt,tripStartDateEditTxt,tripEndDateEditTxt;
    private FirebaseAuth firebaseAuth;
    private TextInputLayout tripNameWrapper;
    private TextInputLayout tripPlaceWrapper;
    private TextView titleTextView;
    private ImageButton backButton;
    public final static String ID_TRIP_NEW_TRIP="com.example.teamtraveler.presentation.createNewTrip.ID_TRIP_NEW_TRIP";
    public final static String RECHARGE_DATA_NEW_TRIP="com.example.teamtraveler.presentation.createNewTrip.RECHARGE_DATA_NEW_TRIP";
    public final static String RECHARGE_DATA_EDIT_TRIP="com.example.teamtraveler.presentation.createNewTrip.RECHARGE_DATA_EDIT_TRIP";
    private String idTrip;
    private LinearLayout linearLayoutForm,linearLayoutProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_trip);
        tripService =new TripService();
        btnCreateTrip = findViewById(R.id.btn_create_form_trip);
        btnUpdateTrip=findViewById(R.id.btn_update_form_trip);
        titleTextView=findViewById(R.id.title_new_trip);
        linearLayoutForm=findViewById(R.id.LinearLayout_form_new_Trip);
        linearLayoutProgressBar=findViewById(R.id.LinearLayout_progress_bar_new_trip);

        tripNameEditTxt = findViewById(R.id.name_form_trip);
        tripNameWrapper= (TextInputLayout) findViewById(R.id.name_form_trip_wrapper);
        tripPlaceWrapper = (TextInputLayout) findViewById(R.id.place_form_trip_wrapper);

        tripPlaceEditTxt=findViewById(R.id.place_form_trip);
        setListnerToEditTexts();
        tripStartDateEditTxt=findViewById(R.id.start_date_form_trip);
        tripEndDateEditTxt=findViewById(R.id.end_date_form_trip);
        setEventsToFieldStartDate();
        setEventsToFieldEndDate();
        firebaseAuth=FirebaseAuth.getInstance();
        backButton = findViewById(R.id.button_back_new_trip);
        Intent intent=getIntent();
        idTrip=intent.getStringExtra(ID_TRIP_NEW_TRIP);
        if (idTrip!=null){
            linearLayoutProgressBar.setVisibility(View.VISIBLE);
            linearLayoutForm.setVisibility(View.GONE);
            btnUpdateTrip.setVisibility(View.VISIBLE);
            titleTextView.setText(getResources().getString(R.string.title_update_trip));
            btnCreateTrip.setVisibility(View.GONE);

            tripService.getTripWithID(idTrip, new ResultAsynchronTaskOneTrip() {
                @Override
                public void onResponseReceived(Trip response) {
                    tripNameEditTxt.setText(response.getName());
                    tripPlaceEditTxt.setText(response.getLocation());
                    if(response.getStartDate()!=null){
                        tripStartDateEditTxt.setText(ManipulateDate.getDateFormatFrench(response.getStartDate().toString()));
                    }
                    if(response.getEndDate()!=null){
                        tripEndDateEditTxt.setText(ManipulateDate.getDateFormatFrench(response.getEndDate().toString()));
                    }
                    linearLayoutProgressBar.setVisibility(View.GONE);
                    linearLayoutForm.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAllResponseReceived() {

                }

                @Override
                public void onNoResponseReceived() {

                }
            });
        }
        else
        {
            linearLayoutProgressBar.setVisibility(View.GONE);
            linearLayoutForm.setVisibility(View.VISIBLE);
            btnUpdateTrip.setVisibility(View.GONE);
            btnCreateTrip.setVisibility(View.VISIBLE);
        }
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra(RECHARGE_DATA_NEW_TRIP,false);
                returnIntent.putExtra(DetailTripActivity.ID_TRIP_DETAIL,idTrip);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });

        View linearLayoutFormCreateTrip=findViewById(R.id.LinearLayout_new_trip);
        linearLayoutFormCreateTrip.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(tripNameEditTxt.getWindowToken(), 0);
                return true;
            }
        });
        btnUpdateTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (firebaseAuth.getCurrentUser() != null) {
                    Toast toast = Toast.makeText(view.getContext(), getResources().getString(R.string.msg_trip_saved_newTrip), Toast.LENGTH_LONG);
                    toast.show();
                    final Trip trip = checkFieldsAndCreateTrip();
                    if(trip==null){
                        throw new NullPointerException();
                    }
                    trip.setId(idTrip);

                    tripService.updateTrip(trip);
                    Intent intent = new Intent(getApplicationContext(), DetailTripActivity.class);
                    intent.putExtra(DetailTripActivity.ID_TRIP_DETAIL,idTrip);
                    intent.putExtra(RECHARGE_DATA_EDIT_TRIP,true);
                    setResult(Activity.RESULT_OK,intent);
                    finish();


                }
            }
        });

        btnCreateTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Resources resources=getResources();
                if (firebaseAuth.getCurrentUser() != null) {
                    final Trip trip=checkFieldsAndCreateTrip();
                    if (trip != null) {

                        tripService.addTrip(trip);
                        Toast toast = Toast.makeText(view.getContext(), resources.getString(R.string.msg_trip_saved_newTrip), Toast.LENGTH_LONG);
                        toast.show();
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra(RECHARGE_DATA_NEW_TRIP,true);
                        setResult(Activity.RESULT_OK,returnIntent);
                        finish();
                    }
                }
                else{//si l'utilisateur est déconnecté
                    Toast toast = Toast.makeText(view.getContext(),resources.getString(R.string.msg_user_not_connected_newTrip) , Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });
    }
    
    private Trip checkFieldsAndCreateTrip() {
        String tripName = tripNameEditTxt.getText().toString();
        String tripPlace = tripPlaceEditTxt.getText().toString();
        String tripStartDateStr = tripStartDateEditTxt.getText().toString();
        String tripEndDateStr = tripEndDateEditTxt.getText().toString();
        Resources resources=getResources();
        Date tripEndDate = null;
        Date tripStartDate = null;
        if (TextUtils.isEmpty(tripName))  {
            Toast toast = Toast.makeText(this.getApplicationContext(), resources.getString(R.string.msg_trip_name_absent), Toast.LENGTH_LONG);
            toast.show();
            tripNameEditTxt.requestFocus(); //donner le focus au champ Nom
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); //afficher le clavier
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
            return null;
        }
        else if (tripName.length()>20){
            Toast toast = Toast.makeText(this.getApplicationContext(), resources.getString(R.string.msg_trip_name_length_exceed), Toast.LENGTH_LONG);
            toast.show();
            tripNameEditTxt.requestFocus(); //donner le focus au champ Nom
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); //afficher le clavier
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
            return null;

        }
        else if (tripPlace.length()>20){
            Toast toast = Toast.makeText(this.getApplicationContext(), resources.getString(R.string.msg_trip_place_length_exceed), Toast.LENGTH_LONG);
            toast.show();
            tripPlaceEditTxt.requestFocus(); //donner le focus au champ Nom
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); //afficher le clavier
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
            return null;

        }
        else {
            tripStartDate =ManipulateDate.strTodDate(tripStartDateStr);
            tripEndDate = ManipulateDate.strTodDate(tripEndDateStr);
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            Trip trip = new Trip(tripName, tripPlace, tripStartDate, tripEndDate, currentUser.getUid());
            return trip;
        }
    }



    public void setListnerToEditTexts(){
        tripNameEditTxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b && TextUtils.isEmpty(tripNameEditTxt.getText())){
                    tripNameWrapper.setErrorEnabled(true);
                    tripNameWrapper.setError(getResources().getString(R.string.error_name_required_create_trip));

                }
            }
        });
        tripNameEditTxt.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length()>20){
                    tripNameWrapper.setErrorEnabled(true);
                    tripNameWrapper.setError(getResources().getString(R.string.error_lenth_name_trip_create_trip));
                }
                else {
                    tripNameWrapper.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        tripPlaceEditTxt.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length()>20){
                    tripPlaceWrapper.setErrorEnabled(true);
                    tripPlaceWrapper.setError(getResources().getString(R.string.error_lenth_name_trip_create_trip));
                }
                else {
                    tripPlaceWrapper.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
    DatePickerDialog.OnDateSetListener onEndDateChangeListener=new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            tripEndDateEditTxt.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
        }
    };
    DatePickerDialog.OnDateSetListener onStartDateChangeLinstener=new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            tripStartDateEditTxt.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
            String startDateStr=tripStartDateEditTxt.getText().toString();
            String endDateStr=tripEndDateEditTxt.getText().toString();
            if (!TextUtils.isEmpty(endDateStr)) {
                Date startDate=ManipulateDate.strTodDate(startDateStr);
                Date endDate=ManipulateDate.strTodDate(endDateStr);
                if (endDate.before(startDate)){//si la date de fin est inférieur à la date de début on lui attribut la date de début
                    tripEndDateEditTxt.setText(startDateStr);
                }
            }
        }
    };


    /**
     * Define the end date of a trip
     */
    private void setEventsToFieldEndDate(){
        final Calendar calendar = Calendar.getInstance();
        final long currentTime=calendar.getTimeInMillis();
        tripEndDateEditTxt.setInputType(InputType.TYPE_NULL);
        tripEndDateEditTxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    if (TextUtils.isEmpty(tripStartDateEditTxt.getText().toString())){
                        showCalender(tripEndDateEditTxt, currentTime,onEndDateChangeListener);
                    }
                    else {
                        long timeStartDate =ManipulateDate.getTimeOfDate(tripStartDateEditTxt.getText().toString());
                        showCalender(tripEndDateEditTxt, timeStartDate,onEndDateChangeListener);
                    }
                }
            }
        });
        tripEndDateEditTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(tripStartDateEditTxt.getText().toString())){
                    showCalender(tripEndDateEditTxt, currentTime,onEndDateChangeListener);
                }
                else {
                    long timeStartDate =ManipulateDate.getTimeOfDate(tripStartDateEditTxt.getText().toString());
                    showCalender(tripEndDateEditTxt, timeStartDate,onEndDateChangeListener);
                }
            }
        });
    }

    /**
     * Define the start date of a trip
     */
    private void setEventsToFieldStartDate(){
        final Calendar calendar = Calendar.getInstance();
        final long currentTime=calendar.getTimeInMillis();
        tripStartDateEditTxt.setInputType(InputType.TYPE_NULL);
        tripStartDateEditTxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    showCalender(tripStartDateEditTxt, currentTime,onStartDateChangeLinstener);
                }
            }
        });
        tripStartDateEditTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCalender(tripStartDateEditTxt,currentTime,onStartDateChangeLinstener);
            }
        });
    }


    private void showCalender(final EditText editText,long minDate,DatePickerDialog.OnDateSetListener onDateSetListener){
        DatePickerDialog picker;
        int day;
        int month;
        int year;
        int[] date = ManipulateDate.parseDate(editText.getText().toString());
        day = date[0];
        month =date[1];
        year = date[2];
        picker = new DatePickerDialog(NewTripActivity.this,R.style.DialogDateTheme, onDateSetListener, year, month, day);
        picker.getDatePicker().setMinDate(minDate);
        picker.show();
    }



}
