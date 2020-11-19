package com.example.teamtraveler.Utils;


/**
 * class for matching note and appreciation
 */
public class NoteAppreciationMapping {


    /**
     * return the corresponding appreciation from a note
     * @param note the note
     * @return the appreciation (a string)
     */
    public static String getAppreciationOfNote(float note){
        if(note<1){
            return "Trés mauvais";
        }
        else if (note<1.5){
            return "Médiocre";
        }
        else if (note<2){
            return "Mauvais";
        }
        else if (note<3){
            return "Moyen";
        }
        else if (note<4){
            return "Bon";
        }
        else if (note<5){
            return "Trés bon";
        }
        else {
            return "Parfait";
        }
    }
}
