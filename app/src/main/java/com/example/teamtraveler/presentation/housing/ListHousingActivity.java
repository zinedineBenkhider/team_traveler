package com.example.teamtraveler.presentation.housing;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teamtraveler.R;
import com.example.teamtraveler.Utils.BaseActivity;
import com.example.teamtraveler.data.api.services.HousingService;
import com.example.teamtraveler.data.api.services.TripService;
import com.example.teamtraveler.data.api.services.resultAsynchTaskHousing.ResultAsynchronTaskOneHousing;
import com.example.teamtraveler.data.api.services.resultAsynchTaskTrip.ResultAsynchronTaskOneTrip;
import com.example.teamtraveler.data.entities.Housing;
import com.example.teamtraveler.data.entities.Trip;
import com.example.teamtraveler.presentation.trip.DetailTripActivity;
import com.example.teamtraveler.presentation.trip.SwipeController.SwipeHelper;
import com.example.teamtraveler.presentation.trip.SwipeController.SwipeInterface;
import com.example.teamtraveler.presentation.housing.SwipeController.dialog.DialogDeleteHousing;
import com.example.teamtraveler.presentation.housing.recyclerView.HousingActionInterface;
import com.example.teamtraveler.presentation.housing.recyclerView.HousingAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class ListHousingActivity extends BaseActivity implements HousingActionInterface {
    public final static String ID_TRIP_LIST_HOUSING = "com.example.teamtraveler.presentation.housing.ListHousingActivity.ID_TRIP_LIST_HOUSING";
    public static final String NAME_TRIP_LIST_HOUSING="com.example.teamtraveler.presentation.housing.ListHousingActivity.NAME_TRIP_LIST_HOUSING";
    public static final String FROM_AIR_BNB_LIST_HOUSING="com.example.teamtraveler.presentation.housing.ListHousingActivity.FROM_AIR_BNB_LIST_HOUSING";
        private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private HousingAdapter housingAdapter;
    private HousingService housingService;
    private FirebaseAuth firebaseAuth;
    private FloatingActionButton btnCreateHousing;
    private Button btnSortByPrice,btnSortByNote;
    private String nameTrip,idTrip;
    private TextView nameTripTextView;
    private ImageButton backImageButton;
    private int nbParticipants=-1;
    private TripService tripService;
    private TextView no_housing_yet;
    public final static int REQUEST_CODE_EDIT_HOUSING=213;
    private LinearLayout linearLayoutBtnsSortHousing;
    private SwipeHelper swipeHelper;
    private Boolean fromAirBnb;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_housing);
        recyclerView =findViewById(R.id.recycler_view_housing);
         backImageButton=findViewById(R.id.backButton_list_housing);
        linearLayoutBtnsSortHousing=findViewById(R.id.LinearLayout_sort_list_housing);
        linearLayoutBtnsSortHousing.setVisibility(View.GONE);
        this.tripService=new TripService();
        firebaseAuth=FirebaseAuth.getInstance();
        housingService=new HousingService();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        Toolbar toolbar = findViewById(R.id.list_housing_toolbar);
        setSupportActionBar(toolbar);
        progressBar=findViewById(R.id.progress_bar_housing);
        btnSortByPrice=findViewById(R.id.btn_sort_price_housing);
        btnSortByNote=findViewById(R.id.btn_sort_note_housing);
        nameTripTextView=findViewById(R.id.name_trip_list_housing);
        no_housing_yet = findViewById(R.id.no_housing_yet);

        Intent intent = getIntent();

        idTrip = intent.getStringExtra(ID_TRIP_LIST_HOUSING);
        nameTrip=intent.getStringExtra(NAME_TRIP_LIST_HOUSING);
        fromAirBnb=intent.getBooleanExtra(FROM_AIR_BNB_LIST_HOUSING,false);
        nameTripTextView.setText(nameTrip);

        btnSortByPrice.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                btnSortByNote.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.color_bg_btn_sort_clicked_housing)));
                btnSortByPrice.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.color_bg_btn_sort_not_clicked_housing)));
                sortByPriceRecyclerView(idTrip);
            }
        });

        btnSortByNote.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                btnSortByPrice.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.color_bg_btn_sort_clicked_housing)));
                btnSortByNote.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.color_bg_btn_sort_not_clicked_housing)));
                sortByNoteRecyclerView(idTrip);
            }
        });
        if (currentUser != null) {
            btnSortByPrice.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.color_bg_btn_sort_clicked_housing)));
            btnSortByNote.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.color_bg_btn_sort_not_clicked_housing)));
            sortByNoteRecyclerView(idTrip);
        }

        btnCreateHousing=findViewById(R.id.btn_new_housing_list_housings);
        btnCreateHousing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), NewHousingActivity.class);
                intent.putExtra(NAME_TRIP_LIST_HOUSING,nameTrip);
                intent.putExtra(NewHousingActivity.ID_TRIP_NEW_HOUSING,idTrip);
                startActivity(intent);
                finish();
            }
        });

        backImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fromAirBnb){
                    Intent intent = new Intent(getApplicationContext(), DetailTripActivity.class);
                    intent.putExtra(DetailTripActivity.ID_TRIP_DETAIL,idTrip);
                    intent.putExtra(DetailTripActivity.FROM_AIR_BNB,fromAirBnb);
                    startActivity(intent);
                }

                finish();
            }
        });


    }

    @Override
    public void housingClick(Housing housing) {
        if(firebaseAuth.getCurrentUser().getUid().equals(housing.getIdUserOwner())){
            Intent intent = new Intent(getApplicationContext(), EditHousingActivity.class);
            intent.putExtra(EditHousingActivity.ID_HOUSING_EDIT_HOUSING,housing.getId());
            intent.putExtra(NAME_TRIP_LIST_HOUSING,nameTrip);
            intent.putExtra(EditHousingActivity.ID_TRIP_EDIT_HOUSING,housing.getIdTrip());
            startActivityForResult(intent,REQUEST_CODE_EDIT_HOUSING);
        }
        else {
            Intent intent = new Intent(getApplicationContext(), DetailHousingActivity.class);
            intent.putExtra(DetailHousingActivity.ID_HOUSING_DETAIL_HOUSING,housing.getId());
            intent.putExtra(DetailHousingActivity.ID_TRIP_DETAIL_HOUSING,housing.getIdTrip());
            intent.putExtra(NAME_TRIP_LIST_HOUSING,nameTrip);
            startActivityForResult(intent,REQUEST_CODE_EDIT_HOUSING);
        }
    }

    public void sortByPriceRecyclerView(String idTrip){
        housingAdapter.emptyDataSet();
        progressBar.setVisibility(View.VISIBLE);
        housingService.getHousingOfTripOrderByPrice(idTrip, new ResultAsynchronTaskOneHousing() {
            @Override
            public void onResponseReceived(Housing response){
                housingAdapter.bindViewModel(response);
                progressBar.setVisibility(View.GONE);
                linearLayoutBtnsSortHousing.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNoResponseReceived(){
                progressBar.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                no_housing_yet.setVisibility(View.VISIBLE);
                linearLayoutBtnsSortHousing.setVisibility(View.GONE);


            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        setUpSwipe(recyclerView);
    }

    private void setUpSwipe(RecyclerView recyclerView) {
        final Activity activity = this;
        if(swipeHelper==null){
            swipeHelper = new SwipeHelper(this, recyclerView) {
                @Override
                public void instantiateUnderlayButton(final RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons) {
                    underlayButtons.add(new SwipeHelper.UnderlayButton(
                            "Supprimer",
                            R.drawable.ic_delete,
                            R.color.color_supprimer_activity,
                            getResources(),
                            new SwipeInterface() {
                                @Override
                                public void onClickQuitter(int pos) {
                                    new DialogDeleteHousing(activity,housingAdapter,((HousingAdapter.HousingViewHolder)viewHolder).getId(),((HousingAdapter.HousingViewHolder)viewHolder).getName()).show();

                                }
                            }
                    ));
                }
            };
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode== Activity.RESULT_OK && requestCode==REQUEST_CODE_EDIT_HOUSING){
            sortByNoteRecyclerView(idTrip);
        }
    }

    public void sortByNoteRecyclerView(final String idTrip){
        progressBar.setVisibility(View.VISIBLE);
        final HousingActionInterface housingActionInterface=this;
        if (nbParticipants==-1){
        tripService.getTripWithID(idTrip, new ResultAsynchronTaskOneTrip() {
            @Override
            public void onResponseReceived(Trip response) {
                nbParticipants=response.getParticipantsID().size();
                housingAdapter = new HousingAdapter(housingActionInterface,"ListHousing",nbParticipants);
                recyclerView.setAdapter(housingAdapter);
                housingAdapter.emptyDataSet();
                housingService.getHousingOfTripOrderByNote(idTrip, new ResultAsynchronTaskOneHousing() {
                    @Override
                    public void onResponseReceived(Housing response){
                        linearLayoutBtnsSortHousing.setVisibility(View.VISIBLE);
                        housingAdapter.bindViewModel(response);
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onNoResponseReceived(){
                        progressBar.setVisibility(View.GONE);
                        no_housing_yet.setVisibility(View.VISIBLE);
                        linearLayoutBtnsSortHousing.setVisibility(View.GONE);

                    }
                });
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                setUpSwipe(recyclerView);
            }

            @Override
            public void onAllResponseReceived() {

            }

            @Override
            public void onNoResponseReceived() {

            }
        });

    }
        else{
            housingAdapter.emptyDataSet();
            housingService.getHousingOfTripOrderByNote(idTrip, new ResultAsynchronTaskOneHousing() {
                @Override
                public void onResponseReceived(Housing response){
                    housingAdapter.bindViewModel(response);
                    linearLayoutBtnsSortHousing.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onNoResponseReceived(){
                    progressBar.setVisibility(View.GONE);
                    linearLayoutBtnsSortHousing.setVisibility(View.GONE);
                    no_housing_yet.setVisibility(View.VISIBLE);


                }
            });
            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        }
    }




}