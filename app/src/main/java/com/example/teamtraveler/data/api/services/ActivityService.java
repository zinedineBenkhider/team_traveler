package com.example.teamtraveler.data.api.services;

import android.content.res.Resources;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.teamtraveler.R;
import com.example.teamtraveler.data.api.services.resultAsynchTaskActivity.ResultAsynchTaskActivityAdded;
import com.example.teamtraveler.data.api.services.resultAsynchTaskActivity.ResultAsynchTaskActivityUpdated;
import com.example.teamtraveler.data.api.services.resultAsynchTaskActivity.ResultAsynchronTaskOneActivity;
import com.example.teamtraveler.data.entities.Activity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.List;

public class ActivityService {
    private static final String TAG_ADD = "add activity";
    private static final String TAG_GET_ALL = "all activities of trip";
    private static final String TAG_GET_ACTIVITY = "get activity with id";
    private CollectionReference collectionReferenceActivities;
    private static final String usersInterested= "usersInterested";

    public ActivityService(){
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        this.collectionReferenceActivities=database.collection("Activities");
    }

    public void removeActivity(String id) {
        getActivitiesOfTrip(id, new ResultAsynchronTaskOneActivity() {
            @Override
            public void onResponseReceived(Activity response) {
                collectionReferenceActivities.document(response.getId()).delete();
            }

            @Override
            public void onNoResponseReceived() {
                //do nothing if no resp
            }

            @Override
            public void onAllResponseReceived() {
                //do nothing if no resp
            }
        });
    }

    public void addActivity(Activity activity,final ResultAsynchTaskActivityAdded resultAsynchTaskActivityAdded){
        collectionReferenceActivities.document(activity.getId()).set(activity).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                resultAsynchTaskActivityAdded.onActivityAdded();
                Log.d(TAG_ADD,"Success the activity is added ");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG_ADD,"Error the activity does not added");
                    }
                });
    }

    public void getActivityByID(String id,final ResultAsynchronTaskOneActivity resultAsynchronTask){
        DocumentReference docRef=collectionReferenceActivities.document(id);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.d(TAG_GET_ACTIVITY,documentSnapshot.toString());
                Activity activity = documentSnapshot.toObject(Activity.class);
                resultAsynchronTask.onResponseReceived(activity);
            }
        });
    }

    public void updateActivity(String activityId,Activity activity){
        final DocumentReference docRef=collectionReferenceActivities.document(activityId);
        docRef.update("entitled",activity.getEntitled());
        docRef.update("location",activity.getLocation());
        docRef.update("price",activity.getPrice());
        docRef.update("type",activity.getType());
        docRef.update("webSite",activity.getWebSite());
    }

    public void addOrUpdateParticipantOpinion(String activityId, final String participantId, final String newOpinion){
        final DocumentReference docRef=collectionReferenceActivities.document(activityId);
        getActivityByID(activityId, new ResultAsynchronTaskOneActivity() {
            @Override
            public void onResponseReceived(Activity response) {
                response.getUsersInterested().put(participantId,newOpinion);
                docRef.update(usersInterested,response.getUsersInterested());
            }

            @Override
            public void onNoResponseReceived() {
                //do nothing if no resp
            }

            @Override
            public void onAllResponseReceived() {
                //do nothing if no resp
            }
        });
    }

    public void initParticipantOpinions(String activityId, final List<String> participantsId, final Resources resources,final ResultAsynchTaskActivityUpdated resultAsynchTaskActivityUpdated){
        final DocumentReference docRef=collectionReferenceActivities.document(activityId);
        getActivityByID(activityId, new ResultAsynchronTaskOneActivity() {
            @Override
            public void onResponseReceived(Activity response) {
                for (int i=0;i<participantsId.size();i++){
                    if(participantsId.get(i).equals(response.getIdUserOwner())){
                        response.getUsersInterested().put(participantsId.get(i), resources.getString(R.string.opinion_participate));
                    }
                    else{
                        response.getUsersInterested().put(participantsId.get(i), resources.getString(R.string.without_opinion));
                    }
                }
                docRef.update(usersInterested,response.getUsersInterested()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        resultAsynchTaskActivityUpdated.onActivityUpdated();
                    }
                });
            }
            @Override
            public void onNoResponseReceived() {
                //do nothing if no resp
            }

            @Override
            public void onAllResponseReceived() {
                //do nothing if no resp
            }
        });
    }

    public void getActivitiesOfTrip(final String idTrip, final ResultAsynchronTaskOneActivity resultAsynchronTask){
        collectionReferenceActivities.whereEqualTo("idTrip",idTrip).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<DocumentSnapshot> activities = task.getResult().getDocuments();
                if(!activities.isEmpty()) {
                    Log.d(TAG_GET_ALL,"Success activities exists");
                    for (DocumentSnapshot housingDocuments : activities) {
                        Activity activity = housingDocuments.toObject(Activity.class);
                        resultAsynchronTask.onResponseReceived(activity);
                    }
                    resultAsynchronTask.onAllResponseReceived();
                }
                else {
                    Log.d(TAG_GET_ALL,"Failed activities does not exists");
                    resultAsynchronTask.onNoResponseReceived();
                }
            }

        });
    }


    public void quitActivity(final FirebaseUser currentUser, String id) {
        getActivitiesOfTrip(id, new ResultAsynchronTaskOneActivity() {
            @Override
            public void onResponseReceived(Activity response) {
                if(response.getUsersInterested().containsKey(currentUser.getUid())){
                    response.getUsersInterested().remove(currentUser.getUid());
                    collectionReferenceActivities.document(response.getId()).update(usersInterested,response.getUsersInterested());
                }
            }

            @Override
            public void onNoResponseReceived() {
                //do nothing if no resp
            }

            @Override
            public void onAllResponseReceived() {
                //do nothing if no resp
            }
        });
    }

    public void deleteActivity(FirebaseUser currentUser, final String id) {
        collectionReferenceActivities.document(id).delete();
    }
}
