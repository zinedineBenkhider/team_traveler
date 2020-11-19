package com.example.teamtraveler.Utils;

import com.example.teamtraveler.R;

/**
 * class for mapping the activity type to the icon needed
 */
public class TypeActivityMapping {


    /**
     * retur nthe corresponding icon's id from the type activity
     * @param typeActivity the type of the activity
     * @return the icon's id
     */
    public static int getImgActivity(String typeActivity){
        if("Sport".equals(typeActivity)){
            return R.drawable.ic_sports;
        }
        else if ("Jeu".equals(typeActivity)){
            return R.drawable.ic_chess;
        }
        else if ("Spectacle".equals(typeActivity)){
            return R.drawable.ic_theater;
        }
        else if ("Visite".equals(typeActivity)){
            return R.drawable.ic_visite;
        }
        else if ("Restaurant".equals(typeActivity)){
            return R.drawable.ic_restaurant;
        }
        else if ("Autre".equals(typeActivity)){
            return R.drawable.ic_group_black_24dp;
        }
        else {
            return R.drawable.ic_group_black_24dp;
        }
    }

}
