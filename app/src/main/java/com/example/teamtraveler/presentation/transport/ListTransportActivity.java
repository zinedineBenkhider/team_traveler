package com.example.teamtraveler.presentation.transport;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.teamtraveler.R;
import com.example.teamtraveler.Utils.BaseActivity;
import com.example.teamtraveler.data.api.services.TransportService;
import com.example.teamtraveler.data.api.services.UserService;
import com.example.teamtraveler.data.api.services.resultAsynchTaskTransport.ResultAsynchronTaskOneTransport;
import com.example.teamtraveler.data.api.services.resultAsynchTaskUser.ResultAsynchronTaskUser;
import com.example.teamtraveler.data.entities.Transport;
import com.example.teamtraveler.data.entities.User;
import com.example.teamtraveler.presentation.transport.recyclerView.TransportActionInterface;
import com.example.teamtraveler.presentation.transport.recyclerView.TransportAdapter;
import com.example.teamtraveler.presentation.transport.recyclerView.TransportViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Map;

public class ListTransportActivity extends BaseActivity implements TransportActionInterface {

    private TransportService transportService;
    private UserService userService;
    private RecyclerView recyclerView;
    private TransportAdapter transportAdapter;
    private FirebaseAuth firebaseAuth;
    private FloatingActionButton btnNewTransport;
    private ProgressBar progressBar;
    public static final String ID_TRIP_LIST_TRANSPORT="com.example.teamtraveler.presentation.transport.ID_TRIP_LIST_TRANSPORT";
    public static final String NAME_TRIP_LIST_TRANSPORT="com.example.teamtraveler.presentation.transport.NAME_TRIP_LIST_TRANSPORT";
    private TextView nameTripTextView;
    public final static int REQUEST_REFRESH_TRANSPORT_LIST=4;
    private String idTrip;
    private String nameTrip;
    private FirebaseUser firebaseUser;
    private ImageButton backImageButton;
    private ImageButton deleteImageButton;
    private TextView no_transport_yet;
    private boolean delete;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_transport);
        nameTripTextView=findViewById(R.id.name_trip_list_transport);
        backImageButton=findViewById(R.id.backButton_list_transport);
        deleteImageButton=findViewById(R.id.btn_list_supprimer_transport);
        transportService=new TransportService();
        userService=new UserService();
        no_transport_yet = findViewById(R.id.no_transport_yet);
        recyclerView=findViewById(R.id.recycler_view_list_transport);
        transportAdapter=new TransportAdapter(this);
        recyclerView.setAdapter(transportAdapter);
        Intent intent=getIntent();
        nameTrip=intent.getStringExtra(NAME_TRIP_LIST_TRANSPORT);
        nameTripTextView.setText(nameTrip);
        firebaseAuth=FirebaseAuth.getInstance();
        progressBar=findViewById(R.id.progress_bar_list_transport);
        firebaseUser= firebaseAuth.getCurrentUser();
        idTrip=intent.getStringExtra(ID_TRIP_LIST_TRANSPORT);
        Toolbar toolbar = findViewById(R.id.toolbar_list_transport);
        setSupportActionBar(toolbar);
        btnNewTransport=findViewById(R.id.btn_new_transport_list_transport);
        btnNewTransport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NewTransportActivity.class);
                intent.putExtra(NewTransportActivity.ID_TRIP_NEW_TRANSPORT,idTrip);
                intent.putExtra(NAME_TRIP_LIST_TRANSPORT,nameTrip);
                startActivityForResult(intent,REQUEST_REFRESH_TRANSPORT_LIST);
            }
        });
        setupRecyclerView();
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        backImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        deleteImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete = !delete;
                deleteImageButton.setEnabled(false);
                setupRecyclerView();
            }
        });
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
    }



    public void setupRecyclerView(){
        progressBar.setVisibility(View.VISIBLE);
        transportAdapter.emptyDataSet();
        deleteImageButton.setVisibility(View.VISIBLE);
        final Activity activity = this;
        transportService.getTransportsOfTrip(idTrip, new ResultAsynchronTaskOneTransport() {
            @Override
            public void onResponseReceived(final Transport transport) {

                userService.getUserWithID(transport.getIdUserOwner(), new ResultAsynchronTaskUser() {
                    @Override
                    public void onResponseReceived(User user) {
                        TransportViewModel transportViewModel;
                        if (user.getId().equals(firebaseUser.getUid())) {
                            transportViewModel = new TransportViewModel(transport, getResources().getString(R.string.transport_proposed_by_you),delete,activity,transportAdapter);
                        }

                        else {
                            transportViewModel = new TransportViewModel(transport, user.getName(),delete,activity,transportAdapter);
                        }
                        transportAdapter.bindViewModel(transportViewModel);
                        no_transport_yet.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        deleteImageButton.setEnabled(true);
                    }
                });
            }

            @Override
            public void onNoResponseReceived() {
                progressBar.setVisibility(View.GONE);
                no_transport_yet.setVisibility(View.VISIBLE);
                deleteImageButton.setVisibility(View.INVISIBLE);
            }

        });

    }


    @Override
    public void joinExitTransportClick(Transport transport, ImageButton imageButton,TextView textView) {
        String userId= firebaseAuth.getCurrentUser().getUid();
        String userOpinion=transport.getUsersParticipations().get(userId);
        String joinTransport=getString(R.string.join_transport_opinion);
        String exitTransport=getString(R.string.exit_transport);
        Map<String,String> usersParticipation=transport.getUsersParticipations();
        if(usersParticipation.containsKey(userId)) {
            if (userOpinion.equals(joinTransport)) {
                usersParticipation.put(userId, exitTransport);
                imageButton.setBackgroundColor(getResources().getColor(R.color.color_participer_transport));
                textView.setText(getString(R.string.join_transport_txt_btn));

            } else {
                usersParticipation.put(userId, joinTransport);
                imageButton.setBackgroundColor(getResources().getColor(R.color.color_quitter_transport));
                textView.setText(getString(R.string.exit_transport_txt_btn));
            }
        }
        else {
            usersParticipation.put(userId, joinTransport);
            imageButton.setBackgroundColor(getResources().getColor(R.color.color_quitter_transport));
            textView.setText(getString(R.string.exit_transport_txt_btn));
        }

        transportService.updateOpinionOfParticipant(transport.getId(),usersParticipation);
    }


    @Override
    public void initJoinExitTransportBtn(Transport transport, ImageButton imageButton, TextView textView) {
        String userId = firebaseAuth.getCurrentUser().getUid();
        String userOpinion = transport.getUsersParticipations().get(userId);
        String joinTransport = getString(R.string.join_transport_opinion);
        Map<String, String> usersParticipation = transport.getUsersParticipations();
        if (usersParticipation.containsKey(userId)) {
            if (userOpinion.equals(joinTransport)) {
                imageButton.setBackgroundColor(getResources().getColor(R.color.color_quitter_transport));
                textView.setText(getString(R.string.exit_transport_txt_btn));

            } else {
                imageButton.setBackgroundColor(getResources().getColor(R.color.color_participer_transport));
                textView.setText(getString(R.string.join_transport_txt_btn));
            }
        } else {
            imageButton.setBackgroundColor(getResources().getColor(R.color.color_participer_transport));
            textView.setText(getString(R.string.join_transport_txt_btn));
        }
    }

    @Override
    public void transportClick(Transport transport) {
        if(transport.getIdUserOwner().equals(firebaseAuth.getCurrentUser().getUid())){
            Intent intent = new Intent(this, NewTransportActivity.class);
            intent.putExtra(NAME_TRIP_LIST_TRANSPORT,nameTrip);
            intent.putExtra(NewTransportActivity.ID_TRIP_UPDATE_TRANSPORT,idTrip);
            intent.putExtra(NewTransportActivity.ID_TRANSPORT_NEW_TRANSPORT,transport.getId());
            this.startActivityForResult(intent,REQUEST_REFRESH_TRANSPORT_LIST);
        }
        else{
            Intent intent = new Intent(this, DetailTransportActivity.class);
            intent.putExtra(NAME_TRIP_LIST_TRANSPORT,nameTrip);
            intent.putExtra(DetailTransportActivity.ID_TRIP_DETAIL_TRANSPORT,idTrip);
            intent.putExtra(DetailTransportActivity.ID_TRANSPORT_DETAIL_TRANSPORT,transport.getId());
            this.startActivityForResult(intent,REQUEST_REFRESH_TRANSPORT_LIST);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==Activity.RESULT_OK && requestCode==REQUEST_REFRESH_TRANSPORT_LIST){
            setupRecyclerView();
        }
    }


}
