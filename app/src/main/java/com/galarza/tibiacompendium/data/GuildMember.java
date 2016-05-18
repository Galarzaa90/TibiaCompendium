package com.galarza.tibiacompendium.data;

import android.util.Log;

import java.text.ParseException;
import java.util.Date;

/**
 * Subtype of Player class that contains a guild member's information
 * @author Allan Galarza
 */
public class GuildMember extends Player {
    private static final String TAG ="GuildMember";
    private String rank;
    private int rankOrder;
    private String title;
    private Date joined = null;
    private boolean online;

    /**
     * @return the rank of the member
     */
    public String getRank() {
        return rank;
    }

    /**
     * @param rank the rank of the member
     */
    public void setRank(String rank) {
        this.rank = rank;
    }

    /**
     * @return the rank's order
     */
    public int getRankOrder() {
        return rankOrder;
    }

    /**
     * @param rankOrder the rank's order
     */
    public void setRankOrder(int rankOrder) {
        this.rankOrder = rankOrder;
    }

    /**
     * @return the title of the member
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title of the member
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the date the member joined
     */
    public Date getJoined() {
        return joined;
    }

    /**
     * @return true if the member is online
     */
    public boolean isOnline() {
        return online;
    }

    /**
     * @param online true if the member is online
     */
    public void setOnline(boolean online) {
        this.online = online;
    }

    /**
     * @param joinedString date when the member joined
     */
    public void setJoined(String joinedString){
        try {
            joined = Utils.SHORT_DATE.parse(joinedString);
        } catch (ParseException e) {
            Log.e(TAG,"setJoined: Couldn't parse date for GuildMember: "+this.getName());
            joined = null;
        }
    }

    /**
     * @return String displaying the date the member joined
     */
    public String getJoinedString(){
        return Utils.SHORT_DATE.format(joined);
    }
}
