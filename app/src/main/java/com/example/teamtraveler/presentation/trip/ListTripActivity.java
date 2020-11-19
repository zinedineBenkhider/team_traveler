package com.example.teamtraveler.presentation.trip;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.teamtraveler.R;
import com.example.teamtraveler.Utils.BaseActivity;
import com.example.teamtraveler.presentation.trip.fragment.TripCompletedFragment;
import com.example.teamtraveler.presentation.trip.fragment.TripInProgressFragment;
import com.example.teamtraveler.presentation.trip.recyclerView.TripActionInterface;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

public class ListTripActivity extends BaseActivity {
    private ViewPager viewPager;
    private Fragment fragmentOne, fragmentTwo;
    private FirebaseAuth firebaseAuth;
    private FloatingActionButton btnNewTrip;
    public final static String FROM_LIST_TRIP_ACTIVITY = "com.example.teamtraveler.presentation.displayTrip.FROM_LIST_TRIP_ACTIVITY";
    public final static int REQUEST_CODE_NEW_TRIP = 1;
    private static ListTripActivity LIST_TRIP_ACTIVITY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LIST_TRIP_ACTIVITY=this;
        setContentView(R.layout.activity_list_trip);
        fragmentOne = TripInProgressFragment.newInstance();
        fragmentTwo = TripCompletedFragment.newInstance();
        Toolbar toolbar = findViewById(R.id.toolbar_list_trip);
        setSupportActionBar(toolbar);
        btnNewTrip = findViewById(R.id.btn_new_trip_trips_progress);
        btnNewTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NewTripActivity.class);
                intent.putExtra(FROM_LIST_TRIP_ACTIVITY, true);
                startActivityForResult(intent, REQUEST_CODE_NEW_TRIP);

            }
        });
        firebaseAuth = FirebaseAuth.getInstance();
        viewPager = findViewById(R.id.tab_viewpager);
        setupViewPagerAndTabs();
    }

    public static ListTripActivity getInstance(){
        return LIST_TRIP_ACTIVITY;
    }

    private void setupViewPagerAndTabs() {
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                if (position == 0) {
                    return fragmentOne;
                }
                return fragmentTwo;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                if (position == 0) {
                    return TripInProgressFragment.TAB_NAME;
                }
                return TripCompletedFragment.TAB_NAME;
            }

            @Override
            public int getCount() {
                return 2;
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            if(data!=null) {
                Boolean recharDataTripEdited, rechargeDataTripCreated;
                recharDataTripEdited = data.getBooleanExtra(NewTripActivity.RECHARGE_DATA_NEW_TRIP, false);
                rechargeDataTripCreated = data.getBooleanExtra(NewTripActivity.RECHARGE_DATA_EDIT_TRIP, false);
                if (recharDataTripEdited || rechargeDataTripCreated) {
                    TripActionInterface tripActionInterface = (TripActionInterface) fragmentOne;
                    tripActionInterface.setupRecyclerView();
                }
            }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null){
            TripActionInterface tripActionInterface = (TripActionInterface) fragmentOne;
            tripActionInterface.setupRecyclerView();
        }
    }

}
