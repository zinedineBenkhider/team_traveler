package com.example.teamtraveler.presentation.trip.SwipeController.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.example.teamtraveler.R;
import com.example.teamtraveler.data.api.services.ActivityService;
import com.example.teamtraveler.data.api.services.HousingService;
import com.example.teamtraveler.data.api.services.TransportService;
import com.example.teamtraveler.data.api.services.TripService;
import com.example.teamtraveler.data.api.services.UserService;
import com.example.teamtraveler.presentation.trip.recyclerView.TripAdapter;
import com.google.firebase.auth.FirebaseAuth;

public class DialogQuitTrip extends Dialog {
    private Activity activity;
    private Dialog d;
    private TextView quit, cancel;
    private UserService userService;
    private TripService tripService;
    private ActivityService activityService;
    private HousingService housingService;
    private TransportService transportService;
    private String id;
    private String nametrip;
    private FirebaseAuth firebaseAuth;
    private TripAdapter tripAdapter;

    public DialogQuitTrip(Activity activity,TripAdapter tripAdapter, String id, String nameTrip) {
        super(activity);
        this.activity = activity;
        this.id = id;
        this.nametrip = nameTrip;
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.userService = new UserService();
        this.activityService = new ActivityService();
        this.housingService = new HousingService();
        this.transportService = new TransportService();
        this.tripService = new TripService();
        this.tripAdapter=tripAdapter;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_quitter_voyage);
        TextView nameTrip = findViewById(R.id.textView_name_voyage);
        nameTrip.setText(nametrip);
        quit = findViewById(R.id.text_view_quitter);
        cancel =  findViewById(R.id.textView_annuler);
        quit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                userService.quitTrip(firebaseAuth.getCurrentUser(),id);
                transportService.quitTransport(firebaseAuth.getCurrentUser(),id);
                housingService.quitHousing(firebaseAuth.getCurrentUser(),id);
                activityService.quitActivity(firebaseAuth.getCurrentUser(),id);
                tripService.quitTrip(firebaseAuth.getCurrentUser(),id);
                tripAdapter.remove(id);
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

