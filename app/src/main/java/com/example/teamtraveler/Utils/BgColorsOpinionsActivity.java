package com.example.teamtraveler.Utils;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import com.example.teamtraveler.R;

/**
 * class for manipulate the background colors corresponding to the opinion
 */
public class BgColorsOpinionsActivity {

    /**
     * return the color defined for the opinion
     * @param opinion the opinion of the participants
     * @param resources the ressources to find the color
     * @return the color to print
     */
    public static ColorStateList getColorOfOpinion(String opinion,Resources resources ){
        if (opinion!=null) {
            if (opinion.equals(resources.getString(R.string.opinion_interested))) {
                return resources.getColorStateList(R.color.color_chip_interested);
            } else if (opinion.equals(resources.getString(R.string.opinion_participate))) {
                return resources.getColorStateList(R.color.color_chip_participate);
            } else if (opinion.equals(resources.getString(R.string.opinion_not_participate))) {
                return resources.getColorStateList(R.color.color_chip_not_participate);
            } 
            else if(opinion.equals(resources.getString(R.string.without_opinion))){
                return resources.getColorStateList(R.color.color_chip_without_opinion);
                    }
            else {
                return resources.getColorStateList(R.color.color_chip_participants_detail_trip);

            }
        }
        else {
            return resources.getColorStateList(R.color.color_chip_without_opinion);
        }

    }

    /**
     * return the color defined for the opinion
     * @param opinion the opinion of the participants
     * @param resources the ressources to find the color
     * @return the drawable to print
     */
    public static Drawable getColorOfOpiniondrawable(String opinion, Resources resources ){
        if (opinion!=null) {
            if (opinion.equals(resources.getString(R.string.opinion_interested))) {
                return resources.getDrawable(R.drawable.background_chip_interest);
            } else if (opinion.equals(resources.getString(R.string.opinion_participate))) {
                return resources.getDrawable(R.drawable.background_chip_participate);
            } else if (opinion.equals(resources.getString(R.string.opinion_not_participate))) {
                return resources.getDrawable(R.drawable.background_chip_not_participate);
            }
            else if(opinion.equals(resources.getString(R.string.without_opinion))){
                return resources.getDrawable(R.drawable.background_chip_without_opinion);
            }
            else {
                return resources.getDrawable(R.drawable.background_chip_interest);

            }
        }
        else {
            return resources.getDrawable(R.drawable.background_chip_without_opinion);
        }

    }
}
