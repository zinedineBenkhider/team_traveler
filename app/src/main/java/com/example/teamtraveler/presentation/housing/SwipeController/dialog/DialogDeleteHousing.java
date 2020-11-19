package com.example.teamtraveler.presentation.housing.SwipeController.dialog;

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
import com.example.teamtraveler.presentation.activities.recyclerView.ActivityAdapter;
import com.example.teamtraveler.presentation.housing.recyclerView.HousingAdapter;
import com.google.firebase.auth.FirebaseAuth;

public class DialogDeleteHousing extends Dialog {
    private Activity activity;
    private Dialog d;
    private TextView delete, cancel;
    private HousingService housingService;
    private String id;
    private String namehousing;
    private FirebaseAuth firebaseAuth;
    private HousingAdapter housingAdapter;

    public DialogDeleteHousing(Activity activity, HousingAdapter housingAdapter, String id, String namehousing) {
        super(activity);
        this.activity = activity;
        this.id = id;
        this.namehousing = namehousing;
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.housingService = new HousingService();
        this.housingAdapter=housingAdapter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_supprimer_housing);
        TextView nameHousing = findViewById(R.id.textView_name_housing);
        nameHousing.setText(namehousing);
        delete = findViewById(R.id.text_view_quitter);
        cancel =  findViewById(R.id.textView_annuler);
        delete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                housingService.deleteHousing(firebaseAuth.getCurrentUser(),id);
                housingAdapter.remove(id);
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

