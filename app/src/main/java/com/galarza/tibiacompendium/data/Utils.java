package com.galarza.tibiacompendium.data;


import android.os.Build;
import android.text.Html;
import android.text.Spanned;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Contains constants and static methods used through the application
 * @author Allan Galarza
 */
public class Utils {
    /* Argument keys */
    public static final String ARG_TITLE_RESOURCE = "title_resource";
    public static final String ARG_GUILD_NAME = "guild_name";
    public static final String ARG_PLAYER_NAME = "player_name";

    public static final String PREFS_NAME = "TibiaCompendiumPrefs";
    public static final String PREFS_CHARACTER_HISTORY = "CharacterHistory";

    /* Tibia.com date formats */
    public static final DateFormat LONG_DATE = new SimpleDateFormat("MMM dd yyyy, HH:mm:ss z", Locale.UK);
    public static final DateFormat SHORT_DATE = new SimpleDateFormat("MMM dd yyyy", Locale.UK);

    /* fetchData AsyncTask error codes */
    public static final int NO_NETWORK_ENABLED = 0;
    public static final int COULDNT_REACH = 1;

    public static String toTitleCase(String string){
        String[] words = string.split("\\s+");
        StringBuilder sb = new StringBuilder();
        if (words[0].length() > 0) {
            sb.append(Character.toUpperCase(words[0].charAt(0))).append(words[0].subSequence(1, words[0].length()).toString().toLowerCase());
            for (int i = 1; i < words.length; i++) {
                sb.append(" ");
                sb.append(Character.toUpperCase(words[i].charAt(0))).append(words[i].subSequence(1, words[i].length()).toString().toLowerCase());
            }
        }
        return sb.toString();
    }

    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String source){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            return Html.fromHtml(source,Html.FROM_HTML_MODE_LEGACY);
        }else{
            return Html.fromHtml(source);
        }
    }
}
