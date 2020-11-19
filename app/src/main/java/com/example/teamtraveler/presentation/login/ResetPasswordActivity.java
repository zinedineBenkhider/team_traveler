package com.example.teamtraveler.presentation.login;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.example.teamtraveler.R;
import com.example.teamtraveler.Utils.BaseActivity;
import com.example.teamtraveler.data.api.services.UserService;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends BaseActivity {

    private EditText email;
    private Button btn_envoyer;
    private FirebaseAuth firebaseAuth;
    private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth= FirebaseAuth.getInstance();
        userService = new UserService();
        setContentView(R.layout.activity_reset_password);
        email = findViewById(R.id.editText_email_reset_password);
        btn_envoyer = findViewById(R.id.btn_send_reset_password);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        btn_envoyer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if("".equals(email.getText().toString())){
                    email.setError(getResources().getString(R.string.give_valid_psw));
                }else{
                    firebaseAuth.sendPasswordResetEmail(email.getText().toString());
                    Toast.makeText(ResetPasswordActivity.this, getResources().getString(R.string.email_reset_psw_sended), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
        findViewById(R.id.button_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }



}
