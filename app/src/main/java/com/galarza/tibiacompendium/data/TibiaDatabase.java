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

    public List<Item> getItemsByCategory(String category, String order){
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT title, capacity, image, look_text\n" +
                "\t,max(CASE WHEN ItemProperties.property = \"Arm\" THEN ItemProperties.value END) AS Armor\n" +
                "\t,max(CASE WHEN ItemProperties.property = \"Voc\" THEN ItemProperties.value END) AS Vocations\n" +
                "\t,max(CASE WHEN ItemProperties.property = \"Level\" THEN ItemProperties.value END) AS Level\n" +
                "\t,max(CASE WHEN ItemProperties.property = \"Atk\" THEN ItemProperties.value END) AS Attack\n" +
                "\t,max(CASE WHEN ItemProperties.property = \"Def\" THEN ItemProperties.value END) AS Def\n" +
                "\t,max(CASE WHEN ItemProperties.property = \"Atk+\" THEN ItemProperties.value END) AS ExtraAttack\n" +
                "\t,max(CASE WHEN ItemProperties.property = \"Range\" THEN ItemProperties.value END) AS Range\n" +
                "\t,max(CASE WHEN ItemProperties.property = \"Attrib\" THEN ItemProperties.value END) AS Attrib\n" +
                "\t,max(CASE WHEN ItemProperties.property = \"Type\" THEN ItemProperties.value END) AS Type\n" +
                "FROM Items\n" +
                "LEFT JOIN ItemProperties ON ItemProperties.itemid = Items.id\n" +
                "WHERE category LIKE \""+category+"\"\n" +
                "GROUP BY 1\n" +
                "ORDER BY "+order+" ASC",null);
        List<Item> items = new ArrayList<>();
        while(c.moveToNext()){
            Item item = new Item();
            item.setName(c.getString(0));
            item.setImageFromBlob(c.getBlob(2));
            item.setLookText(c.getString(3));
            items.add(item);
        }
        c.close();
        return items;
    }

    public Item getItem(String name){
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT Items.title, Items.capacity, Items.image, Items.look_text\n" +
                "\t,max(CASE WHEN ItemProperties.property = \"Arm\" THEN ItemProperties.value END) AS Armor\n" +
                "\t,max(CASE WHEN ItemProperties.property = \"Voc\" THEN ItemProperties.value END) AS Vocations\n" +
                "\t,max(CASE WHEN ItemProperties.property = \"Level\" THEN ItemProperties.value END) AS Level\n" +
                "\t,max(CASE WHEN ItemProperties.property = \"Atk\" THEN ItemProperties.value END) AS Attack\n" +
                "\t,max(CASE WHEN ItemProperties.property = \"Def\" THEN ItemProperties.value END) AS Def\n" +
                "\t,max(CASE WHEN ItemProperties.property = \"Atk+\" THEN ItemProperties.value END) AS ExtraAttack\n" +
                "\t,max(CASE WHEN ItemProperties.property = \"Range\" THEN ItemProperties.value END) AS Range\n" +
                "\t,max(CASE WHEN ItemProperties.property = \"Attrib\" THEN ItemProperties.value END) AS Attrib\n" +
                "\t,max(CASE WHEN ItemProperties.property = \"Type\" THEN ItemProperties.value END) AS Type\n" +
                "FROM Items\n" +
                "LEFT JOIN ItemProperties ON ItemProperties.itemid = Items.id\n" +
                "WHERE Items.name LIKE \""+name+"\"\n" +
                "GROUP BY 1\n" +
                "LIMIT 1",null);
        if(c.getCount() < 1){
            return null;
        }
        c.moveToFirst();
        Item item = new Item();
        item.setName(c.getString(0));
        item.setLookText(c.getString(3));
        c = db.rawQuery("SELECT Creatures.title AS \"Name\", CreatureDrops.percentage, Creatures.image \n" +
                "FROM Items, CreatureDrops, Creatures \n" +
                "WHERE CreatureDrops.itemid = Items.id AND Creatures.id = CreatureDrops.creatureid AND Items.name LIKE \""+name+"\"\n" +
                "ORDER by CreatureDrops.percentage DESC",null);
        while(c.moveToNext()){
            ItemDrop itemDrop = new ItemDrop();
            itemDrop.setCreature(c.getString(0));
            itemDrop.setChance(c.getDouble(1));
            itemDrop.setImageFromBlob(c.getBlob(2));
            item.addDrop(itemDrop);
        }
        c = db.rawQuery("SELECT NPCs.name, NPCs.city, SellItems.value\n" +
                "FROM Items, NPCs, SellItems\n" +
                "WHERE Items.id = SellItems.itemid AND NPCs.id = SellItems.vendorid AND Items.name LIKE \""+name+"\"\n" +
                "ORDER BY SellItems.value DESC",null);
        while (c.moveToNext()){
            NpcOffer sellOffer = new NpcOffer();
            sellOffer.setNpc(c.getString(0));
            sellOffer.setCity(Utils.toTitleCase(c.getString(1)));
            sellOffer.setValue(c.getInt(2));
            item.addBoughtBy(sellOffer);
        }
        c = db.rawQuery("SELECT NPCs.name, NPCs.city, BuyItems.value\n" +
                "FROM Items, NPCs, BuyItems\n" +
                "WHERE Items.id = BuyItems.itemid AND NPCs.id = BuyItems.vendorid AND Items.name LIKE \""+name+"\"\n" +
                "ORDER BY BuyItems.value ASC",null);
        while (c.moveToNext()){
            NpcOffer buyOffer = new NpcOffer();
            buyOffer.setNpc(c.getString(0));
            buyOffer.setCity(Utils.toTitleCase(c.getString(1)));
            buyOffer.setValue(c.getInt(2));
            item.addSoldBy(buyOffer);
        }
        c.close();
        return item;
    }
}
