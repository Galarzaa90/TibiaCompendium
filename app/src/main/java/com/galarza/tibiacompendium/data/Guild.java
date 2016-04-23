package com.galarza.tibiacompendium.data;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Guild {
    private String name;
    private String logoUrl;
    private String world;
    private Date founded;
    private final List<GuildMember> memberList = new ArrayList<>();

    private static final DateFormat DATEFORMAT = new SimpleDateFormat("MMM dd yyyy", Locale.UK);

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getWorld() {
        return world;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public Date getFounded() {
        return founded;
    }

    public void setFounded(Date founded) {
        this.founded = founded;
    }

    public String getFoundedString(){
        return DATEFORMAT.format(founded);
    }

    public List<GuildMember> getMemberList() {
        return memberList;
    }

    public void setFounded(String foundedString){
        try {
            founded = DATEFORMAT.parse(foundedString);
        } catch (ParseException e) {
            Log.e("Guild","Couldn't parse date for Guild: "+this.getName());
            founded = null;
        }
    }

    public int getMemberCount(){
        return memberList.size();
    }

    public int getOnlineCount(){
        int count = 0;
        for (GuildMember member: memberList){
            if (member.isOnline()){
                count++;
            }
        }
        return count;
    }

    public boolean addMember(GuildMember member){
        return memberList.add(member);
    }

    public void sortByRank(){
        Collections.sort(memberList,new RankComparator());
    }

    public void sortByLevel(){
        Collections.sort(memberList,new LevelComparator());
    }

    public void sortByName(){
        Collections.sort(memberList,new NameComparator());
    }

    public void sortByVocation(){
        Collections.sort(memberList,new VocationComparator());
    }


    public void sortByJoined(){
        Collections.sort(memberList,new JoinedComparator());
    }

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
            int comp = (lhs.getVocation()).compareTo(rhs.getVocation());
            if(comp != 0){
                return comp;
            }else{
                return lhs.getName().compareToIgnoreCase(rhs.getName());
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