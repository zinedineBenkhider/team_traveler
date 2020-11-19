package com.example.teamtraveler.presentation.housing;
import android.app.Activity;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.teamtraveler.R;
import com.example.teamtraveler.Utils.LockScreenActivity;
import com.example.teamtraveler.Utils.ParseWebPageAirBnb;
import com.example.teamtraveler.data.api.services.HousingService;
import com.example.teamtraveler.data.api.services.TripService;
import com.example.teamtraveler.data.api.services.resultAsynchTaskTrip.ResultAsynchronTaskOneTrip;
import com.example.teamtraveler.data.entities.Housing;
import com.example.teamtraveler.data.entities.Trip;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewHousingActivity extends LockScreenActivity {
    public final static String ID_TRIP_NEW_HOUSING="com.example.teamtraveler.presentation.housing.NewHousingActivity.ID_TRIP_NEW_HOUSING";
    private HousingService housingService;
    private TripService tripService;
    private EditText nameEditText,priceEditText, nbRoomEditText,nbBedEditText, nbBathRoomEditText,othersEditText,linkEditText;
    private TextInputLayout nameWrapper,priceWrapper,nbRoomWrapper,nbBathRoomWrapper,othersWrapper,nbBedWrapper;
    private CheckBox wifiCheckBox,garageCheckBox,gardinCheckBox,swimmingPoolCheckBox,chickenCheckBox,climatisationCheckBox;
    private Button btnCreate;
    private ImageButton backButton;
    private String pathAirBnb=null;
    private LinearLayout linearLayoutNewHousing,linearLayoutProgressBar,linearLayoutActivity;
    private String idTrip;
    final static int LENGTH_MAX_FIELD_OTHERS=80;
    final static int LENGTH_MAX_FIELD_NAME=50;
    private FirebaseAuth firebaseAuth;
    private MaterialBetterSpinner typeSpinner;
    private TextView nameTripTextView;
    final List<String> TYPES_SPINNER_DATA = new ArrayList<>();
    final List<String> idsOfTripsNames=new ArrayList<>();
    private String nameTrip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_housing);
        typeSpinner = findViewById(R.id.type_spinner_form_housing);
        nameTripTextView=findViewById(R.id.name_trip_new_housing);
        Intent intentExtra=getIntent();
        pathAirBnb=intentExtra.getStringExtra(Intent.EXTRA_TEXT);

        linearLayoutActivity=findViewById(R.id.activity_new_housing);
        nameEditText=findViewById(R.id.name_form_housing);
        nbBathRoomEditText=findViewById(R.id.nb_bathroom_form_housing);
        nbRoomEditText=findViewById(R.id.nb_room_form_housing);
        othersEditText=findViewById(R.id.others_form_housing);
        priceEditText=findViewById(R.id.price_form_housing);
        nbBedEditText=findViewById(R.id.nb_bed_form_housing);
        linkEditText=findViewById(R.id.link_form_new_housing);

        nameWrapper=findViewById(R.id.name_form_housing_wrapper);
        priceWrapper=findViewById(R.id.price_form_housing_wrapper);
        nbRoomWrapper=findViewById(R.id.nb_room_form_housing_wrapper);
        nbBathRoomWrapper=findViewById(R.id.nb_bathroom_form_housing_wrapper);
        nbBedWrapper=findViewById(R.id.nb_bed_form_housing_wrapper);
        othersWrapper=findViewById(R.id.description_form_housing_wrapper);

        linearLayoutNewHousing=findViewById(R.id.LinearLayout_new_housing);
        linearLayoutProgressBar=findViewById(R.id.LinearLayout_progress_bar_new_housing);

        wifiCheckBox=findViewById(R.id.checkbox_wifi_new_housing);
        garageCheckBox=findViewById(R.id.checkbox_garage_new_housing);
        gardinCheckBox=findViewById(R.id.checkbox_gardin_new_housing);
        swimmingPoolCheckBox=findViewById(R.id.checkbox_swimming_pool_new_housing);
        chickenCheckBox=findViewById(R.id.checkbox_chicken_new_housing);
        climatisationCheckBox=findViewById(R.id.checkbox_climatisation_new_housing);
        firebaseAuth=FirebaseAuth.getInstance();

        View linearLayoutFormCreateTrip=findViewById(R.id.LinearLayout_new_housing);
        linearLayoutFormCreateTrip.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(nameEditText.getWindowToken(), 0);
                return true;
            }
        });

        setListnerToEditTexts();
        Intent intent=getIntent();
        housingService=new HousingService();
        tripService=new TripService();
        idTrip=intent.getStringExtra(ID_TRIP_NEW_HOUSING);
        nameTrip=intent.getStringExtra(ListHousingActivity.NAME_TRIP_LIST_HOUSING);
        nameTripTextView.setText(nameTrip);
        typeSpinner.setVisibility(View.GONE);

        if(pathAirBnb!=null) {
            typeSpinner.setVisibility(View.VISIBLE);
            linearLayoutNewHousing.setVisibility(View.GONE);
            linearLayoutProgressBar.setVisibility(View.VISIBLE);
            linkEditText.setText(pathAirBnb);
            nameTripTextView.setText("Partager via AirBnb");
            initFields();
        }

        backButton = findViewById(R.id.button_back_new_housing);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnCreate=findViewById(R.id.btn_create_form_housing);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Housing housing=checkFields();
                if (housing!=null) {
                    housingService.addHousing(housing);
                    tripService.addHousingToTrip(idTrip, housing.getId());
                    Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.msg_housing_saved_newhousing), Toast.LENGTH_LONG);

                    toast.show();
                    tripService.notifyParticipants( idTrip,firebaseAuth.getCurrentUser().getUid());//notification
                        Intent intent = new Intent(view.getContext(), ListHousingActivity.class);
                        intent.putExtra(ListHousingActivity.ID_TRIP_LIST_HOUSING,idTrip);
                        intent.putExtra(ListHousingActivity.NAME_TRIP_LIST_HOUSING,nameTrip);
                        intent.putExtra(ListHousingActivity.FROM_AIR_BNB_LIST_HOUSING,true);
                        startActivity(intent);
                        finish();

                }
            }
        });
    }


    public void setListnerToEditTexts(){
        typeSpinner.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b && TextUtils.isEmpty(typeSpinner.getText())){
                    typeSpinner.setError(getResources().getString(R.string.error_name__trip_required_create_housing));
                }
            }
        });
        typeSpinner.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                idTrip=idsOfTripsNames.get(TYPES_SPINNER_DATA.indexOf(charSequence.toString()));
                nameTrip=charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
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
        nbBedEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    if(!TextUtils.isEmpty(charSequence.toString())){
                        Integer.valueOf(charSequence.toString());
                    }
                    nbBedWrapper.setErrorEnabled(false);
                }
                catch (Exception e){
                    nbBedWrapper.setErrorEnabled(true);
                    nbBedWrapper.setError(getResources().getString(R.string.msg_field_must_be_integer_housing));
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
                    othersWrapper.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


    }

    public void initFields(){
        tripService.getTripsInProgressOfParticipant(firebaseAuth.getCurrentUser().getUid(), new ResultAsynchronTaskOneTrip() {
            @Override
            public void onResponseReceived(Trip response) {
                TYPES_SPINNER_DATA.add(response.getName());
                idsOfTripsNames.add(response.getId());

            }

            @Override
            public void onAllResponseReceived() {
                Object[] data=TYPES_SPINNER_DATA.toArray();
                String[] strArray=new String[data.length];
                System.arraycopy(data, 0, strArray, 0, data.length);
                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(NewHousingActivity.this, android.R.layout.simple_dropdown_item_1line, strArray);
                typeSpinner.setAdapter(spinnerAdapter);
            }

            @Override
            public void onNoResponseReceived() {


            }
        });

      new GetTitleOfHousingTask().execute();
      new GetInfosOfHousingTask().execute();
      new GetEquipementsOfHousingTask().execute();
    }

    class GetTitleOfHousingTask extends AsyncTask<Void,String,String>
    {
        @Override
        protected String doInBackground(Void... arg0) {
            String title="";
            try {
                ParseWebPageAirBnb parseWebPageAirBnb=new ParseWebPageAirBnb(pathAirBnb);

                title= parseWebPageAirBnb.getTitle();

            }
            catch (Exception e){

            }

            return title;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            nameEditText.setText(result);
        }
    }
    class GetInfosOfHousingTask extends AsyncTask<Void,Map<String,String>,Map<String,String>>
    {

        @Override
        protected Map<String,String> doInBackground(Void... arg0) {
            Map<String,String> infos=new HashMap<>();
            try {
                ParseWebPageAirBnb parseWebPageAirBnb=new ParseWebPageAirBnb(pathAirBnb);

                infos= parseWebPageAirBnb.getInfos();

            }
            catch (Exception e){

            }

            return infos;
        }

        @Override
        protected void onPostExecute(Map<String,String> result) {
            super.onPostExecute(result);
            nbRoomEditText.setText(result.get("Chambres"));
            result.get("Voyageurs");
            nbBedEditText.setText(result.get("Lits"));
            nbBathRoomEditText.setText(result.get("SallesDeBain"));
        }
    }
    class GetEquipementsOfHousingTask extends AsyncTask<Void,List<String>,List<String>>
    {

        @Override
        protected List<String> doInBackground(Void... arg0) {
            List<String> equipements=new ArrayList<>();
            try {
                ParseWebPageAirBnb parseWebPageAirBnb=new ParseWebPageAirBnb(pathAirBnb);

                equipements= parseWebPageAirBnb.getEquipements();

            }
            catch (Exception e){

            }

            return equipements;
        }

        @Override
        protected void onPostExecute(List<String> result) {
            super.onPostExecute(result);
            for (int i=0;i<result.size();i++){
                if(result.get(i).equals(wifiCheckBox.getText())){
                    wifiCheckBox.setChecked(true);
                }
                else if(result.get(i).equals(swimmingPoolCheckBox.getText())){
                    swimmingPoolCheckBox.setChecked(true);
                }
                else if(result.get(i).equals(gardinCheckBox.getText())){
                    swimmingPoolCheckBox.setChecked(true);
                }
                else if (result.get(i).equals(chickenCheckBox.getText())){
                    chickenCheckBox.setChecked(true);
                }
                else if (result.get(i).equals(climatisationCheckBox.getText())){
                    climatisationCheckBox.setChecked(true);
                }
                else {
                    if (TextUtils.isEmpty(othersEditText.getText())){
                        othersEditText.setText(result.get(i));
                    }
                    else{
                        othersEditText.setText(othersEditText.getText()+", "+result.get(i));
                    }
                }
            }
            linearLayoutNewHousing.setVisibility(View.VISIBLE);
            linearLayoutProgressBar.setVisibility(View.GONE);
        }
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
        if (TextUtils.isEmpty(typeSpinner.getText()) && pathAirBnb!=null){
            Toast toast = Toast.makeText(this.getApplicationContext(), getResources().getString(R.string.name_trip_is_required_create_housing), Toast.LENGTH_LONG);
            toast.show();
            typeSpinner.requestFocus();
            return null;
        }

        String nbRoom="";
        String price="";
        String nbBathRoom="";
        String nbBed="";
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
        try {
            nbBed=nbBedEditText.getText().toString();
            if(!TextUtils.isEmpty(nbBed)) {
                Integer.valueOf(nbBedEditText.getText().toString());
            }
        }
        catch (Exception e){
            Toast toast = Toast.makeText(this.getApplicationContext(), getResources().getString(R.string.msg_field_must_be_integer_housing), Toast.LENGTH_LONG);
            toast.show();
            nbBedEditText.requestFocus();
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
        boolean haveClimatisation=climatisationCheckBox.isChecked();
        boolean haveChicken=chickenCheckBox.isChecked();
        String link=linkEditText.getText().toString();
        Housing housing=new Housing(firebaseAuth.getCurrentUser().getUid(),name, nbRoom,nbBathRoom,others,idTrip, haveSwimmingPool, haveGarage, haveGardin, haveWifi);
        housing.setHaveChicken(haveChicken);
        housing.setHaveClimatisation(haveClimatisation);
        housing.setNbBed(nbBed);
        housing.setLink(link);
        if(!TextUtils.isEmpty(price)){
            housing.setPrice(Integer.valueOf(price));
        }
        return housing;
    }

}

