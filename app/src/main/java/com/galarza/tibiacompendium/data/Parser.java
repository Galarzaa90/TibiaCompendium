package com.galarza.tibiacompendium.data;


import android.text.Html;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    /**
     * Parses Tibia's website content and fetches player's data
     * @param content String containing website content
     * @return A Player object containing the fetched info or null if the character was not found
     */
    public static Player parseCharacter(String content){
        Player player = new Player();

        /* In order to reduce regular expression load, we reduce the content string */
        int startIndex = content.indexOf("BoxContent");
        int endIndex = content.indexOf("<B>Search Character</B>");

        if(startIndex < 0){
            return null;
        }
        content = content.substring(startIndex,endIndex);
        /* TODO: Replace regular expressions with a HTML parser. */
        /* Get player's name */
        Matcher m = getMatcher(content, "Name:</td><td>([^<]+)\\s<");
        if(m.find()){
            player.setName(m.group(1).trim());
        }else{
            return null;
        }

        /* Get former names if available */
        m = getMatcher(content, "Names:</td><td>([^<]+)<");
        if(m.find()){
            player.setFormerNames(m.group(1).trim());
        }

        /* Get player's sex */
        m = getMatcher(content, "Sex:</td><td>([^<]+)");
        if(m.find()) {
            player.setSex(m.group(1));
        }

        /* Get player's vocation */
        m = getMatcher(content, "Vocation:</td><td>([^<]+)");
        if(m.find()) {
            player.setVocation(m.group(1));
        }

        /* Get player's level */
        m = getMatcher(content, "Level:</td><td>(\\d+)");
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
        m = getMatcher(content, "Points:</nobr></td><td>(\\d+)");
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
        m = getMatcher(content, "World:</td><td>([^<]+)");
        if(m.find()) {
            player.setWorld(m.group(1));
        }

        /* Get player's former world */
        m = getMatcher(content, "Former World:</td><td>([^<]+)");
        if(m.find()) {
            player.setFormerWorld(m.group(1));
        }

        /* Get player's residence (city) */
        m = getMatcher(content, "Residence:</td><td>([^<]+)");
        if(m.find()) {
            player.setResidence(m.group(1));
        }

        /* Get player's house */
        m = getMatcher(content, "House:</td><td>(.+?)\\s\\(([A-z]+)\\) is paid until ([A-z]+).*?;(\\d+).*?;(\\d+)");
        if(m.find()) {
            player.setHouse(m.group(1));
            player.setHouseCity(m.group(2));
        }


        /* Get the player's guild rank */
        m = getMatcher(content, "membership:</td><td>([^<]+)\\sof the");
        if(m.find()){
            player.setGuildRank(m.group(1).trim());

            /* Get the player's guild */
            m = getMatcher(content, "GuildName=.*?([^\"]+).+");
            if (m.find()){
                player.setGuild(m.group(1).replaceAll("\\+"," "));
            }
        }

        /* Get the player's last login date */
        m = getMatcher(content, "Last login:</td><td>([A-z]+).*?;(\\d+).*?;(\\d+).*?;(\\d+):(\\d+):(\\d+).*?([A-Z]+)");
        if(m.find()) {
            /* Groups: Month, day, year, hour, minutes, seconds, timezone */
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

        /* Get player's comment */
        m = getMatcher(content, "Comment:</td><td>(.*)\\s</td>",Pattern.DOTALL);
        if(m.find()){
            player.setComment(m.group(1));
        }

        /* Get player's premium status */
        m = getMatcher(content,"Status:</td><td>([^<]+)");
        if(m.find()){
            player.setPremium(m.group(1).contains("Premium"));
        }

        /* Checking if character has recent deaths */
        startIndex = content.indexOf("<b>Character Deaths</b>");
        if(startIndex >= 0) {
            /* Reducing the content string to reduce regex load */
            content = content.substring(startIndex);

            /* Getting character's deaths */
            m = getMatcher(content, "valign=\"top\" >([^<]+)<\\/td><td>(.+?)<\\/td></tr>");
            while (m.find()) {
                Death death = new Death();
                Matcher m1 = getMatcher(m.group(1), "(\\w+).+?;(\\d+).+?;(\\d+).+?;(\\d+):(\\d+):(\\d+).+?;(\\w+)");
                /* Getting death's date */
                if (m1.find()) {
                    death.setDateString(String.format(
                            "%s %s %s %s:%s:%s %s",
                            m1.group(1), m1.group(2), m1.group(3), m1.group(4), m1.group(5), m1.group(6), m1.group(7)
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
                if (m.group(2).contains("Died")) {
                    Matcher m2 = getMatcher(m.group(2), "Level (\\d+) by ([^.]+)");
                    if (m2.find()) {
                        death.setLevel(Integer.parseInt(m2.group(1)));
                        death.setKiller(m2.group(2));
                        death.setByPlayer(false);
                    }
                /* Killed by player */
                } else {
                    Matcher m2 = getMatcher(m.group(2), "Level (\\d+) by .+?name=([^\"]+)");
                    if (m2.find()) {
                        death.setLevel(Integer.parseInt(m2.group(1)));
                        death.setKiller(m2.group(2).replaceAll("\\+", " "));
                        death.setByPlayer(true);
                    }
                }
                player.addDeath(death);

            }
        }

        /* Check if it displays other characters (character not hidden) */
        startIndex = content.indexOf("<B>Characters</B>");
        if (startIndex >= 0) {
            /* Reducing the content string to reduce regex load */
            content = content.substring(startIndex);
            m = getMatcher(content,"<TD WIDTH=10%><NOBR>([^<]+)[^?]+.+?VALUE=\"([^\"]+)",Pattern.DOTALL);
            while(m.find()){
                Player otherCharacter = new Player();
                otherCharacter.setWorld(m.group(1));
                otherCharacter.setName(m.group(2));
                /* Ignore if it's the current character */
                if (player.getName().equalsIgnoreCase(otherCharacter.getName()))
                        continue;
                player.addCharacter(otherCharacter);
            }

        }


        return player;
    }


    public static Guild parseGuild(String content){
        Guild guild = new Guild();

        int startIndex = content.indexOf("<td>Status</td>");

        if(startIndex < 0){
            return null;
        }
        int endIndex = content.indexOf("</table",startIndex);
        content = content.substring(startIndex,endIndex);

        Matcher m = getMatcher(
                content,
                "<TD>([^<]+)</TD></td><TD><A HREF=\"https://secure\\.tibia\\.com/community/\\?subtopic=characters&name=([^\"]+)\">[^<]+</A> *\\(*(.*?)\\)*</TD><TD>([^<]+)</TD><TD>([^<]+)</TD><TD>([^<]+)</TD><TD class='onlinestatus'><span class=\"(\\w+)\"",
                Pattern.DOTALL
        );
        String prevRank = "";
        while(m.find()){
            GuildMember member = new GuildMember();
            String rank = Html.fromHtml(m.group(1)).toString();
            if(rank.equals(String.valueOf((char) 160))){
                member.setRank(prevRank);
            }else{
                member.setRank(rank);
                prevRank = rank;
            }
            member.setName(m.group(2).replaceAll("\\+", " "));
            member.setVocation(m.group(4));
            int level = 0;
            try{
                level = Integer.parseInt(m.group(5));
            }catch(NumberFormatException nfe){
                Log.e("Parser","Couldn't parse level: \""+m.group(5)+"\".");
            }
            member.setLevel(level);
            member.setJoined(m.group(6));
            member.setOnline(m.group(7).equalsIgnoreCase("green"));
            guild.addMember(member);
        }
        return guild;
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
