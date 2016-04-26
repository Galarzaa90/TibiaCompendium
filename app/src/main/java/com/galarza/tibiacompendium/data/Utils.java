package com.galarza.tibiacompendium.data;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class Utils {
    /* Argument keys */
    public static final String ARG_SECTION_NUMBER = "section_number";
    public static final String ARG_GUILD_NAME = "guild_name";
    public static final String ARG_PLAYER_NAME = "player_name";

    /* Tibia.com date formats */
    public static final DateFormat LONG_DATE = new SimpleDateFormat("MMM dd yyyy, HH:mm:ss z", Locale.UK);
    public static final DateFormat SHORT_DATE = new SimpleDateFormat("MMM dd yyyy", Locale.UK);
}
