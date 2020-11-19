package com.example.teamtraveler.presentation.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.example.teamtraveler.R;
import com.example.teamtraveler.Utils.BaseActivity;
import com.example.teamtraveler.Utils.LockScreenActivity;
import com.example.teamtraveler.data.api.services.UserService;
import com.example.teamtraveler.data.api.services.resultAsynchTaskUser.ResultAsynchronTaskUser;
import com.example.teamtraveler.data.entities.User;
import com.example.teamtraveler.presentation.login.LoginActivity;
import com.example.teamtraveler.presentation.trip.ListTripActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends LockScreenActivity implements ResultAsynchronTaskUser {

    private TextView email;
    private TextView username;
    private ImageButton editEmail_btn;
    private EditText editEmail;
    private ImageButton edit_pass_btn;
    private EditText edit_email_pass;
    private EditText edit_username;
    private EditText edit_pass_pre;
    private EditText edit_pass_new1;
    private EditText edit_username_pass;
    private EditText edit_pass_new2;
    private TextView password;
    private Button saveedit;
    private FirebaseAuth firebaseAuth;
    private UserService userService;
    private ImageView btn_edit_username;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth= FirebaseAuth.getInstance();

        userService = new UserService();
        userService.getUserWithID(firebaseAuth.getCurrentUser().getUid(),this);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        email = findViewById(R.id.email_field_profile);
        edit_email_pass = findViewById(R.id.editText_email_pass);
        username = findViewById(R.id.name_field_profile);
        password = findViewById(R.id.password_field_profile);
        editEmail_btn = findViewById(R.id.btn_edit_email_profile);
        edit_pass_btn = findViewById(R.id.btn_edit_pass_profile);
        saveedit = findViewById(R.id.save_edit_profil);
        edit_username = findViewById(R.id.editText_username_profile);
        edit_username_pass = findViewById(R.id.editText_username_pass_profile);
        editEmail = findViewById(R.id.editText_email_profile);
        editEmail.setHint(firebaseAuth.getCurrentUser().getEmail());
        edit_pass_pre = findViewById(R.id.editText_pass_pre_profile);
        edit_pass_new1 = findViewById(R.id.editText_pass_new1_profile);
        edit_pass_new2 = findViewById(R.id.editText_pass_new2_profile);
        email.setText(firebaseAuth.getCurrentUser().getEmail());
        btn_edit_username = findViewById(R.id.btn_edit_username_profile);
        findViewById(R.id.button_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btn_edit_username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btn_edit_username.getTag().equals("edit")){
                    btn_edit_username.setTag("stop");
                    edit_username.setVisibility(View.VISIBLE);
                    username.setVisibility(View.INVISIBLE);
                    displayBtnEnregistrer();
                    edit_username.setHint(user.getName());
                    btn_edit_username.setImageDrawable(getResources().getDrawable(R.drawable.ic_x));
                }
                else{
                    edit_username.setVisibility(View.INVISIBLE);
                    username.setVisibility(View.VISIBLE);
                    btn_edit_username.setTag("edit");
                    btn_edit_username.setImageDrawable(getResources().getDrawable(R.drawable.ic_edit));
                    displayBtnEnregistrer();
                }
                displaypass();
            }
        });
        editEmail_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editEmail_btn.getTag().equals("edit")) {
                    editEmail_btn.setTag("stop");
                    editEmail_btn.setImageDrawable(getResources().getDrawable(R.drawable.ic_x));
                    editEmail.setVisibility(View.VISIBLE);
                    email.setVisibility(View.INVISIBLE);
                    displayBtnEnregistrer();
                }
                else{
                    editEmail_btn.setTag("edit");
                    editEmail_btn.setImageDrawable(getResources().getDrawable(R.drawable.ic_edit));
                    editEmail.setVisibility(View.INVISIBLE);
                    email.setVisibility(View.VISIBLE);
                    displayBtnEnregistrer();
                }
                displaypass();
            }
        });
        edit_pass_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edit_pass_btn.getTag().equals("edit")) {
                    edit_pass_btn.setTag("stop");
                    edit_pass_btn.setImageDrawable(getResources().getDrawable(R.drawable.ic_x));
                    password.setVisibility(View.INVISIBLE);
                    edit_pass_new1.setVisibility(View.VISIBLE);
                    edit_pass_new1.setHint("Nouveau mot de passe");
                    edit_pass_new2.setVisibility(View.VISIBLE);
                    edit_pass_new2.setHint("Vérification nouveau");
                    displayBtnEnregistrer();
                }
                else{
                    edit_pass_btn.setTag("edit");
                    edit_pass_btn.setImageDrawable(getResources().getDrawable(R.drawable.ic_edit));
                    password.setVisibility(View.VISIBLE);
                    edit_pass_new1.setVisibility(View.INVISIBLE);
                    edit_pass_new2.setVisibility(View.INVISIBLE);
                    displayBtnEnregistrer();
                }
                displaypass();
            }
        });
        saveedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = edit_username.getText().toString();

                if (isValidEmail() || isValidPassword() || isValidUser()){
                    String oldpass =getpass();
                    userService.updateUser(firebaseAuth.getCurrentUser(),username, editEmail.getText().toString(), oldpass, edit_pass_new1.getText().toString());
                    Intent intent = new Intent(getBaseContext(),ListTripActivity.class);
                    startActivity(intent);
                }
            }
        });
        View activityview =findViewById(R.id.constraintLayoutProfile);
        activityview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edit_username.getWindowToken(), 0);
                return true;
            }
        });
    }

    private String getpass() {
        if ("".equals(edit_username_pass.getText().toString())){
            if ("".equals(edit_email_pass.getText().toString())){
                return edit_pass_pre.getText().toString();
            }else{
                return edit_email_pass.getText().toString();
            }
        }
        else{
            return edit_username_pass.getText().toString();
        }
    }

    private boolean isValidUser() {
        if("edit".equals(btn_edit_username.getTag())){
            return false;
        }
        else{
            if("".equals(edit_username.getText().toString())) {
                edit_username.setError("Veuillez entrez un nom !");
                return false;
            }
            if(edit_username.getText().toString().equals(user.getEmail())){
                return false;
            }
            if ("".equals(edit_username_pass.getText().toString())){
                edit_username_pass.setError("Veuillez entrez votre mot de passe !");
                return false;
            }
            return true;
        }
    }

    private boolean isValidEmail(){
        String editEmail_btn_s =  (String) editEmail_btn.getTag();
        if("edit".equals(editEmail_btn_s)){
            return false;
        }
        else{
            if("".equals(editEmail.getText().toString())){
                editEmail.setError("Veuillez entrez un email !");
                return false;
            }
            if (editEmail.getText().toString().equals(user.getEmail())){
                return false;
            }
            if ("".equals(edit_email_pass.getText().toString())){
                edit_email_pass.setError("Veuillez entrez votre mot de passe !");
                return false;
            }
            return true;
        }
    }

    private boolean isValidPassword(){
        if(edit_pass_btn.getTag().equals("edit")){
            return false;
        }
        else{
            if("".equals(edit_pass_pre.getText().toString())){
                editEmail.setError("Veuillez entrez votre mot de passe !");
                return false;
            }
            if("".equals(edit_pass_new1.getText().toString())){
                editEmail.setError("Veuillez entrez un nouveau mot de passe !");
                return false;
            }
            if("".equals(edit_pass_new2.getText().toString())){
                editEmail.setError("Veuillez entrez le nouveau mot de passe !");
                return false;
            }
            if(!edit_pass_new1.equals(edit_pass_new2)){
                return false;
            }
            return true;
        }
    }

    private void displaypass(){
        if (btn_edit_username.getTag().equals("stop")){
            edit_username_pass.setVisibility(View.VISIBLE);
            edit_username_pass.setHint("Mot de passe");
            edit_email_pass.setVisibility(View.INVISIBLE);
            edit_pass_pre.setVisibility(View.INVISIBLE);
        }
        else{
            edit_username_pass.setVisibility(View.INVISIBLE);
            if(editEmail_btn.getTag().equals("stop")){
                edit_email_pass.setVisibility(View.VISIBLE);
                edit_email_pass.setHint("Mot de passe");
                edit_pass_pre.setVisibility(View.INVISIBLE);
            }
            else{
                edit_email_pass.setVisibility(View.INVISIBLE);
                if (edit_pass_btn.getTag().equals("stop")){
                    edit_pass_pre.setVisibility(View.VISIBLE);
                    edit_pass_pre.setHint("Mot de passe");
                }
                else{
                    edit_pass_pre.setVisibility(View.INVISIBLE);
                }
            }
        }
    }
    private void displayBtnEnregistrer() {
        if(btn_edit_username.getTag().equals("edit") && editEmail_btn.getTag().equals("edit") && edit_pass_btn.getTag().equals("edit")){
            saveedit.setVisibility(View.INVISIBLE);
        }
        else{
            saveedit.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onResponseReceived(User response) {
        user = response;
        username.setText(response.getName());
        edit_username.setHint(response.getName());
        findViewById(R.id.progressBar_profile).setVisibility(View.INVISIBLE);
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
            return false;
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
