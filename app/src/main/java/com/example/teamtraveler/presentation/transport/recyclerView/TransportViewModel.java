package com.example.teamtraveler.presentation.transport.recyclerView;

import android.app.Activity;
import android.app.Dialog;

import com.example.teamtraveler.data.entities.Transport;

public class TransportViewModel {
    private Transport transport;
    private String nameOwner;
    private Activity activity;
    private boolean suppr;
    private TransportAdapter transportAdapter;

    public TransportViewModel(Transport transport, String nameOwner, boolean suppr, Activity activity,TransportAdapter transportAdapter) {
        this.transport = transport;
        this.nameOwner = nameOwner;
        this.suppr = suppr;
        this.activity = activity;
        this.transportAdapter =transportAdapter;
    }

    public Transport getTransport() {
        return transport;
    }

    public void setTransport(Transport transport) {
        this.transport = transport;
    }

    public String getNameOwner() {
        return nameOwner;
    }

    public void setNameOwner(String nameOwner) {
        this.nameOwner = nameOwner;
    }

    public boolean getSuppr() {
        return suppr;
    }

    public Activity getActivity() {
        return activity;
    }

    public TransportAdapter getTransportAdapter() {
        return transportAdapter;
    }
}
