package com.galarza.tibiacompendium.data;

import android.util.Log;

import java.text.ParseException;
import java.util.Date;

/**
 * Constains information about a character's death
 * @author Allan Galarza
 */
public class Death{
    private static final String TAG ="Death";

    private Date date;
    private boolean byPlayer;
    private String killer; //TODO: Replace with List of killers
    private int level;

    /**
     * @return String displaying the time of death in Tibia.com's format
     */
    public String getDateString(){
        return Utils.LONG_DATE.format(date);
    }

    /**
     * @param dateString Time and date when the date ocurred
     */
    public void setDate(String dateString){
        try{
            date = Utils.LONG_DATE.parse(dateString);
        }catch (ParseException e) {
            Log.e(TAG,"setDate: Couldn't parse date");
        }
    }

    /**
     * @return true if it was caused by a player, false if it was by a creature.
     */
    public boolean isByPlayer() {
        return byPlayer;
    }

    /**
     * @param byPlayer true if it was caused by a player, false if it was by a creature.
     */
    public void setByPlayer(boolean byPlayer) {
        this.byPlayer = byPlayer;
    }

    /**
     * @return the name of the killer
     */
    public String getKiller() {
        return killer;
    }

    /**
     * @param killer the name of the killer
     */
    public void setKiller(String killer) {
        this.killer = killer;
    }

    /**
     * @return the level at death
     */
    public int getLevel() {
        return level;
    }

    /**
     * @param level the level at death
     */
    public void setLevel(int level) {
        this.level = level;
    }
}