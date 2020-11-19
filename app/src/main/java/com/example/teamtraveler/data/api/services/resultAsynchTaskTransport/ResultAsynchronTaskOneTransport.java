package com.example.teamtraveler.data.api.services.resultAsynchTaskTransport;

import com.example.teamtraveler.data.entities.Transport;

public interface ResultAsynchronTaskOneTransport {
    void onResponseReceived(Transport response);
    void onNoResponseReceived();
}
