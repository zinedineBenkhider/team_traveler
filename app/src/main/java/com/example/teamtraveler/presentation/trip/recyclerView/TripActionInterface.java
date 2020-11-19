package com.example.teamtraveler.presentation.trip.recyclerView;

import android.widget.ImageView;

import com.example.teamtraveler.data.entities.Trip;

public interface TripActionInterface {
    void tripClick(Trip trip);
    void setVisibilityToImgNotification(Trip trip, ImageView imgNotification);
    void setupRecyclerView();
}
