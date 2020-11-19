package com.example.teamtraveler.presentation.housing.recyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teamtraveler.R;
import com.example.teamtraveler.data.entities.Housing;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class HousingAdapter extends RecyclerView.Adapter<HousingAdapter.HousingViewHolder> {

    private List<Housing> housingList;
    private static HousingActionInterface housingActionInterface;
    private String activity;
    private static int nbParticipants;
    public HousingAdapter(HousingActionInterface housingActionInterface, String activty, int nbParticipants) {
        this.housingList = new ArrayList<>();
       this.housingActionInterface = housingActionInterface;
       this.activity=activty;
       this.nbParticipants=nbParticipants;
    }


    @Override
    public HousingAdapter.HousingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if (activity.equals("DetailTrip")){
            v=LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycler_housing_item_detail_trip, parent, false);
        }
        else {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycler_housing_item, parent, false);
        }
        HousingViewHolder voyageViewHolder = new HousingViewHolder(v);
        return voyageViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull HousingViewHolder holder, int position) {
        holder.updateHousing(housingList.get(position),position);
    }


    @Override
    public int getItemCount() {
        return housingList.size();
    }


    public void bindViewModel(Housing housing) {
        this.housingList.add(housing);
        notifyDataSetChanged();
    }
    public void emptyDataSet(){
        this.housingList=new ArrayList<>();
    }

    public void remove(String id) {
        Housing toDelete=null;
        for(Housing h : housingList){
            if (id.equals(h.getId())){
                toDelete=h;
            }
        }
        housingList.remove(toDelete);
        notifyDataSetChanged();
    }


    public static class HousingViewHolder extends RecyclerView.ViewHolder {
        private TextView noteTextView;
        private TextView nameTextView;
        private TextView rangeTextView;
        private RatingBar markRatingBar;
        private TextView pricetextView;
        private TextView suffix;
        private ImageView imgmedal;
        private View v;
        private String id;
        private String name;

        public HousingViewHolder(View v) {
            super(v);
            this.v = v;
            nameTextView = v.findViewById(R.id.name_housing_item);
            pricetextView=v.findViewById(R.id.price_housing_item);
            markRatingBar=v.findViewById(R.id.rating_bar_housing_item);
            rangeTextView = v.findViewById(R.id.range_housing_item);
            noteTextView = v.findViewById(R.id.textView_note);
            imgmedal = v.findViewById(R.id.imageView_medal);
            suffix = v.findViewById(R.id.range_housing_item_suffixe);
        }

        public void updateHousing(Housing housing,int position) {
            nameTextView.setText(housing.getName());
            int priceParticipant = housing.getPrice() / nbParticipants;
            id = housing.getId();
            name=housing.getName();
            pricetextView.setText(priceParticipant + "€/nuit/pers - ("+ housing.getPrice()+"€/nuit)");
            markRatingBar.setRating(housing.getAverageNote());
            markRatingBar.setEnabled(false);
            rangeTextView.setText((position+1)+"");
            suffix.setText("eme");
            if (rangeTextView.getText().equals("1")){
                suffix.setText("er");
                int d = R.drawable.ic_gold_medal;
                imgmedal.setImageResource(d);
            }
            else if (rangeTextView.getText().equals("2")){
                int d = R.drawable.ic_silver_medal;
                imgmedal.setImageResource(d);
            }
            else if (rangeTextView.getText().equals("3")){
                int d = R.drawable.ic_bronze_medal;
                imgmedal.setImageResource(d);
            }
            else{
                imgmedal.setImageResource(0);
            }
            noteTextView.setText(String.valueOf(new DecimalFormat("#.##").format(housing.getAverageNote())));
            setupListeners(housing);
        }

        private void setupListeners(final Housing housing){
           v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    housingActionInterface.housingClick(housing);
                }
            });
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

