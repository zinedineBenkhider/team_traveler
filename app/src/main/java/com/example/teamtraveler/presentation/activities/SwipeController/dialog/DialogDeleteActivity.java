package com.example.teamtraveler.presentation.activities.SwipeController.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.example.teamtraveler.R;
import com.example.teamtraveler.data.api.services.ActivityService;
import com.example.teamtraveler.data.api.services.TripService;
import com.example.teamtraveler.presentation.activities.recyclerView.ActivityAdapter;
import com.google.firebase.auth.FirebaseAuth;

public class DialogDeleteActivity extends Dialog {
    private Activity activity;
    private Dialog d;
    private TextView delete, cancel;
    private ActivityService activityService;
    private TripService tripService;
    private String id;
    private String idTrip;
    private String nameActivity;
    private FirebaseAuth firebaseAuth;
    private ActivityAdapter activityAdapter;

    public DialogDeleteActivity(Activity activity, String idTrip, ActivityAdapter activityAdapter, String id, String nameActivity) {
        super(activity);
        this.activity = activity;
        this.id = id;
        this.nameActivity = nameActivity;
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.activityService = new ActivityService();
        this.activityAdapter=activityAdapter;
        this.tripService=new TripService();
        this.idTrip=idTrip;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_supprimer_activity);
        TextView nameTrip = findViewById(R.id.textView_name_activity);
        nameTrip.setText(nameActivity);
        delete = findViewById(R.id.text_view_quitter);
        cancel =  findViewById(R.id.textView_annuler);
        delete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                activityService.deleteActivity(firebaseAuth.getCurrentUser(),id);
                activityAdapter.remove(id);
                tripService.deleteActivityToTrip(idTrip,id);
                dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

}

