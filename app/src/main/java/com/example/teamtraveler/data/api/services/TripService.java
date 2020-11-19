package com.example.teamtraveler.data.api.services;

import android.util.Log;
import androidx.annotation.NonNull;
import com.example.teamtraveler.Utils.ManipulateDate;
import com.example.teamtraveler.data.api.services.resultAsynchTaskTrip.ResultAsynchronTaskOneTrip;
import com.example.teamtraveler.data.entities.Trip;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TripService {
    private static final String TAG_ADD = "add trip";
    private CollectionReference collectionReferenceTrips;
    private UserService userService;
    private final static String participantsID = "participantsID";
    private final static String activitiesId = "activitiesId";

    public TripService(){
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        this.collectionReferenceTrips=database.collection("Voyages");
        userService=new UserService();

    }


    public void addHousingToTrip(String idTrip, final String housingId){
        final DocumentReference docRef=collectionReferenceTrips.document(idTrip);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Trip trip = documentSnapshot.toObject(Trip.class);
                trip.addHousingId(housingId);

                docRef.update("housingsId", trip.getHousingsId());
            }
        });
    }



    public void addActivityToTrip(String idTrip, final String activityId){
        final DocumentReference docRef=collectionReferenceTrips.document(idTrip);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Trip trip = documentSnapshot.toObject(Trip.class);
                trip.addActivity(activityId);

                docRef.update(activitiesId, trip.getActivitiesId());
            }
        });
    }

    public void deleteActivityToTrip(String idTrip, final String activityId){
        final DocumentReference docRef=collectionReferenceTrips.document(idTrip);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Trip trip = documentSnapshot.toObject(Trip.class);
                trip.getActivitiesId().remove(activityId);

                docRef.update(activitiesId, trip.getActivitiesId());
            }
        });
    }
    public void updateTrip(Trip trip){
        final DocumentReference docRef=collectionReferenceTrips.document(trip.getId());
        docRef.update("name",trip.getName());
        docRef.update("location",trip.getLocation());
        docRef.update("startDate",trip.getStartDate());
        docRef.update("endDate",trip.getEndDate());
    }

    public void addTrip(Trip trip){
        collectionReferenceTrips.document(trip.getId()).set(trip).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG_ADD,"Success the trip is added ");
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG_ADD,"Error the trip does not added");
            }
        });
    }

    public void getTripWithID(String id,final ResultAsynchronTaskOneTrip resultAsynchronTask){

        DocumentReference docRef=collectionReferenceTrips.document(id);

                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Log.d(TAG_ADD, documentSnapshot.toString());
                            Trip trip = documentSnapshot.toObject(Trip.class);
                            resultAsynchronTask.onResponseReceived(trip);
                        }
                        else {
                            resultAsynchronTask.onNoResponseReceived();
                        }
                    }
                });
    }







    public void getTripsInProgressOfParticipant(final String userID, final ResultAsynchronTaskOneTrip resultAsynchronTask){
        collectionReferenceTrips.whereArrayContains(participantsID,userID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<DocumentSnapshot> documentSnapshotList = task.getResult().getDocuments();
                int count = 0;
                for (DocumentSnapshot documentSnapshot : documentSnapshotList) {
                    Trip trip = documentSnapshot.toObject(Trip.class);
                    if (trip.getEndDate() != null) {
                        if (ManipulateDate.compareDate(trip.getEndDate(), new Date()) >= 0) {
                            count++;
                            resultAsynchronTask.onResponseReceived(trip);
                        }
                    } else {
                        count++;
                        resultAsynchronTask.onResponseReceived(trip);
                    }

                }
                if (count == 0) {
                    resultAsynchronTask.onNoResponseReceived();
                }

                resultAsynchronTask.onAllResponseReceived();

            }
        });
    }

    public void getCompletedTripsOfParticipant(String userID,final ResultAsynchronTaskOneTrip resultAsynchronTask) {
        collectionReferenceTrips.whereArrayContains(participantsID,userID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<DocumentSnapshot> documentSnapshotList = task.getResult().getDocuments();
                int count = 0;
                for (DocumentSnapshot documentSnapshot : documentSnapshotList) {
                        Trip trip = documentSnapshot.toObject(Trip.class);
                        if (trip.getEndDate() != null) {
                            if (ManipulateDate.compareDate(trip.getEndDate(), new Date()) < 0) {
                                resultAsynchronTask.onResponseReceived(trip);
                                count++;
                            }

                        }
                    }
                   if (count == 0) {
                        resultAsynchronTask.onNoResponseReceived();
                    }
            }

        });
    }

    public void updateParticipantsToNotify(String tripId, List<String> participantsId){
        final DocumentReference docRef=collectionReferenceTrips.document(tripId);
        docRef.update("participantsIdToNotify",participantsId);
    }

    public void addParticipantToTrip(final String idParticipant, String tripId){
            final DocumentReference docRef=collectionReferenceTrips.document(tripId);
            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {

                    Trip trip = documentSnapshot.toObject(Trip.class);
                    if (trip!=null){
                        if(!trip.getParticipantsID().contains(idParticipant)){
                            trip.addParticipant(idParticipant);

                            docRef.update(participantsID, trip.getParticipantsID());
                    }

                }}
            });
    }

    public void notifyParticipants(final String idTrip,final String idCurrentUser){
        getTripWithID(idTrip, new ResultAsynchronTaskOneTrip() {
            @Override
            public void onResponseReceived(Trip response) {
                Set<String> participantsToNotify = new HashSet<>(response.getParticipantsID());

                participantsToNotify.remove(idCurrentUser);
                List<String> listParticipantsToNotify=new ArrayList<>(participantsToNotify);
                updateParticipantsToNotify(idTrip,listParticipantsToNotify);
            }

            @Override
            public void onAllResponseReceived() {
                //do nothing if no resp
            }

            @Override
            public void onNoResponseReceived() {
                //do nothing if no resp
            }
        });
    }

    public void quitTrip(final FirebaseUser currentUser, final String id) {
        getTripWithID(id, new ResultAsynchronTaskOneTrip() {
            @Override
            public void onResponseReceived(Trip response) {
                response.removeParticipant(currentUser.getUid());
                collectionReferenceTrips.document(response.getId()).update(participantsID,response.getParticipantsID());
                if(response.getParticipantsID().size()==0){
                    collectionReferenceTrips.document(id).delete();
                    ActivityService activityService = new ActivityService();
                    HousingService housingService = new HousingService();
                    TransportService transportService = new TransportService();
                    activityService.removeActivity(id);
                    housingService.removeService(id);
                    transportService.removeTransport(id);
                }
            }

            @Override
            public void onAllResponseReceived() {
                //do nothing if no resp
            }

            @Override
            public void onNoResponseReceived() {
                //do nothing if no resp

            }
        });
    }
}
