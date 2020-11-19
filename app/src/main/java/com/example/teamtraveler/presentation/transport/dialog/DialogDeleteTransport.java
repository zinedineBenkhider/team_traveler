package com.example.teamtraveler.presentation.transport.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.example.teamtraveler.R;
import com.example.teamtraveler.data.api.services.ActivityService;
import com.example.teamtraveler.data.api.services.TransportService;
import com.example.teamtraveler.presentation.activities.recyclerView.ActivityAdapter;
import com.example.teamtraveler.presentation.transport.recyclerView.TransportAdapter;
import com.google.firebase.auth.FirebaseAuth;

public class DialogDeleteTransport extends Dialog {
    private Activity activity;
    private Dialog d;
    private TextView delete, cancel;
    private TransportService transportService;
    private String id;
    private String nameTransport;
    private FirebaseAuth firebaseAuth;
    private TransportAdapter transportAdapter;

    public DialogDeleteTransport(Activity activity, TransportAdapter transportAdapter, String id, String nameTransport) {
        super(activity);
        this.activity = activity;
        this.id = id;
        this.nameTransport = nameTransport;
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.transportService = new TransportService();
        this.transportAdapter = transportAdapter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_supprimer_transport);
        TextView nameTrans = findViewById(R.id.textView_name_transport);
        nameTrans.setText(nameTransport);
        delete = findViewById(R.id.text_view_quitter);
        cancel =  findViewById(R.id.textView_annuler);
        delete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                transportService.deleteTransport(firebaseAuth.getCurrentUser(),id);
                transportAdapter.remove(id);
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

