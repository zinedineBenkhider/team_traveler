package com.example.teamtraveler.data.api.services;

import android.util.Log;

import androidx.annotation.NonNull;
import com.example.teamtraveler.data.api.services.resultAsynchTaskHousing.ResultAsynchronTaskOneHousing;
import com.example.teamtraveler.data.api.services.resultAsynchTaskTrip.ResultAsynchronTaskOneTrip;
import com.example.teamtraveler.data.entities.Housing;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class HousingService {
    private static final String TAG_ADD = "add housing";
    private static final String TAG_GET_HOUSE = "get housing with id";
    private static final String s_usersNotes = "usersNotes";
    private static final String s_averageNote = "averageNote";
    private static final String s_idTrip = "idTrip";
    private CollectionReference collectionReferenceHousings;

    public HousingService(){
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        this.collectionReferenceHousings=database.collection("Housings");
    }

    public void addHousing(Housing housing){
        collectionReferenceHousings.document(housing.getId()).set(housing).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG_ADD,"Success the housing is added ");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG_ADD,"Error the housing does not added");
                    }
                });
    }

    public void getHousingWithID(String id,final ResultAsynchronTaskOneHousing resultAsynchronTask){
        DocumentReference docRef=collectionReferenceHousings.document(id);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.d(TAG_GET_HOUSE,documentSnapshot.toString());
                Housing housing = documentSnapshot.toObject(Housing.class);
                resultAsynchronTask.onResponseReceived(housing);
            }
        });
    }



    public void updateNoteOfHousing(String housingId, final String userId, final float note,final ResultAsynchronTaskOneHousing resultAsynchronTask){
        final DocumentReference docRef=collectionReferenceHousings.document(housingId);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Housing housing = documentSnapshot.toObject(Housing.class);
                if(housing==null){
                    throw new NullPointerException();
                }
                Map<String,Float> usersNotes=housing.getUsersNotes();
                usersNotes.put(userId,note);
                housing.setUsersNotes(usersNotes);
                float averageNote=calculateAverageOfNote(usersNotes);
                housing.setAverageNote(averageNote);
                docRef.update(s_usersNotes, usersNotes);
                docRef.update(s_averageNote, averageNote);
                resultAsynchronTask.onResponseReceived(housing);
            }
        });
    }

    public void updateHousing(String housingId,Housing newHousing){
        final DocumentReference docRef=collectionReferenceHousings.document(housingId);
        docRef.update("name",newHousing.getName());
        docRef.update("nbRoom",newHousing.getNbRoom());
        docRef.update("nbBathRoom",newHousing.getNbBathRoom());
        docRef.update("description",newHousing.getDescription());
        docRef.update("haveSwimmingPool",newHousing.isHaveSwimmingPool());
        docRef.update("haveGarage",newHousing.isHaveGarage());
        docRef.update("haveGardin",newHousing.isHaveGarage());
        docRef.update("link",newHousing.getLink());
        docRef.update("haveWifi",newHousing.isHaveWifi());
        docRef.update("price",newHousing.getPrice());
        docRef.update("nbBed",newHousing.getNbBed());
        docRef.update("haveChicken",newHousing.isHaveChicken());
        docRef.update("haveClimatisation",newHousing.isHaveClimatisation());
    }

    private float calculateAverageOfNote(Map<String,Float> usersNotes){
        Iterator<Map.Entry<String, Float>> itr = usersNotes.entrySet().iterator();
        float somme=0;
        while(itr.hasNext())
        {
            Map.Entry<String, Float> entry = itr.next();
            somme+=entry.getValue();
        }
        return somme/usersNotes.size();
    }

    public void getHousingOfTripOrderByNote(final String idTrip, final ResultAsynchronTaskOneHousing resultAsynchronTask){
        collectionReferenceHousings.whereEqualTo(s_idTrip,idTrip).orderBy(s_averageNote, Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                    List<DocumentSnapshot> housings = task.getResult().getDocuments();
                    if(!housings.isEmpty()) {
                        for (DocumentSnapshot housingDocuments : housings) {
                            Housing housing = housingDocuments.toObject(Housing.class);
                            resultAsynchronTask.onResponseReceived(housing);
                        }
                    }
                    else {
                            resultAsynchronTask.onNoResponseReceived();
                    }
            }

        });
    }

    public void getHousingOfTripOrderByPrice(final String idTrip, final ResultAsynchronTaskOneHousing resultAsynchronTask){
        collectionReferenceHousings.whereEqualTo(s_idTrip,idTrip).orderBy("price", Query.Direction.ASCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<DocumentSnapshot> housings = task.getResult().getDocuments();
                if(!housings.isEmpty()) {
                    for (DocumentSnapshot housingDocuments : housings) {
                        Housing housing = housingDocuments.toObject(Housing.class);
                        resultAsynchronTask.onResponseReceived(housing);
                    }
                }
                else {
                        resultAsynchronTask.onNoResponseReceived();
                }
            }

        });
    }

    public void getSomeHousingsOrderByNote(String idTrip,final ResultAsynchronTaskOneHousing resultAsynchronTask){
        collectionReferenceHousings.whereEqualTo(s_idTrip,idTrip).orderBy(s_averageNote, Query.Direction.DESCENDING).limit(3).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                List<DocumentSnapshot> housings = task.getResult().getDocuments();
                if(!housings.isEmpty()) {
                    for (DocumentSnapshot housingDocuments : housings) {
                        Housing housing = housingDocuments.toObject(Housing.class);
                        resultAsynchronTask.onResponseReceived(housing);
                    }
                }
                else {
                    resultAsynchronTask.onNoResponseReceived();
                }
            }

        });
    }


    public void quitHousing(final FirebaseUser currentUser, String id) {
        getHousingOfTripOrderByNote(id, new ResultAsynchronTaskOneHousing() {
            @Override
            public void onResponseReceived(Housing response) {
                if(response.getUsersNotes().containsKey(currentUser.getUid())){
                    response.getUsersNotes().remove(currentUser.getUid());
                    collectionReferenceHousings.document(response.getId()).update(s_usersNotes,response.getUsersNotes());
                }
            }

            @Override
            public void onNoResponseReceived() {
                //do nothing if no resp
            }
        });
    }

    public void removeService(String id) {
        getHousingOfTripOrderByNote(id, new ResultAsynchronTaskOneHousing() {
            @Override
            public void onResponseReceived(Housing response) {
                collectionReferenceHousings.document(response.getId()).delete();
            }

            @Override
            public void onNoResponseReceived() {
                //do nothing if no resp
            }
        });
    }

    public void deleteHousing(FirebaseUser currentUser, String id) {
        collectionReferenceHousings.document(id).delete();
    }
}