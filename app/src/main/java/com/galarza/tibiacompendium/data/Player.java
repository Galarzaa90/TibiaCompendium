package com.galarza.tibiacompendium.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Player {
    private String name;
    private String formerNames;
    private String sex;
    private String vocation;
    private int level;
    private int achievementPoints;
    private String world;
    private String formerWorld;
    private String residence;
    private String house;
    private String houseCity;
    private String guildRank;
    private String guild;
    private Date lastLogin;
    private String lastLoginString;
    private String comment;
    private boolean premium;
    private final List<Death> deathList = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFormerNames() {
        return formerNames;
    }

    public void setFormerNames(String formerNames) {
        this.formerNames = formerNames;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getVocation() {
        return vocation;
    }

    public void setVocation(String vocation) {
        this.vocation = vocation;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getAchievementPoints() {
        return achievementPoints;
    }

    public void setAchievementPoints(int achievementPoints) {
        this.achievementPoints = achievementPoints;
    }

    public String getWorld() {
        return world;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public String getFormerWorld() {
        return formerWorld;
    }

    public void setFormerWorld(String formerWorld) {
        this.formerWorld = formerWorld;
    }

    public String getResidence() {
        return residence;
    }

    public void setResidence(String residence) {
        this.residence = residence;
    }

    public String getHouse() {
        return house;
    }

    public void setHouse(String house) {
        this.house = house;
    }

    public String getHouseCity() {
        return houseCity;
    }

    public void setHouseCity(String houseCity) {
        this.houseCity = houseCity;
    }

    public String getGuildRank() {
        return guildRank;
    }

    public void setGuildRank(String guildRank) {
        this.guildRank = guildRank;
    }

    public String getGuild() {
        return guild;
    }

    public void setGuild(String guild) {
        this.guild = guild;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getLastLoginString() {
        return lastLoginString;
    }

    public void setLastLoginString(String lastLoginString) {
        this.lastLoginString = lastLoginString;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isPremium() {
        return premium;
    }

    public void setPremium(boolean premium) {
        this.premium = premium;
    }

    public List<Death> getDeathList() {
        return deathList;
    }

    /*
    Unused setter
    public void setDeathList(List<Death> deathList) {
        this.deathList = deathList;
    }*/

    public void addDeath(Death death){
        deathList.add(death);
    }


}