package com.example.teamtraveler.data.api.services;

import android.util.Log;

import androidx.annotation.NonNull;


import com.example.teamtraveler.data.api.services.resultAsynchTaskTransport.ResultAsynchTaskTransportAdded;
import com.example.teamtraveler.data.api.services.resultAsynchTaskTransport.ResultAsynchronTaskOneTransport;
import com.example.teamtraveler.data.entities.Transport;
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
import java.util.Map;

public class TransportService {
    private static final String TAG_ADD = "add transport";
    private static final String TAG_GET_TRANSPORT = "get transport with id";
    private static final String FIELD_TO_UPDATE_NAME="name";
    private static final String FIELD_TO_UPDATE_DEPARTURE_LOCATION="departure";
    private static final String FIELD_TO_UPDATE_REACH_LOCATION="reach";
    private static final String FIELD_TO_UPDATE_DEPARTURE_DATE="departureDate";
    private static final String FIELD_TO_UPDATE_DEPARTURE_TIME="timeDeparture";
    private static final String FIELD_TO_UPDATE_REACH_TIME="timeReach";
    private static final String FIELD_TO_UPDATE_TYPE="type";
    private static final String FIELD_TO_UPDATE_USERS_PARTICIPATION = "usersParticipations";
    private CollectionReference collectionReferenceTransports;

    public TransportService(){
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        this.collectionReferenceTransports=database.collection("Transport");
    }

    public void addTransport(Transport transport,final ResultAsynchTaskTransportAdded asynchTaskTransportAdded){
        collectionReferenceTransports.document(transport.getId()).set(transport).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                asynchTaskTransportAdded.onTransportAdded();
                Log.d(TAG_ADD,"Success the transport is added ");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG_ADD,"Error the transport does not added");
                    }
                });
    }

    public void getTransportWithID(String id,final ResultAsynchronTaskOneTransport resultAsynchronTask){
        DocumentReference docRef=collectionReferenceTransports.document(id);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.d(TAG_GET_TRANSPORT,documentSnapshot.toString());
                Transport transport = documentSnapshot.toObject(Transport.class);
                resultAsynchronTask.onResponseReceived(transport);
            }
        });
    }




    public void updateTransport(String transportId,Transport newTransport){
        final DocumentReference docRef=collectionReferenceTransports.document(transportId);
        docRef.update(FIELD_TO_UPDATE_NAME,newTransport.getName());
        docRef.update(FIELD_TO_UPDATE_DEPARTURE_LOCATION,newTransport.getDeparture());
        docRef.update(FIELD_TO_UPDATE_DEPARTURE_DATE,newTransport.getDepartureDate());
        docRef.update(FIELD_TO_UPDATE_REACH_LOCATION,newTransport.getReach());
        docRef.update(FIELD_TO_UPDATE_DEPARTURE_TIME,newTransport.getTimeDeparture());
        docRef.update(FIELD_TO_UPDATE_REACH_TIME,newTransport.getTimeReach());
        docRef.update(FIELD_TO_UPDATE_TYPE,newTransport.getType());
    }

    public void updateOpinionOfParticipant(String transportId, Map<String,String> usersParticipations){
        final DocumentReference docRef=collectionReferenceTransports.document(transportId);
        docRef.update(FIELD_TO_UPDATE_USERS_PARTICIPATION,usersParticipations);

    }



    public void getTransportsOfTrip(final String idTrip, final ResultAsynchronTaskOneTransport resultAsynchronTask){
        collectionReferenceTransports.whereEqualTo("idTrip",idTrip).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<DocumentSnapshot> transports = task.getResult().getDocuments();
                if(!transports.isEmpty()) {
                    for (DocumentSnapshot transportDocuments : transports) {
                        Transport transport = transportDocuments.toObject(Transport.class);
                        resultAsynchronTask.onResponseReceived(transport);

                    }

                    Log.d(TAG_ADD,"Success All transports are displayed");
                }
                else {
                    resultAsynchronTask.onNoResponseReceived();
                }
            }

        });
    }

    public void quitTransport(final FirebaseUser currentUser, String id) {
        getTransportsOfTrip(id, new ResultAsynchronTaskOneTransport() {
            @Override
            public void onResponseReceived(Transport response) {
                if(response.getUsersParticipations().containsKey(currentUser.getUid())){
                    response.getUsersParticipations().remove(currentUser.getUid());
                    collectionReferenceTransports.document(response.getId()).update("usersParticipations",response.getUsersParticipations());
                }
            }

            @Override
            public void onNoResponseReceived() {
                //do nothing if no resp
            }
        });
    }

    public void removeTransport(String id) {
        getTransportsOfTrip(id, new ResultAsynchronTaskOneTransport() {
            @Override
            public void onResponseReceived(Transport response) {
                collectionReferenceTransports.document(response.getId()).delete();
            }

            @Override
            public void onNoResponseReceived() {
                //do nothing if no resp
            }
        });
    }

    public void deleteTransport(FirebaseUser currentUser, String id) {
        collectionReferenceTransports.document(id).delete();
    }
}
