package com.example.teamtraveler.presentation.activities.recyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teamtraveler.R;
import com.example.teamtraveler.Utils.TypeActivityMapping;
import com.example.teamtraveler.data.api.services.resultAsynchTaskUser.ResultAsynchronTaskUserName;
import com.example.teamtraveler.data.entities.Activity;
import com.example.teamtraveler.data.entities.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder> {

    private List<Activity> activityList;
    private static ActivityActionInterface activityActionInterface;

    private static Map<String,List<ParticipantViewModel>> participantViewModels;

    public ActivityAdapter(ActivityActionInterface activityActionInterface) {
        this.activityList = new ArrayList<>();
        this.activityActionInterface = activityActionInterface;
        participantViewModels=new HashMap<>();
    }


    @Override
    public ActivityAdapter.ActivityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_activity_item, parent, false);
        ActivityViewHolder voyageViewHolder = new ActivityViewHolder(v);
        return voyageViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ActivityViewHolder holder, int position) {
        holder.updateActivity(activityList.get(position));
    }


    @Override
    public int getItemCount() {
        return activityList.size();
    }


    public void bindViewModel(Activity activity) {
        this.activityList.add(activity);
        notifyDataSetChanged();
    }
    public void emptyDataSet(){
        this.activityList=new ArrayList<>();
    }

    public void remove(String id) {
        Activity toDelete = null;
        for(Activity a : activityList){
            if (id.equals(a.getId())){
               toDelete=a;
            }
        }
        activityList.remove(toDelete);
        notifyDataSetChanged();
    }


    public static class ActivityViewHolder extends RecyclerView.ViewHolder {
        private TextView entitledTextView;
        private TextView locationTextView;
        private TextView priceTextView;
        private TextView nbParticipantsTextView;
        private ImageView activityImageView;
        private TextView nameUserOwner;
        private RecyclerView participantsRecyclerView;
        private View v;
        private String name;
        private String id;

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public ActivityViewHolder(View v) {
            super(v);
            this.v = v;
            entitledTextView = v.findViewById(R.id.entitled_activity_item);
            locationTextView=v.findViewById(R.id.location_activity_item);
            activityImageView=v.findViewById(R.id.activity_img_activity_item);
            priceTextView=v.findViewById(R.id.price_activity_item);
            nbParticipantsTextView=v.findViewById(R.id.nb_participants_activity_item);
            participantsRecyclerView=v.findViewById(R.id.recycler_view_participants);
            nameUserOwner=v.findViewById(R.id.name_user_owner_activity_item);
        }

        public void updateActivity(final Activity activity) {
            entitledTextView.setText(activity.getEntitled());
            locationTextView.setText(activity.getLocation());
            priceTextView.setText(activity.getPrice()+"€");
            name = activity.getEntitled();
            id = activity.getId();
            int ressource = TypeActivityMapping.getImgActivity(activity.getType());
            activityImageView.setImageDrawable(v.getResources().getDrawable(ressource));
            Iterator<String> participantsId=activity.getUsersInterested().keySet().iterator();
            int nbParticipants=0;
            while(participantsId.hasNext()){
                String participantId=participantsId.next();
                if("Je participe".equals(activity.getUsersInterested().get(participantId))){
                    nbParticipants = nbParticipants+1;
                }
            }

            String participantStr=" "+v.getResources().getString(R.string.nb_participants_recycler_activity_item);
            nbParticipantsTextView.setText(nbParticipants+participantStr);
            final ParticipantAdapter participantAdapter=new ParticipantAdapter();
            participantsRecyclerView.setAdapter(participantAdapter);
            participantsRecyclerView.setLayoutManager(new LinearLayoutManager(v.getContext(), LinearLayoutManager.HORIZONTAL, false));
            setupListeners(activity);
            final List<ParticipantViewModel> participantViewModelsList=new ArrayList<>();
            if(!participantViewModels.containsKey(activity.getId())) {
                activityActionInterface.showButtomProgressBar();
                activityActionInterface.changeMarginButtomOfRecyclerView(34);

                activityActionInterface.getParticipants(activity.getIdUserOwner(), new ResultAsynchronTaskUserName() {
                    @Override
                    public void onResonseReceived(User user) {
                        String opinion = activity.getUsersInterested().get(user.getId());
                        ParticipantViewModel participantViewModel = new ParticipantViewModel(user.getName(), opinion);

                        participantAdapter.bindViewModel(participantViewModel);
                        participantViewModelsList.add(participantViewModel);
                        participantViewModels.put(activity.getId(), participantViewModelsList);
                        activityActionInterface.hideButtomProgressBar();
                        activityActionInterface.changeMarginButtomOfRecyclerView(0);
                    }

                    @Override
                    public void onResonseReceivedNameOwnerOfActivity(String nameOwner) {
                        String proposedByStr = v.getResources().getString(R.string.activity_proposed_by) + " ";
                        nameUserOwner.setText(proposedByStr + nameOwner);
                        }

                    @Override
                    public void onResonseReceivedAllUsers() {

                    }
                });
            }
            else {
                for(int i=0;i<participantViewModels.get(activity.getId()).size();i++){
                    participantAdapter.bindViewModel(participantViewModels.get(activity.getId()).get(i));
                }
                activityActionInterface.hideButtomProgressBar();
                activityActionInterface.changeMarginButtomOfRecyclerView(0);
            }
        }

        private void setupListeners(final Activity activity){
           v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activityActionInterface.activityClick(activity);
                }
            });
        }

    }
}

