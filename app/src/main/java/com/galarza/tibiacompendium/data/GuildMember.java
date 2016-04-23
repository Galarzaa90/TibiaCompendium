package com.galarza.tibiacompendium.data;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GuildMember extends Player{
    private String rank;
    private int rankOrder;
    private String title;
    private Date joined = null;
    private boolean online;

    private static final DateFormat DATEFORMAT = new SimpleDateFormat("MMM dd yyyy",Locale.UK);

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public int getRankOrder() {
        return rankOrder;
    }

    public void setRankOrder(int rankOrder) {
        this.rankOrder = rankOrder;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getJoined() {
        return joined;
    }

    public void setJoined(Date joined) {
        this.joined = joined;
    }
    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public void setJoined(String joinedString){
        try {
            joined = DATEFORMAT.parse(joinedString);
        } catch (ParseException e) {
            Log.e("GuildMember","Couldn't parse date for GuildMember: "+this.getName());
            joined = null;
        }
    }

    public String getJoinedString(){
        return DATEFORMAT.format(joined);
    }
}
