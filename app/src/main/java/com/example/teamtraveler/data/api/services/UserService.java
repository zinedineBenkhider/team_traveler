package com.example.teamtraveler.data.api.services;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.teamtraveler.data.api.services.resultAsynchTaskUser.ResultAsynchronTaskUser;
import com.example.teamtraveler.data.entities.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class UserService {

    private static final String TAG_ADD = "add particpant";
    private static final String TAG_GET_ALL = "all particpants";
    private static final String TAG_GET_USER = "User with id";
    private static final String TAG_UPDATE_USER = "UPDATE user";
    private CollectionReference collectionReference;
    private User participant=null;

    public UserService(){
        FirebaseFirestore database =FirebaseFirestore.getInstance();
        this.collectionReference=database.collection("Users");
    }

    public List<User> getUsers(){
        final List<User> listParticipant=new ArrayList<User>();
        this.collectionReference.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                listParticipant.add(document.toObject(User.class));
                                Log.d(TAG_GET_ALL, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.w(TAG_GET_ALL, "Error getting documents.", task.getException());
                        }
                    }
                });
        return listParticipant;
    }

    public void addUser(User profil){
        this.collectionReference.document(profil.getId()).set(profil).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG_ADD,"Success the profil is added ");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG_ADD,"Error the profil does not added");
                    }
                });
    }

    public void getUserWithID(String id,final ResultAsynchronTaskUser resultAsynchronTasKUser){

        DocumentReference docRef=this.collectionReference.document(id);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.d(TAG_GET_USER,documentSnapshot.toString());
                participant = documentSnapshot.toObject(User.class);
                resultAsynchronTasKUser.onResponseReceived(participant);
            }
        });
    }

    public void updateUser(final FirebaseUser user,final String username, final String email, final String oldpassword, final String newpassword){
        final AuthCredential credential = EmailAuthProvider
                .getCredential(user.getEmail(), oldpassword);
        if (!user.getEmail().equals(email) && !email.equals("")) {
            user.reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            user.updateEmail(email)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG_UPDATE_USER, "User email address updated.");
                                            }
                                        }
                                    });
                        }
                    });
            collectionReference.document(user.getUid()).update("email",email);
        }
        if (!oldpassword.equals(newpassword) && !"".equals(newpassword)) {
            user.reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            user.updatePassword(newpassword)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG_UPDATE_USER, "User password updated.");
                                            }
                                        }
                                    });
                        }
                    });
        }
        if (!"".equals(username)) {
            user.reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG_UPDATE_USER, "Username updated.");
                            }
                        }
                    });
            collectionReference.document(user.getUid()).update("name",username);
        }

    }

    public void quitTrip(FirebaseUser user, final String id) {
        getUserWithID(user.getUid(), new ResultAsynchronTaskUser() {
            @Override
            public void onResponseReceived(User response) {
                response.getTripsID().remove(id);
            }
        });
    }
}
