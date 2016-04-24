package com.galarza.tibiacompendium.data;

import java.text.ParseException;
import java.util.Date;

public class Death{
    private Date date;
    private boolean byPlayer;
    private String killer;
    private int level;

    public Death() {
    }

    public String toString(){
        return "Death: level = "+level+", killer = "+killer+", byPlayer = "+byPlayer+", date = "+
                date.toString();
    }

    public String getDateString(){
        return Utils.LONG_DATE.format(date);
    }

    public void setDate(String dateString){
        try{
            date = Utils.LONG_DATE.parse(dateString);
        }catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public boolean isByPlayer() {
        return byPlayer;
    }

    public void setByPlayer(boolean byPlayer) {
        this.byPlayer = byPlayer;
    }

    public String getKiller() {
        return killer;
    }

    public void setKiller(String killer) {
        this.killer = killer;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}