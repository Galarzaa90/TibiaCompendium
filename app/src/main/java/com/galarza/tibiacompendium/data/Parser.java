package com.galarza.tibiacompendium.data;


import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {

    private static String STATUS = "Status:?<.*?(Free Account|Premium Account).+";

    public static Player parseCharacter(String content){
        Player player = new Player();

        content = content.substring(
                content.indexOf("BoxContent"),
                content.indexOf("<B>Search Character</B>"));

        /* Get player's name */
        String NAME = "Name:</td><td>([^<]+)\\s<";
        Matcher m = getMatcher(content, NAME);
        if(m.find()){
            player.setName(m.group(1).trim());
        }else{
            return null;
        }

        /* Get former names if available */
        String FORMER_NAMES = "Names:</td><td>([^<]+)<";
        m = getMatcher(content, FORMER_NAMES);
        if(m.find()){
            player.setFormerNames(m.group(1).trim());
        }

        /* Get player's sex */
        String SEX = "Sex:</td><td>([^<]+)";
        m = getMatcher(content, SEX);
        if(m.find()) {
            player.setSex(m.group(1));
        }

        /* Get player's vocation */
        String VOCATION = "Vocation:</td><td>([^<]+)";
        m = getMatcher(content, VOCATION);
        if(m.find()) {
            player.setVocation(m.group(1));
        }

        /* Get player's level */
        String LEVEL = "Level:</td><td>(\\d+)";
        m = getMatcher(content, LEVEL);
        int lvl = 0;
        if(m.find()){
            try{
                lvl = Integer.parseInt(m.group(1));
            }catch(NumberFormatException nfe){
                Log.e("Parser","Couldn't parse level: \""+m.group(1)+"\".");
            }
            player.setLevel(lvl);
        }
        player.setLevel(lvl);

        /* Get player's achievement points */
        String ACHIEVEMENTS = "Points:</td><td>(\\d+)";
        m = getMatcher(content, ACHIEVEMENTS);
        int achievements = 0;
        if(m.find()){
            try{
                achievements = Integer.parseInt(m.group(1));
            }catch(NumberFormatException nfe){
                Log.e("Parser","Couldn't parse achievement points: \""+m.group(1)+"\".");
            }
            player.setAchievementPoints(achievements);
        }
        player.setAchievementPoints(achievements);

        /* Get player's world */
        String WORLD = "World:</td><td>([^<]+)";
        m = getMatcher(content, WORLD);
        if(m.find()) {
            player.setWorld(m.group(1));
        }

        /* Get player's former world */
        String FORMER_WORLD = "Former World:</td><td>([^<]+)";
        m = getMatcher(content, FORMER_WORLD);
        if(m.find()) {
            player.setFormerWorld(m.group(1));
        }

        /* Get player's residence (city) */
        String RESIDENCE = "Residence:</td><td>([^<]+)";
        m = getMatcher(content, RESIDENCE);
        if(m.find()) {
            player.setResidence(m.group(1));
        }

        /* Get player's house */
        String HOUSE = "House:</td><td>(.+?)\\s\\(([A-z]+)\\) is paid until ([A-z]+).*?;(\\d+).*?;(\\d+)";
        m = getMatcher(content, HOUSE);
        if(m.find()) {
            player.setHouse(m.group(1));
            player.setHouseCity(m.group(2));
        }


        String GUILD_RANK = "membership:</td><td>([^<]+)\\sof the";
        m = getMatcher(content, GUILD_RANK);
        if(m.find()){
            player.setGuildRank(m.group(1).trim());

            String GUILD = "GuildName=.*?([^\"]+).+";
            m = getMatcher(content, GUILD);
            if (m.find()){
                player.setGuild(m.group(1).replaceAll("\\+"," "));
            }
        }

        String LAST_LOG = "Last login:</td><td>([A-z]+).*?;(\\d+).*?;(\\d+).*?;(\\d+):(\\d+):(\\d+).*?([A-Z]+)";
        m = getMatcher(content, LAST_LOG);
        if(m.find()) {
            player.setLastLoginString(String.format(
                    "%s %s %s %s:%s:%s %s",
                    m.group(1),m.group(2),m.group(3),m.group(4),m.group(5),m.group(6),m.group(7)
            ));
            DateFormat format = new SimpleDateFormat("MMM dd yyyy HH:mm:ss z", Locale.UK);
            try {
                player.setLastLogin(format.parse(player.getLastLoginString()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        String COMMENT = "Comment:</td><td>(.*)\\s</td>";
        m = getMatcher(content, COMMENT,Pattern.DOTALL);
        if(m.find()){
            player.setComment(m.group(1));
        }

        /* Reducing the content string to reduce regex load */
        content = content.substring(content.indexOf("<b>Character Deaths</b>"));
        String DEATHS = "valign=\"top\" >([^<]+)<\\/td><td>(.+?)<\\/td></tr>";
        m = getMatcher(content, DEATHS);
        while(m.find()){
            Death death = new Death();
            Matcher m1 = getMatcher(m.group(1),"(\\w+).+?;(\\d+).+?;(\\d+).+?;(\\d+):(\\d+):(\\d+).+?;(\\w+)");
            /* Getting death's date */
            if(m1.find()){
                Log.e("date match",m1.group(0));
                death.setDateString(String.format(
                        "%s %s %s %s:%s:%s %s",
                        m1.group(1),m1.group(2),m1.group(3),m1.group(4),m1.group(5),m1.group(6),m1.group(7)
                ));
                DateFormat format = new SimpleDateFormat("MMM dd yyyy HH:mm:ss z", Locale.UK);
                try {
                    death.setDate(format.parse(death.getDateString()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            /* Getting level and killer */
            /* Died by monster */
            if(m.group(2).contains("Died")){
                Matcher m2 = getMatcher(m.group(2),"Level (\\d+) by ([^.]+)");
                if(m2.find()){
                    death.setLevel(Integer.parseInt(m2.group(1)));
                    death.setKiller(m2.group(2));
                    death.setByPlayer(false);
                }
            /* Killed by player */
            }else{
                Matcher m2 = getMatcher(m.group(2),"Level (\\d+) by .+?name=([^\"]+)");
                if(m2.find()){
                    death.setLevel(Integer.parseInt(m2.group(1)));
                    death.setKiller(m2.group(2).replaceAll("\\+"," "));
                    death.setByPlayer(true);
                }
            }
            Log.e("death",death.toString());
            player.addDeath(death);
        }

        return player;
    }

    private static Matcher getMatcher(String str, String regex){
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(str);
    }

    private static Matcher getMatcher(String str, String regex, int flags){
        Pattern pattern = Pattern.compile(regex,flags);
        return pattern.matcher(str);
    }

}
