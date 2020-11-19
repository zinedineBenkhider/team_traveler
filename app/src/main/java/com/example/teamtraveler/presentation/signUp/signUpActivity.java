package com.example.teamtraveler.presentation.signUp;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.teamtraveler.R;
import com.example.teamtraveler.Utils.LockScreenActivity;
import com.example.teamtraveler.data.entities.User;
import com.example.teamtraveler.data.api.services.UserService;
import com.example.teamtraveler.presentation.trip.ListTripActivity;
import com.example.teamtraveler.presentation.login.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class signUpActivity extends LockScreenActivity {

    private EditText emailTextView,passwordTextView,passworConfirmationdTextView,identifiantTextView;
   private Button registerButton;
    private FirebaseAuth firebaseAuth;
    private UserService profileService;
    private ImageButton btnBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        emailTextView = (EditText) findViewById(R.id.user_email_register);
        btnBack=findViewById(R.id.button_back_signUp);
        passwordTextView = (EditText) findViewById(R.id.user_password_register);
        passworConfirmationdTextView = (EditText) findViewById(R.id.user_confirmation_password_register);
        registerButton = (Button) findViewById(R.id.btn_register);
        identifiantTextView=(EditText) findViewById(R.id.user_identifiant_register);
        firebaseAuth = FirebaseAuth.getInstance();
        profileService =new UserService();
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = identifiantTextView.getText().toString();
                final String email = emailTextView.getText().toString();
                String password = passwordTextView.getText().toString();
                String passwordConfirmation = passworConfirmationdTextView.getText().toString();
                if(checkFields( email, password, passwordConfirmation, name)){
                        firebaseAuth.createUserWithEmailAndPassword(email,password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Resources resources=getResources();
                                if(task.isSuccessful()){
                                    Toast.makeText(getApplicationContext(),resources.getString(R.string.msg_register_success_register),Toast.LENGTH_SHORT).show();
                                    FirebaseUser currentUser=firebaseAuth.getCurrentUser();
                                    User profil =new User(name,  email,currentUser.getUid());
                                    profileService.addUser(profil);
                                    startActivity(new Intent(getApplicationContext(), ListTripActivity.class));
                                    finish();
                                }
                                else{
                                    Toast.makeText(getApplicationContext(),resources.getString(R.string.msg_register_failed_register),Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                }
        }});


    }

    //Il faut ajouter d'autres  verifications en créant des fonctions qui verifient si un mot de passe à le bon format etc.!!!!!
    public boolean checkFields(String email,String password,String passwordConfirmation,String identifiant){
        Resources resources=getResources();
        if (TextUtils.isEmpty(identifiant)) {
            Toast.makeText(getApplicationContext(),resources.getString(R.string.msg_ident_required_register),Toast.LENGTH_SHORT).show();
            return false;
        }

        else if(TextUtils.isEmpty(email)){
            Toast.makeText(getApplicationContext(),resources.getString(R.string.msg_email_required_regiter),Toast.LENGTH_SHORT).show();
            return false;
        }

        else if(TextUtils.isEmpty(password)){
            Toast.makeText(getApplicationContext(),resources.getString(R.string.msg_psw_required_register),Toast.LENGTH_SHORT).show();
            return false;
        }

        else if(password.length()<6){
            Toast.makeText(getApplicationContext(),resources.getString(R.string.msg_psw_lenth_not_respected_register),Toast.LENGTH_SHORT).show();
            return false;
        }

        else if(!password.equals(passwordConfirmation)){
            Toast.makeText(getApplicationContext(),resources.getString(R.string.msg_psw_confrm_required_register),Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;

    }

}
