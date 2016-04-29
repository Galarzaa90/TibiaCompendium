package com.galarza.tibiacompendium.data;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

public class TibiaDatabase extends SQLiteAssetHelper{
    private static final String DATABASE_NAME = "Database.db";
    private static final int DATABASE_VERSION = 1;

    public TibiaDatabase(Context context){
        super(context,DATABASE_NAME, null, DATABASE_VERSION);
    }

    public List<Item> getItemsByCategory(String category){
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT title, capacity, image\n" +
                "\t,max(CASE WHEN ItemProperties.property = \"Arm\" THEN ItemProperties.value END) AS Armor\n" +
                "\t,max(CASE WHEN ItemProperties.property = \"Voc\" THEN ItemProperties.value END) AS Vocations\n" +
                "\t,max(CASE WHEN ItemProperties.property = \"Level\" THEN ItemProperties.value END) AS Level\n" +
                "FROM Items\n" +
                "LEFT JOIN ItemProperties ON ItemProperties.itemid = Items.id\n" +
                "WHERE category LIKE ?\n" +
                "GROUP BY 1\n" +
                "ORDER BY Armor",new String[]{category});
        List<Item> items = new ArrayList<>();
        while(c.moveToNext()){
            Item item = new Item();
            item.setName(c.getString(0));
            item.setImage(c.getBlob(2));
            items.add(item);
        }
        c.close();
        return items;
    }
}
