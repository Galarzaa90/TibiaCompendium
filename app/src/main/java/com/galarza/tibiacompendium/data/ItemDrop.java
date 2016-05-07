package com.galarza.tibiacompendium.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Constains information of an item drop
 * @author Allan Galarza
 */
public class ItemDrop {
    private String creature;
    private double chance;
    private Bitmap image;

    /**
     * @return name of the creature that drops it
     */
    public String getCreature() {
        return creature;
    }

    /**
     * @param creature name of the creature that drops it
     */
    public void setCreature(String creature) {
        this.creature = creature;
    }

    /**
     * @return chance to get the item drop
     */
    public double getChance() {
        return chance;
    }

    /**
     * @param chance chance to get the item drop
     */
    public void setChance(double chance) {
        this.chance = chance;
    }

    /**
     * @return image of the item
     */
    public Bitmap getImage() {
        return image;
    }

    /**
     * Converts a binary image to a bitmap
     * @param image image in blob format
     */
    public void setImageFromBlob(byte[] image) {
        this.image = BitmapFactory.decodeByteArray(image,0,image.length);
    }
}
