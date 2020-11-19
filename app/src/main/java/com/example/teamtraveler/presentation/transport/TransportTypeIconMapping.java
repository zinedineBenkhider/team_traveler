package com.example.teamtraveler.presentation.transport;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import com.example.teamtraveler.R;

public class TransportTypeIconMapping {
    
    public static Drawable getIconOfType(Resources resources, String type){
        if(type.equals(resources.getString(R.string.type_transport_car))){
            return resources.getDrawable(R.drawable.ic_directions_car_darkgrey_24dp);
        }
        else if(type.equals(resources.getString(R.string.type_transport_train))){
            return resources.getDrawable(R.drawable.ic_train);
        }
        else if (type.equals(resources.getString(R.string.type_transport_airplane))){
            return resources.getDrawable(R.drawable.ic_airplanemode);
        }

        else if(type.equals(resources.getString(R.string.type_transport_bus))) {
            return resources.getDrawable(R.drawable.ic_bus);
        }
        else {
            return resources.getDrawable(R.drawable.ic_spaceship);
        }
    }
}
