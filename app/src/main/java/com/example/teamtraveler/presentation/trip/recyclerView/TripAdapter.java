package com.example.teamtraveler.presentation.trip.recyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teamtraveler.R;
import com.example.teamtraveler.Utils.ManipulateDate;
import com.example.teamtraveler.data.entities.Trip;

import java.util.ArrayList;
import java.util.List;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {

    private List<Trip> tripList;
    private static TripActionInterface tripActionInterface;


    public TripAdapter(TripActionInterface tripActionInterface) {
        this.tripList = new ArrayList<>();
       this.tripActionInterface = tripActionInterface;
    }


    // Create new views (invoked by the layout manager)
    @Override
    public TripAdapter.TripViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_trip_item, parent, false);
        TripViewHolder voyageViewHolder = new TripViewHolder(v);
        return voyageViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
        holder.updateTrip(tripList.get(position));
    }


    @Override
    public int getItemCount() {
        return tripList.size();
    }


    public void bindViewModel(Trip mTrip) {
        this.tripList.add(mTrip);
        notifyDataSetChanged();
    }

    public void remove(Trip mTrip){
        this.tripList.remove(mTrip);
        notifyDataSetChanged();
    }

    public void remove(String tripId){
        for (int i=0;i<tripList.size();i++){
            if(tripList.get(i).getId().equals(tripId)){
                this.tripList.remove(i);
                notifyDataSetChanged();
            }
        }


    }

    public void emptyDataSet(){
        this.tripList=new ArrayList<>();
    }


    public static class TripViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView;
        private TextView nbParticipantTextView;
        private TextView locationTextView;
        private TextView datetextView;
        private ImageView imgNotification;
        private View v;
        private String id;


        public TripViewHolder(View v) {
            super(v);
            this.v = v;
            nameTextView = v.findViewById(R.id.name_trip_trip_item);
            datetextView=v.findViewById(R.id.date_trip_trip_item);
            locationTextView=v.findViewById(R.id.location_trip_trip_item);
            nbParticipantTextView = v.findViewById(R.id.nb_partipants_trip_item);
            imgNotification=v.findViewById(R.id.img_notification);
        }

        public String getName(){
            return nameTextView.getText().toString();
        }

        public String getId(){
            return id;
        }

        public void updateTrip(Trip trip) {
            id = trip.getId();
            String startDate="";
            String endDate="";
            if (trip.getStartDate()!=null){
                startDate=ManipulateDate.getDateFormatFrench(trip.getStartDate().toString());
            }
            if (trip.getEndDate()!=null){
                endDate= ManipulateDate.getDateFormatFrench(trip.getEndDate().toString());
            }
            nameTextView.setText(trip.getName());
            if(! "".equals(startDate)){
                datetextView.setText("Du " + startDate + " au " + endDate);
            }
            else{
                datetextView.setText("Pas de date définie");
            }
            if(! "".equals(trip.getLocation())){
                locationTextView.setText("à " + trip.getLocation());

            }
            nbParticipantTextView.setText(String.valueOf(trip.getParticipantsID().size()));
            tripActionInterface.setVisibilityToImgNotification(trip,imgNotification);

            if(TextUtils.isEmpty(trip.getLocation())){
                locationTextView.setVisibility(View.INVISIBLE);
            }
            setupListeners(trip);
        }

        private void setupListeners(final Trip trip){
           v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    imgNotification.setVisibility(View.GONE);
                    tripActionInterface.tripClick(trip);
                }
            });
        }

    }
}

