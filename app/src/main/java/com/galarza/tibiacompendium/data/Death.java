package com.galarza.tibiacompendium.data;

import java.util.Date;

public class Death{
    private Date date;
    private String dateString;
    private boolean byPlayer;
    private String killer;
    private int level;

    public Death() {
    }

    public String toString(){
        return "Death: level = "+level+", killer = "+killer+", byPlayer = "+byPlayer+", date = "+
                date.toString();
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
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