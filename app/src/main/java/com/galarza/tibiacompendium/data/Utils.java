package com.galarza.tibiacompendium.data;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class Utils {
    /* Argument keys */
    public static final String ARG_TITLE_RESOURCE = "title_resource";
    public static final String ARG_GUILD_NAME = "guild_name";
    public static final String ARG_PLAYER_NAME = "player_name";

    /* Tibia.com date formats */
    public static final DateFormat LONG_DATE = new SimpleDateFormat("MMM dd yyyy, HH:mm:ss z", Locale.UK);
    public static final DateFormat SHORT_DATE = new SimpleDateFormat("MMM dd yyyy", Locale.UK);

    /* fetchData AsyncTask error codes */
    public static final int NO_NETWORK_ENABLED = 0;
    public static final int COULDNT_REACH = 1;
}
