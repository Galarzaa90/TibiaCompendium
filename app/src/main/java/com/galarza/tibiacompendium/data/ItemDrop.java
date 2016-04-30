package com.galarza.tibiacompendium.data;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ItemDrop {
    private String creature;
    private double chance;
    private Bitmap image;

    public String getCreature() {
        return creature;
    }

    public void setCreature(String creature) {
        this.creature = creature;
    }

    public double getChance() {
        return chance;
    }

    public void setChance(double chance) {
        this.chance = chance;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImageFromBlob(byte[] image) {
        this.image = BitmapFactory.decodeByteArray(image,0,image.length);
    }
}
