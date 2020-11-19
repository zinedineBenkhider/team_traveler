package com.example.teamtraveler.presentation.activities.recyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teamtraveler.R;
import com.example.teamtraveler.Utils.BgColorsOpinionsActivity;
import com.google.android.material.chip.Chip;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class ParticipantAdapter extends RecyclerView.Adapter<ParticipantAdapter.ParticipantViewHolder> {

    private List<ParticipantViewModel> participantsList;

    public ParticipantAdapter() {
        this.participantsList = new ArrayList<>();
    }


    @Override
    public ParticipantAdapter.ParticipantViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chip_manual_with_opinion, parent, false);
        ParticipantAdapter.ParticipantViewHolder voyageViewHolder = new ParticipantAdapter.ParticipantViewHolder(v);
        return voyageViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ParticipantAdapter.ParticipantViewHolder holder, int position) {
        holder.updateParticipant(participantsList.get(position));
    }

    @Override
    public int getItemCount() {
        return participantsList.size();
    }


    public void bindViewModel(ParticipantViewModel participant) {
        if ("".equals(participant.getParticipantOpinion())){
            this.participantsList.add(participant);
            notifyDataSetChanged();
        }
        else {
            this.participantsList.add(participant);
            this.participantsList = sort_participant_list(this.participantsList);
            notifyDataSetChanged();
        }
    }

    public void bindViewModelJoinTrip() {
        boolean add = true;
        for (ParticipantViewModel p : participantsList){
            if ("Vous".equals(p.getParticpantName())){
                add = false;
            }
        }
        if (add){
            participantsList.add(new ParticipantViewModel("Vous",""));
        }
    }

    public List<ParticipantViewModel> sort_participant_list(List<ParticipantViewModel> lp){
        List<ParticipantViewModel> new_list = new ArrayList<>();
        List<ParticipantViewModel> list_sans_avis = new ArrayList<>();
        List<ParticipantViewModel> list_participe = new ArrayList<>();
        List<ParticipantViewModel> list_interesse = new ArrayList<>();
        List<ParticipantViewModel> list_participepas = new ArrayList<>();

        for(ParticipantViewModel p  : lp){
            if("Interessé(e)".equals(p.getParticipantOpinion())){
                list_interesse.add(p);
            }
            if("Je participe".equals(p.getParticipantOpinion())){
                list_participe.add(p);
            }
            if("Je participe pas".equals(p.getParticipantOpinion())){
                list_participepas.add(p);
            }
            if("Sans avis".equals(p.getParticipantOpinion())){
                list_sans_avis.add(p);
            }
        }
        new_list.addAll(list_participe);
        new_list.addAll(list_interesse);
        new_list.addAll(list_participepas);
        new_list.addAll(list_sans_avis);

        return new_list;

    }

    public void emptyDataSet(){
        this.participantsList=new ArrayList<>();
    }

    public static class ParticipantViewHolder extends RecyclerView.ViewHolder {
        //private Chip chipParticipant;
        private TextView nameParticipant;
        private TextView opinionParticipantTextView;
        private LinearLayout layout;
        private View v;
        public ParticipantViewHolder(View v) {
            super(v);
            this.v = v;
            //chipParticipant = v.findViewById(R.id.chip_participant);
            nameParticipant = v.findViewById(R.id.textView_name_chip);
            layout = v.findViewById(R.id.linearLayout_chip_opinion);

            //opinionParticipantTextView=v.findViewById(R.id.opinion_participant);
            opinionParticipantTextView = v.findViewById(R.id.textView_opinion_chip);
        }

        public void updateParticipant(ParticipantViewModel participantViewModel) {
            //chipParticipant.setText(participantViewModel.getParticpantName());
            //chipParticipant.setChipBackgroundColor(BgColorsOpinionsActivity.getColorOfOpinion(participantViewModel.getParticipantOpinion(),v.getResources()));
            nameParticipant.setText(participantViewModel.getParticpantName());
            layout.setBackground(BgColorsOpinionsActivity.getColorOfOpiniondrawable(participantViewModel.getParticipantOpinion(),v.getResources()));
            opinionParticipantTextView.setText(participantViewModel.getParticipantOpinion());
        }


    }
}


