package com.galarza.tibiacompendium.data;

import android.util.Log;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Constains information for a character
 * @author Allan Galarza
 */
public class Player {
    private static final String TAG = "Player";

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
    private String comment;
    private boolean premium;
    private Date deletion;
    private final List<Death> deathList = new ArrayList<>();
    private final List<Player> otherCharacters = new ArrayList<>();

    /**
     * @return name of the character
     */
    public String getName() {
        return name;
    }

    /**
     * @param name name of the character
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return former names of the character separated by commas
     */
    public String getFormerNames() {
        return formerNames;
    }

    /**
     * @param formerNames former names of the character separated by commas
     */
    public void setFormerNames(String formerNames) {
        this.formerNames = formerNames;
    }

    /**
     * @return gender of the character
     */
    public String getSex() {
        return sex;
    }

    /**
     * @param sex gender of the character
     */
    public void setSex(String sex) {
        this.sex = sex;
    }

    /**
     * @return vocation of the character
     */
    public String getVocation() {
        return vocation;
    }

    /**
     * @param vocation vocation of the character
     */
    public void setVocation(String vocation) {
        this.vocation = vocation;
    }

    /**
     * @return level of the character
     */
    public int getLevel() {
        return level;
    }

    /**
     * @param level level of the character
     */
    public void setLevel(int level) {
        this.level = level;
    }

    /**
     * @return achievement points for the character
     */
    public int getAchievementPoints() {
        return achievementPoints;
    }

    /**
     * @param achievementPoints achievement points for the character
     */
    public void setAchievementPoints(int achievementPoints) {
        this.achievementPoints = achievementPoints;
    }

    /**
     * @return world of the character
     */
    public String getWorld() {
        return world;
    }

    /**
     * @param world world of the character
     */
    public void setWorld(String world) {
        this.world = world;
    }

    /**
     * @return former world of the character
     */
    public String getFormerWorld() {
        return formerWorld;
    }

    /**
     * @param formerWorld former world of the character
     */
    public void setFormerWorld(String formerWorld) {
        this.formerWorld = formerWorld;
    }

    /**
     * @return city the character is resident of
     */
    public String getResidence() {
        return residence;
    }

    /**
     * @param residence city the character is resident of
     */
    public void setResidence(String residence) {
        this.residence = residence;
    }

    /**
     * @return house owned by the character
     */
    public String getHouse() {
        return house;
    }

    /**
     * @param house house owned by the character
     */
    public void setHouse(String house) {
        this.house = house;
    }

    /**
     * @return city of the character's house
     */
    public String getHouseCity() {
        return houseCity;
    }

    /**
     * @param houseCity city of the character's house
     */
    public void setHouseCity(String houseCity) {
        this.houseCity = houseCity;
    }

    /**
     * @return guild rank of the character
     */
    public String getGuildRank() {
        return guildRank;
    }

    /**
     * @param guildRank guild rank of the character
     */
    public void setGuildRank(String guildRank) {
        this.guildRank = guildRank;
    }

    /**
     * @return name of the guild the character belongs to
     */
    public String getGuild() {
        return guild;
    }

    /**
     * @param guild name of the guild the character belongs to
     */
    public void setGuild(String guild) {
        this.guild = guild;
    }

    /**
     * @return string displaying the last login of the character
     */
    public String getLastLoginString(){
        return Utils.LONG_DATE.format(lastLogin);
    }

    /**
     * @param lastLoginString string displaying the last login of the character
     */
    public void setLastLogin(String lastLoginString){
        try{
            lastLogin = Utils.LONG_DATE.parse(lastLoginString);
        }catch(ParseException e){
            Log.e(TAG,"setLastLogin: Couldn't parse date for Player: "+this.getName());
        }
    }

    /**
     * @return the comment of the character
     */
    public String getComment() {
        return comment;
    }

    /**
     * @param comment the comment of the character
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * @return if the character has a premium account or not
     */
    public boolean isPremium() {
        return premium;
    }

    /**
     * @param premium if the character has a premium account or not
     */
    public void setPremium(boolean premium) {
        this.premium = premium;
    }

    /**
     * @return date when the character is getting deleted
     */
    public Date getDeletion(){
        return deletion;
    }

    /**
     * @return string displaying the date the character is getting deleted
     */
    public String getDeletionString(){
        return Utils.LONG_DATE.format(deletion);
    }

    /**
     * @param deletionString string dislpaying the date the character is getting deleted
     */
    public void setDeletion(String deletionString){
        try{
            deletion = Utils.LONG_DATE.parse(deletionString);
        }catch(ParseException e){
            Log.e(TAG,"setDeletion: Couldn't parse date for Player: "+this.getName());
        }
    }

    /**
     * @return List of the character deaths
     */
    public List<Death> getDeathList() {
        return deathList;
    }

    /**
     * @param death Death to add
     */
    public void addDeath(Death death){
        deathList.add(death);
    }

    /**
     * @return List of other characters from the same account
     */
    public List<Player> getOtherCharacters() {
        return otherCharacters;
    }

    /**
     * @param player player to add
     */
    public void addCharacter(Player player){
        otherCharacters.add(player);
    }

    /**
     * Gets unique id for vocations for better ordering
     * @return vocation id
     */
    public int getVocationId(){
        String vocation = getVocation().toLowerCase();
        if(vocation.contains("druid")){
            return 1;
        }else if(vocation.contains("sorcerer")){
            return 2;
        }else if(vocation.contains("paladin")){
            return 3;
        }else if(vocation.contains("knight")){
            return 4;
        }else{
            return 0;
        }
    }
}