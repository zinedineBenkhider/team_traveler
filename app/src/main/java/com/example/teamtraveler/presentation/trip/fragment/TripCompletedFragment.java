package com.example.teamtraveler.presentation.trip.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teamtraveler.R;
import com.example.teamtraveler.data.api.services.resultAsynchTaskTrip.ResultAsynchronTaskOneTrip;
import com.example.teamtraveler.data.api.services.TripService;
import com.example.teamtraveler.data.entities.Trip;
import com.example.teamtraveler.presentation.trip.DetailTripActivity;
import com.example.teamtraveler.presentation.trip.ListTripActivity;
import com.example.teamtraveler.presentation.trip.recyclerView.TripActionInterface;
import com.example.teamtraveler.presentation.trip.recyclerView.TripAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class TripCompletedFragment extends Fragment implements TripActionInterface{
    public static final String TAB_NAME = "Terminés";
    private static TripCompletedFragment INSTANCE = null;
    private View rootView;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private TripAdapter tripAdapter;
    private TripService tripService;
    private FirebaseAuth firebaseAuth;
    private TextView no_trip_yet;


    private TripCompletedFragment() {
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public static TripCompletedFragment newInstance() {
        if (INSTANCE==null){
            return new TripCompletedFragment();
        }
        return INSTANCE;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        rootView = inflater.inflate(R.layout.fragment_trips_completed, container, false);
        INSTANCE=this;
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        progressBar = rootView.findViewById(R.id.progress_bar_trips_completed);
        no_trip_yet = getView().findViewById(R.id.no_trip_textview);
        tripService=new TripService();
        recyclerView = rootView.findViewById(R.id.recycler_view_ended_trip);
        tripAdapter = new TripAdapter(this);
        recyclerView.setAdapter(tripAdapter);
        setupRecyclerView();
    }

    public void setupRecyclerView() {
        tripAdapter.emptyDataSet();
        FirebaseUser currentUser=firebaseAuth.getCurrentUser();
        if(currentUser!=null) {
            progressBar.setVisibility(View.VISIBLE);
            tripService.getCompletedTripsOfParticipant(currentUser.getUid(), new ResultAsynchronTaskOneTrip() {
                @Override
                public void onResponseReceived(Trip response) {
                    tripAdapter.bindViewModel(response);
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onAllResponseReceived() {

                }

                @Override
                public void onNoResponseReceived() {
                    progressBar.setVisibility(View.GONE);
                    no_trip_yet.setVisibility(View.VISIBLE);

                }
            });
        }
        else{
            no_trip_yet.setVisibility(View.VISIBLE);
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void tripClick(Trip trip) {
        Intent intent = new Intent(getActivity().getApplicationContext(), DetailTripActivity.class);
        String currentUserId=firebaseAuth.getCurrentUser().getUid();
        List<String> participantsIdToNotify= trip.getParticipantsIdToNotify();
        participantsIdToNotify.remove(currentUserId);
        tripService.updateParticipantsToNotify(trip.getId(),participantsIdToNotify);

        intent.putExtra(DetailTripActivity.ID_TRIP_DETAIL,trip.getId());
        startActivityForResult(intent, ListTripActivity.REQUEST_CODE_NEW_TRIP);
    }


    @Override
    public void setVisibilityToImgNotification(Trip trip, ImageView imgNotification) {
        String idCurrentUser=firebaseAuth.getCurrentUser().getUid();
        if (trip.getParticipantsIdToNotify().contains(idCurrentUser)){
            imgNotification.setVisibility(View.VISIBLE);
        }
        else{
            imgNotification.setVisibility(View.GONE);
        }
    }
}
