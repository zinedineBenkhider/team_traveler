package com.example.teamtraveler.presentation.transport.recyclerView;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.teamtraveler.R;
import com.example.teamtraveler.Utils.ManipulateDate;
import com.example.teamtraveler.data.entities.Transport;
import com.example.teamtraveler.presentation.transport.TransportTypeIconMapping;
import com.example.teamtraveler.presentation.transport.dialog.DialogDeleteTransport;

import java.util.ArrayList;
import java.util.List;

public class TransportAdapter extends RecyclerView.Adapter<TransportAdapter.TransportViewHolder> {

    private List<TransportViewModel> transportList;
    private static TransportActionInterface transportActionInterface;

    public TransportAdapter(TransportActionInterface transportActionInterface) {
        this.transportList = new ArrayList<>();
        this.transportActionInterface = transportActionInterface;
    }


    @Override
    public TransportAdapter.TransportViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_transport_item, parent, false);
        TransportAdapter.TransportViewHolder transportViewHolder = new TransportAdapter.TransportViewHolder(v);
        return transportViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TransportAdapter.TransportViewHolder holder, int position) {
        holder.updateTransport(transportList.get(position));
    }


    @Override
    public int getItemCount() {
        return transportList.size();
    }


    public void bindViewModel(TransportViewModel transport) {
        this.transportList.add(transport);
        notifyDataSetChanged();
    }

    public void emptyDataSet() {
        for (TransportViewModel t : transportList){
            transportList.remove(t);
            notifyDataSetChanged();
        }
    }

    public void remove(String id) {
        TransportViewModel toDelete = null;
        for (TransportViewModel t : transportList){
            if (id.equals(t.getTransport().getId())){
                toDelete = t;
            }
        }
        transportList.remove(toDelete);
        notifyDataSetChanged();
    }


    public static class TransportViewHolder extends RecyclerView.ViewHolder {
        private TextView departureLocationTextView;
        private TextView reachLocationTextView;
        private TextView dateDepartureTextView;
        private TextView timeTextView;
        private TextView organizer;
        private ImageView imgTypeTransport;
        private ImageButton btnJoinExitTransport;
        private TextView joinExitTxtListTransport;
        private ImageButton btnSupprimer;
        private CardView cardView;
        private View v;
        public TransportViewHolder(View v) {
            super(v);
            this.v = v;
            departureLocationTextView=v.findViewById(R.id.departure_location_list_transport);
            reachLocationTextView=v.findViewById(R.id.reach_location_list_transport);
            dateDepartureTextView = v.findViewById(R.id.date_departure_list_transport);
            timeTextView=v.findViewById(R.id.time_list_transport);
            organizer=v.findViewById(R.id.name_creator_list_transport);
            imgTypeTransport=v.findViewById(R.id.imageView_type_list_transport);
            btnJoinExitTransport=v.findViewById(R.id.image_button_transport);
            joinExitTxtListTransport=v.findViewById(R.id.join_exit_txt_list_transport);
            btnSupprimer = v.findViewById(R.id.imageButton_supprimer);
            cardView = v.findViewById(R.id.cardview_transport);
        }

        public void updateTransport(final TransportViewModel transportViewModel) {
   
            String departureLocation="";
            String reachLocation="";
            String departureDate="";
            String departureTime="";
            String reachTime="";
            String transportType=v.getResources().getString(R.string.transport_type_not_specified);

            if(!TextUtils.isEmpty(transportViewModel.getTransport().getDeparture())){
                departureLocation=transportViewModel.getTransport().getDeparture();
            }
            if(!TextUtils.isEmpty(transportViewModel.getTransport().getType())){
                transportType=transportViewModel.getTransport().getType();
            }
            if(!TextUtils.isEmpty(transportViewModel.getTransport().getReach())){
                reachLocation=transportViewModel.getTransport().getReach();
            }
            if(transportViewModel.getTransport().getDepartureDate()!=null){
                departureDate =ManipulateDate.getDateFormatFrench(transportViewModel.getTransport().getDepartureDate().toString());
            }
            if(transportViewModel.getTransport().getTimeDeparture()!=null){
                departureTime=ManipulateDate.getFormatTime(transportViewModel.getTransport().getTimeDeparture().getHours(),transportViewModel.getTransport().getTimeDeparture().getMinutes());

            }
            if (transportViewModel.getTransport().getTimeReach()!=null){
                reachTime=ManipulateDate.getFormatTime(transportViewModel.getTransport().getTimeReach().getHours(),transportViewModel.getTransport().getTimeReach().getMinutes());
            }

            departureLocationTextView.setText(v.getResources().getString(R.string.departure_transport)+departureLocation);
            reachLocationTextView.setText(v.getResources().getString(R.string.reach_transport)+reachLocation);
            
            dateDepartureTextView.setText(departureDate);
            if(! "".equals(departureTime)){
                timeTextView.setText(departureTime+v.getResources().getString(R.string.departureAndReachTimes)+reachTime + "\n");
            }
            else{
                timeTextView.setText(v.getResources().getString(R.string.departure_date_not_spicified));
            }

            organizer.setText(transportViewModel.getNameOwner());
            setupListeners(transportViewModel.getTransport());
            transportActionInterface.initJoinExitTransportBtn(transportViewModel.getTransport(),btnJoinExitTransport,joinExitTxtListTransport);
            Drawable imgTypeDrawable= TransportTypeIconMapping.getIconOfType(v.getResources(),transportViewModel.getTransport().getType());
            imgTypeTransport.setImageDrawable(imgTypeDrawable);
            cardView.setCardBackgroundColor(v.getResources().getColor(R.color.color_backgroud_card_transport_item));

            if (transportViewModel.getSuppr()){
                cardView.setCardBackgroundColor(v.getResources().getColor(R.color.color_backgroud_card_transport_item_when_deleting));
                btnSupprimer.setVisibility(View.VISIBLE);
                btnJoinExitTransport.setBackgroundColor(v.getResources().getColor(R.color.color_backgroud_card_transport_item_when_deleting));
                btnJoinExitTransport.setClickable(false);
                btnSupprimer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new DialogDeleteTransport(transportViewModel.getActivity(),transportViewModel.getTransportAdapter(),transportViewModel.getTransport().getId(),transportViewModel.getTransport().getName()).show();
                    }
                });
            }
            else {
                btnSupprimer.setVisibility(View.INVISIBLE);
                cardView.setCardBackgroundColor(v.getResources().getColor(R.color.color_backgroud_card_transport));
            }
        }

        private void setupListeners(final Transport transport){
            btnJoinExitTransport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    transportActionInterface.joinExitTransportClick(transport,btnJoinExitTransport,joinExitTxtListTransport);
                }
            });

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    transportActionInterface.transportClick(transport);
                }
            });

        }

    }
}
