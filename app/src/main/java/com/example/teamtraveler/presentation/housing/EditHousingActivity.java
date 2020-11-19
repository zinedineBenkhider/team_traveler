package com.example.teamtraveler.presentation.housing;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.teamtraveler.R;
import com.example.teamtraveler.Utils.LockScreenActivity;
import com.example.teamtraveler.Utils.NoteAppreciationMapping;
import com.example.teamtraveler.data.api.services.HousingService;
import com.example.teamtraveler.data.api.services.TripService;
import com.example.teamtraveler.data.api.services.resultAsynchTaskHousing.ResultAsynchronTaskOneHousing;
import com.example.teamtraveler.data.entities.Housing;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EditHousingActivity extends LockScreenActivity {
    public final static String ID_HOUSING_EDIT_HOUSING="com.example.teamtraveler.presentation.housing.DetailHousingActivity.ID_HOUSING_EDIT_HOUSING";
    public final static String ID_TRIP_EDIT_HOUSING="com.example.teamtraveler.presentation.housing.DetailHousingActivity.ID_TRIP_EDIT_HOUSING";
    private EditText nameEditText,priceEditText,nbRoomEditText,nbBathRoomEditText,othersEditText,nbBedEditText, linkEditText;
    private TextView averageTextView,nameTripEditText,noteTextView;
    private TextInputLayout nameWrapper,priceWrapper,nbRoomWrapper,nbBathRoomWrapper,othersWrapper;
    private CheckBox wifiCheckBox,garageCheckBox,gardinCheckBox,swimmingPoolCheckBox,climatisationCheckBox, chickenCheckBox;
    private HousingService housingService;
    private TripService tripService;
    private FirebaseUser firebaseUser;
    private Button btnSave;
    private ImageButton backButton;
    private RatingBar ratingBarAverage,ratingBarNote;
    private LinearLayout linearLayoutEditHousing,linearLayoutProgressBar;
    final static int LENGTH_MAX_FIELD_OTHERS=80;
    final static int LENGTH_MAX_FIELD_NAME=50;
    private String nameTrip;
    private double oldNote,currentNote;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_housing);
        nameTripEditText=findViewById(R.id.name_trip_edit_housing);
        Intent intent=getIntent();
        final String housingId=intent.getStringExtra(ID_HOUSING_EDIT_HOUSING);
        final String tripId=intent.getStringExtra(ID_TRIP_EDIT_HOUSING);
        nameTrip=intent.getStringExtra(ListHousingActivity.NAME_TRIP_LIST_HOUSING);
        nameTripEditText.setText(nameTrip);
        housingService=new HousingService();
        tripService=new TripService();
        btnSave=findViewById(R.id.btn_save_form_edit_housing);
        ratingBarAverage=findViewById(R.id.rating_bar_average_housing_edit);
        ratingBarAverage.setEnabled(false);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        nameEditText=findViewById(R.id.name_form_edit_housing);
        priceEditText=findViewById(R.id.price_form_edit_housing);
        nbRoomEditText=findViewById(R.id.nb_room_form_edit_housing);
        nbBathRoomEditText=findViewById(R.id.nb_bathroom_form_edit_housing);
        othersEditText=findViewById(R.id.others_form_edit_housing);
        averageTextView=findViewById(R.id.average_housing_edit);
        nbBedEditText=findViewById(R.id.nb_bed_form_edit_housing);
        linkEditText=findViewById(R.id.link_form_edit_housing);
        ratingBarNote=findViewById(R.id.rating_bar_note_housing_detail);
        nameWrapper=findViewById(R.id.name_form_edit_housing_wrapper);
        priceWrapper=findViewById(R.id.price_form_edit_housing_wrapper);
        nbRoomWrapper=findViewById(R.id.nb_room_form_edit_housing_wrapper);
        nbBathRoomWrapper=findViewById(R.id.nb_bathroom_form_housing_edit_wrapper);
        othersWrapper=findViewById(R.id.description_form_edit_housing_wrapper);
        noteTextView=findViewById(R.id.note_housing_detail);
        wifiCheckBox=findViewById(R.id.checkbox_wifi_edit_housing);
        garageCheckBox=findViewById(R.id.checkbox_garage_edit_housing);
        gardinCheckBox=findViewById(R.id.checkbox_gardin_edit_housing);
        swimmingPoolCheckBox=findViewById(R.id.checkbox_swimming_pool_edit_housing);
        chickenCheckBox=findViewById(R.id.checkbox_chicken_edit_housing);
        climatisationCheckBox=findViewById(R.id.checkbox_climatisation_edit_housing);
        backButton = findViewById(R.id.backButton_housing_detail);
        linearLayoutEditHousing=findViewById(R.id.LinearLayout_edit_housing);
        linearLayoutProgressBar=findViewById(R.id.LinearLayout_progress_bar_edit_housing);
        linearLayoutEditHousing.setVisibility(View.GONE);
        linearLayoutProgressBar.setVisibility(View.VISIBLE);

        setListnerToEditTexts();
        View linearLayoutFormCreateTrip=findViewById(R.id.LinearLayout_edit_housing);
        linearLayoutFormCreateTrip.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(nameEditText.getWindowToken(), 0);
                return true;
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name=nameEditText.getText().toString();
                Housing newHousing =checkFields();
                if(newHousing!=null){
                    tripService.notifyParticipants( tripId,firebaseUser.getUid());//notification
                    housingService.updateHousing(housingId, newHousing);
                    Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.msg_updates_saved_edit_housing), Toast.LENGTH_LONG);
                    toast.show();
                    Intent intent = getIntent();
                    intent.putExtra(ListHousingActivity.NAME_TRIP_LIST_HOUSING,nameTrip);
                    intent.putExtra(ListHousingActivity.ID_TRIP_LIST_HOUSING, tripId);
                    setResult(Activity.RESULT_OK,intent);
                    finish();
                }
            }
        });

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
                nameEditText.setText(housing.getName());
                priceEditText.setText(String.valueOf(housing.getPrice()));
                nbBathRoomEditText.setText(housing.getNbBathRoom());
                nbRoomEditText.setText(housing.getNbRoom());
                othersEditText.setText(housing.getDescription());
                wifiCheckBox.setChecked(housing.isHaveWifi());
                garageCheckBox.setChecked(housing.isHaveGarage());
                gardinCheckBox.setChecked(housing.isHaveGardin());
                swimmingPoolCheckBox.setChecked(housing.isHaveSwimmingPool());
                chickenCheckBox.setChecked(housing.isHaveChicken());
                climatisationCheckBox.setChecked(housing.isHaveClimatisation());
                nbBedEditText.setText(housing.getNbBed());
                linkEditText.setText(housing.getLink());

                ratingBarAverage.setRating(housing.getAverageNote());
                try {
                    float noteOfUser=housing.getUsersNotes().get(firebaseUser.getUid());
                    oldNote=noteOfUser;
                    currentNote=oldNote;
                    ratingBarNote.setRating(noteOfUser);
                    noteTextView.setText("Note: "+noteOfUser+"/5 - "+ NoteAppreciationMapping.getAppreciationOfNote(noteOfUser));

                }
                catch (Exception e){
                    noteTextView.setText(getResources().getString(R.string.note_housing_not_specified));
                }

                if (housing.getUsersNotes().size()!=0){
                    averageTextView.setText("Moyenne: "+housing.getAverageNote()+"/5 sur "+housing.getUsersNotes().size() +" note(s) - "+ NoteAppreciationMapping.getAppreciationOfNote(housing.getAverageNote()));

                }
                else {
                    averageTextView.setText("Moyenne: "+housing.getAverageNote()+"/5 sur "+housing.getUsersNotes().size() +" note(s)");

                }
                linearLayoutEditHousing.setVisibility(View.VISIBLE);
                linearLayoutProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onNoResponseReceived() {

            }
        });

        ratingBarNote.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(final RatingBar ratingBar, float v, boolean b) {
                currentNote=ratingBarNote.getRating();
                housingService.updateNoteOfHousing(housingId,firebaseUser.getUid(), ratingBar.getRating(), new ResultAsynchronTaskOneHousing() {
                    @Override
                    public void onResponseReceived(Housing housing) {
                        ratingBarAverage.setEnabled(true);
                        ratingBarAverage.setRating(housing.getAverageNote());
                        ratingBarAverage.setEnabled(false);
                        tripService.notifyParticipants( tripId,firebaseUser.getUid());//notification
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
            Intent intent = new Intent();
            setResult(Activity.RESULT_OK,intent);
            finish();
        }
    }

    public void setListnerToEditTexts(){
        nameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b && TextUtils.isEmpty(nameEditText.getText())){
                    nameWrapper.setErrorEnabled(true);
                    nameWrapper.setError(getResources().getString(R.string.error_name_required_create_housing));
                }
            }
        });
        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length()>LENGTH_MAX_FIELD_NAME){
                    nameWrapper.setErrorEnabled(true);
                    nameWrapper.setError(getResources().getString(R.string.msg_housing_name_length_exceed));
                }
                else {
                    nameWrapper.setErrorEnabled(false);
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
                    if(!TextUtils.isEmpty(charSequence.toString())){
                        Integer.valueOf(charSequence.toString());
                    }
                    priceWrapper.setErrorEnabled(false);
                }
                catch (Exception e){
                    priceWrapper.setErrorEnabled(true);
                    priceWrapper.setError(getResources().getString(R.string.msg_field_must_be_integer_housing));
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        nbBathRoomEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    if(!TextUtils.isEmpty(charSequence.toString())){
                        Integer.valueOf(charSequence.toString());
                    }
                    nbBathRoomWrapper.setErrorEnabled(false);
                }
                catch (Exception e){
                    nbBathRoomWrapper.setErrorEnabled(true);
                    nbBathRoomWrapper.setError(getResources().getString(R.string.msg_field_must_be_integer_housing));
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


        nbRoomEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    if(!TextUtils.isEmpty(charSequence.toString())){
                        Integer.valueOf(charSequence.toString());
                    }
                    nbRoomWrapper.setErrorEnabled(false);
                }
                catch (Exception e){
                    nbRoomWrapper.setErrorEnabled(true);
                    nbRoomWrapper.setError(getResources().getString(R.string.msg_field_must_be_integer_housing));
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        othersEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length()>LENGTH_MAX_FIELD_OTHERS){
                    othersWrapper.setErrorEnabled(true);
                    othersWrapper.setError(getResources().getString(R.string.error_length_field_others_exceed_housing));
                }
                else {
                    nameWrapper.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

    }

    public Housing checkFields(){
        String name=nameEditText.getText().toString();
        if (TextUtils.isEmpty(name)) {
            Toast toast = Toast.makeText(this.getApplicationContext(), getResources().getString(R.string.error_name_required_create_housing), Toast.LENGTH_LONG);
            toast.show();
            nameEditText.requestFocus(); //donner le focus au champ Nom
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); //afficher le clavier
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
            return null;
        }
        else if(name.length()>LENGTH_MAX_FIELD_NAME){
            Toast toast = Toast.makeText(this.getApplicationContext(), getResources().getString(R.string.msg_housing_name_length_exceed), Toast.LENGTH_LONG);
            toast.show();
            nameEditText.requestFocus(); //donner le focus au champ Nom
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); //afficher le clavier
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
            return null;
        }

        String nbRoom="";
        String price="";
        String nbBathRoom="";
        try {
            price=priceEditText.getText().toString();
            if(!TextUtils.isEmpty(price)) {
                Integer.valueOf(priceEditText.getText().toString());
            }
        }
        catch (Exception e){
            Toast toast = Toast.makeText(this.getApplicationContext(), getResources().getString(R.string.msg_field_must_be_integer_housing), Toast.LENGTH_LONG);
            toast.show();
            priceEditText.requestFocus();
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); //afficher le clavier
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
            return null;
        }
        try {
            nbRoom=nbRoomEditText.getText().toString();
            if(!TextUtils.isEmpty(nbRoom)) {
                Integer.valueOf(nbRoomEditText.getText().toString());
            }
        }
        catch (Exception e){
            Toast toast = Toast.makeText(this.getApplicationContext(), getResources().getString(R.string.msg_field_must_be_integer_housing), Toast.LENGTH_LONG);
            toast.show();
            nbRoomEditText.requestFocus(); //donner le focus au champ Nom
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); //afficher le clavier
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
            return null;
        }
        try {
            nbBathRoom=nbBathRoomEditText.getText().toString();
            if(!TextUtils.isEmpty(nbBathRoom)) {
                Integer.valueOf(nbBathRoomEditText.getText().toString());
            }
        }
        catch (Exception e){
            Toast toast = Toast.makeText(this.getApplicationContext(), getResources().getString(R.string.msg_field_must_be_integer_housing), Toast.LENGTH_LONG);
            toast.show();
            nbBathRoomEditText.requestFocus();
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); //afficher le clavier
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
            return null;
        }
        String others=othersEditText.getText().toString();
        if(others.length()>LENGTH_MAX_FIELD_OTHERS){
            Toast toast = Toast.makeText(this.getApplicationContext(), getResources().getString(R.string.msg_housing_others_length_exceed), Toast.LENGTH_LONG);
            toast.show();
            othersEditText.requestFocus(); //donner le focus au champ Nom
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); //afficher le clavier
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
            return null;
        }
        boolean haveSwimmingPool=swimmingPoolCheckBox.isChecked();
        boolean haveGarage =garageCheckBox.isChecked();
        boolean haveGardin=gardinCheckBox.isChecked();
        boolean haveWifi=wifiCheckBox.isChecked();
        boolean haveChicken=chickenCheckBox.isChecked();
        boolean haveClimatistion=climatisationCheckBox.isChecked();
        String nbBad=nbBedEditText.getText().toString();
        String link=linkEditText.getText().toString();

        Housing newHousing = new Housing(name, nbRoom, nbBathRoom, others, haveSwimmingPool, haveGarage, haveGardin, haveWifi);
        newHousing.setNbBed(nbBad);
        newHousing.setHaveClimatisation(haveClimatistion);
        newHousing.setHaveChicken(haveChicken);
        newHousing.setLink(link);

        if(!TextUtils.isEmpty(price)){
            newHousing.setPrice(Integer.valueOf(price));
        }
        return newHousing;
    }





}
