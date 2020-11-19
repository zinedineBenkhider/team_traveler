package com.example.teamtraveler.presentation.transport.recyclerView;

import android.widget.ImageButton;
import android.widget.TextView;

import com.example.teamtraveler.data.entities.Transport;

public interface TransportActionInterface {

    void joinExitTransportClick(Transport transport, ImageButton imageButton,TextView textView);
    void initJoinExitTransportBtn(Transport transport, ImageButton imageButton, TextView textView);
    void transportClick(Transport transport);
}
