package com.galarza.tibiacompendium.data;


import android.text.Html;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Contains static methods to handle HTML parsing from the Tibia website
 * @author Allan Galarza
 */
public class Parser {
    private static final String TAG = "Parser";
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


        /* Get player's name */
        Matcher m = getMatcher(content, "Name:</td><td>([^<,]+)");
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

        /* Check if player is scheduled for deletion and get date */
        m = getMatcher(content,", will be deleted at ([^<]+)");
        if(m.find()){
            String deleted = Html.fromHtml(m.group(1)).toString();
            deleted = deleted.replaceAll(String.valueOf((char) 160), " ");
            player.setDeletion(deleted);
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
                Log.e(TAG,"parseCharacter: Couldn't parse level: \""+m.group(1)+"\".");
            }
            player.setLevel(lvl);
        }

        /* Get player's achievement points */
        m = getMatcher(content, "Points:</nobr></td><td>(\\d+)");
        int achievements = 0;
        if(m.find()){
            try{
                achievements = Integer.parseInt(m.group(1));
            }catch(NumberFormatException nfe){
                Log.e(TAG,"parseCharacter: Couldn't parse achievement points: \""+m.group(1)+"\".");
            }
            player.setAchievementPoints(achievements);
        }

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
        m = getMatcher(content, "Last login:</td><td>([^<]+)");
        if(m.find()) {
            String lastLogin = Html.fromHtml(m.group(1)).toString();
            lastLogin = lastLogin.replaceAll(String.valueOf((char) 160), " ");
            player.setLastLogin(lastLogin);
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

        /* Checking if player has recent deaths */
        startIndex = content.indexOf("<b>Player Deaths</b>");
        if(startIndex >= 0) {
            /* Reducing the content string to reduce regex load */
            content = content.substring(startIndex);

            /* Getting player's deaths */
            m = getMatcher(content, "valign=\"top\" >([^<]+)<\\/td><td>(.+?)<\\/td></tr>");
            while (m.find()) {
                Death death = new Death();
                /* Getting death's date */
                String date = Html.fromHtml(m.group(1)).toString();
                date = date.replaceAll(String.valueOf((char) 160), " ");
                death.setDate(date);
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

        /* Check if it displays other characters (player not hidden) */
        startIndex = content.indexOf("<B>Characters</B>");
        if (startIndex >= 0) {
            /* Reducing the content string to reduce regex load */
            content = content.substring(startIndex);
            m = getMatcher(content,"<TD WIDTH=10%><NOBR>([^<]+)[^?]+.+?VALUE=\"([^\"]+)",Pattern.DOTALL);
            while(m.find()){
                Player otherCharacter = new Player();
                otherCharacter.setWorld(m.group(1));
                otherCharacter.setName(m.group(2));
                /* Ignore if it's the current player */
                if (player.getName().equalsIgnoreCase(otherCharacter.getName()))
                        continue;
                player.addCharacter(otherCharacter);
            }

        }
        return player;
    }

    /**
     * Parses Tibia's website content and fetches guild's data
     * @param content String containing website content
     * @return A Guild object containing the fetched info or null if the guild was not found
     */
    public static Guild parseGuild(String content){
        Guild guild = new Guild();

        int startIndex = content.indexOf("<H1>");

        if(startIndex < 0){
            return null;
        }

        /* Get guild name */
        Matcher m = getMatcher(content,"<H1>([^<]+)");
        if(m.find()){
            guild.setName(m.group(1));
        }

        /* Get guild's logo */
        m = getMatcher(content,"<IMG SRC=\"([^\"]+)\" W");
        if(m.find()){
            guild.setLogoUrl(m.group(1));
        }

        /* Get guild found date */
        m = getMatcher(content,"founded on (\\w+) on ([^.]+)");
        if(m.find()){
            guild.setWorld(m.group(1));
            String founded = Html.fromHtml(m.group(2)).toString();
            founded = founded.replaceAll(String.valueOf((char) 160), " ");
            guild.setFounded(founded);
        }

        /* Reducing string size to reduce regex load */
        startIndex = content.indexOf("<td>Status</td>");
        int endIndex = content.indexOf("</table",startIndex);
        content = content.substring(startIndex,endIndex);

        /* Getting member list */
        m = getMatcher(
                content,
                "<TD>([^<]+)</TD></td><TD><A HREF=\"https://secure\\.tibia\\.com/community/\\?subtopic=characters&name=([^\"]+)\">[^<]+</A> *\\(*(.*?)\\)*</TD><TD>([^<]+)</TD><TD>([^<]+)</TD><TD>([^<]+)</TD><TD class='onlinestatus'><span class=\"(\\w+)\"",
                Pattern.DOTALL
        );
        String prevRank = "";
        int rankOrder = 0;
        while(m.find()){
            GuildMember member = new GuildMember();
            /* Getting rank */
            String rank = Html.fromHtml(m.group(1)).toString();
            if(rank.equals(String.valueOf((char) 160))){
                member.setRank(prevRank);
            }else{
                member.setRank(rank);
                prevRank = rank;
                rankOrder++;
            }
            /* Rank order is saved to sort the same way as the website */
            member.setRankOrder(rankOrder);
            /* Getting name and title */
            member.setName(m.group(2).replaceAll("\\+", " "));
            member.setTitle(m.group(3));
            /* Getting vocation */
            member.setVocation(m.group(4));
            /* Getting level */
            int level = 0;
            try{
                level = Integer.parseInt(m.group(5));
            }catch(NumberFormatException nfe){
                Log.e(TAG,"parseGuild: Couldn't parse level: \""+m.group(5)+"\".");
            }
            member.setLevel(level);
            /* Getting join date */
            String joined = Html.fromHtml(m.group(6)).toString();
            joined = joined.replaceAll(String.valueOf((char) 160), " ");
            member.setJoined(joined);
            member.setOnline(m.group(7).equalsIgnoreCase("green"));

            if(!guild.addMember(member)){
                Log.e(TAG,"parseGuild: Couldn't add GuildMember:"+member.getName());
            }
        }
        return guild;
    }

    /**
     * Compiles a regular expression, matches a string with the pattern and returns the matcher
     * @param str String to match
     * @param regex Regular expression to compile
     * @return {@code Matcher} for the string and regular expression
     */
    private static Matcher getMatcher(String str, String regex){
        return getMatcher(str,regex,0);
    }

    /**
     * Compiles a regular expression, matches a string with the pattern and returns the matcher
     * @param str String to match
     * @param regex Regular expression to compile
     * @param flags Compiler flags
     * @return {@code Matcher} for the string and regular expression
     */
    private static Matcher getMatcher(String str, String regex, int flags){
        return Pattern.compile(regex,flags).matcher(str);
    }

}
