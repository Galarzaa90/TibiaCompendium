package com.galarza.tibiacompendium.data;

import android.util.Log;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Constains information and functions related to Tibia guilds
 *
 * @author Allan Galarza
 */
public class Guild {

    private String name;
    private String logoUrl;
    private String world;
    private Date founded;
    private final List<GuildMember> memberList = new ArrayList<>();

    /**
     * @return the name of the guild
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name of the guild
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the URL of the logo of the guild
     */
    public String getLogoUrl() {
        return logoUrl;
    }

    /**
     * @param logoUrl the URL of the logo of the guild
     */
    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    /**
     * @return the name of the world where the guild is
     */
    public String getWorld() {
        return world;
    }

    /**
     * @param world name of the world where the guild is
     */
    public void setWorld(String world) {
        this.world = world;
    }

    /**
     * @return a {@code List} of all the guild members
     */
    public List<GuildMember> getMemberList() {
        return memberList;
    }

    /**
     * @return String displaying the date the guild was founded
     */
    public String getFoundedString(){
        return Utils.SHORT_DATE.format(founded);
    }

    /**
     * @param foundedString Date when the guild was founded
     */
    public void setFounded(String foundedString){
        try {
            founded = Utils.SHORT_DATE.parse(foundedString);
        } catch (ParseException e) {
            Log.e("TAG","setFounded: Couldn't parse date for Guild: "+this.getName());
            founded = null;
        }
    }

    /**
     * @return number of guild members
     */
    public int getMemberCount(){
        return memberList.size();
    }

    /**
     * Iterates through the member list to count online members
     * @return number of online members
     */
    public int getOnlineCount(){
        int count = 0;
        for (GuildMember member: memberList){
            if (member.isOnline()){
                count++;
            }
        }
        return count;
    }

    /**
     * @param member member to add
     * @return true if successful
     */
    public boolean addMember(GuildMember member){
        return memberList.add(member);
    }

    /**
     * Sorts the member list by name and then rank
     */
    public void sortByRank(){
        Collections.sort(memberList,new RankComparator());
    }

    /**
     * Sorts the member list by name and then level
     */
    public void sortByLevel(){
        Collections.sort(memberList,new LevelComparator());
    }

    /**
     * Sorts the member list by name
     */
    public void sortByName(){
        Collections.sort(memberList,new NameComparator());
    }

    /**
     * Sorts the member list by level and then vocation
     */
    public void sortByVocation(){
        Collections.sort(memberList,new VocationComparator());
    }

    /**
     * Sorts the member list by name and then join date
     */
    public void sortByJoined(){
        Collections.sort(memberList,new JoinedComparator());
    }

    /* Comparator classes used for sorting */
    private class NameComparator implements Comparator<GuildMember>{
        @Override
        public int compare(GuildMember lhs, GuildMember rhs) {
            return lhs.getName().compareToIgnoreCase(rhs.getName());
        }
    }

    private class RankComparator implements Comparator<GuildMember>{
        @Override
        public int compare(GuildMember lhs, GuildMember rhs) {
            int comp = ((Integer)lhs.getRankOrder()).compareTo(rhs.getRankOrder());
            if(comp != 0){
                return comp;
            }else{
                return lhs.getName().compareToIgnoreCase(rhs.getName());
            }
        }
    }

    private class LevelComparator implements Comparator<GuildMember>{
        @Override
        public int compare(GuildMember lhs, GuildMember rhs) {
            int comp = ((Integer)rhs.getLevel()).compareTo(lhs.getLevel());
            if(comp != 0){
                return comp;
            }else{
                return lhs.getName().compareToIgnoreCase(rhs.getName());
            }
        }
    }

    private class VocationComparator implements Comparator<GuildMember>{
        @Override
        public int compare(GuildMember lhs, GuildMember rhs) {
            int comp = ((Integer)lhs.getVocationId()).compareTo(rhs.getVocationId());
            if(comp != 0){
                return comp;
            }else{
                return ((Integer)rhs.getLevel()).compareTo(lhs.getLevel());
            }
        }
    }

    private class JoinedComparator implements Comparator<GuildMember>{
        @Override
        public int compare(GuildMember lhs, GuildMember rhs) {
            int comp = (lhs.getJoined()).compareTo(rhs.getJoined());
            if(comp != 0){
                return comp;
            }else{
                return lhs.getName().compareToIgnoreCase(rhs.getName());
            }
        }
    }
}