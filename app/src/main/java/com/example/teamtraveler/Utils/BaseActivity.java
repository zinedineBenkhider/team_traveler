package com.example.teamtraveler.Utils;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.teamtraveler.R;
import com.example.teamtraveler.presentation.trip.ListTripActivity;
import com.example.teamtraveler.presentation.login.LoginActivity;
import com.example.teamtraveler.presentation.profile.ProfileActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class BaseActivity extends LockScreenActivity {

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth=FirebaseAuth.getInstance();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        FirebaseUser currentUser=firebaseAuth.getCurrentUser();
        if(currentUser!=null){
            getMenuInflater().inflate(R.menu.menu_user_online, menu);
        }else {
            getMenuInflater().inflate(R.menu.menu_user_offline, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)  {
        int id = item.getItemId();

        if (id == R.id.action_connexion) {
            Intent intent = new Intent(this, LoginActivity.class);
            this.startActivity(intent);
            finish();
        }
        else if(id == R.id.action_profil){
            Intent intent = new Intent(this, ProfileActivity.class);
            this.startActivity(intent);
        }


        else if(id == R.id.action_log_off){
            firebaseAuth.signOut();
            Toast toast = Toast.makeText(this, "Vous etes déconnecté", Toast.LENGTH_LONG);
            toast.show();
            Intent intent = new Intent(this, ListTripActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


}
