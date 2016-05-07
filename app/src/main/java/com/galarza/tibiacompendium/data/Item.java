package com.galarza.tibiacompendium.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains information about an in-game item
 * @author Allan Galarza
 */
public class Item {
    private String name;
    private int value;
    private int weight;
    private boolean stackable;
    private Bitmap image;
    private String lookText;
    private List<ItemDrop> droppedBy = new ArrayList<>();
    private List<NpcOffer> buyers = new ArrayList<>();
    private List<NpcOffer> sellers = new ArrayList<>();

    /**
     * @return the name of the item
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name of the item
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the value of the item
     */
    public int getValue() {
        return value;
    }

    /**
     * @param value the value of the item
     */
    public void setValue(int value) {
        this.value = value;
    }

    /**
     * @return the weight of the item
     */
    public int getWeight() {
        return weight;
    }

    /**
     * @param weight the weight of the item
     */
    public void setWeight(int weight) {
        this.weight = weight;
    }

    /**
     * @return true if the item is stackable
     */
    public boolean isStackable() {
        return stackable;
    }

    /**
     * @param stackable true if the item is stackable
     */
    public void setStackable(boolean stackable) {
        this.stackable = stackable;
    }

    /**
     * @return the bitmap of the image
     */
    public Bitmap getImage() {
        return image;
    }

    /**
     * Converts an image binary into a bitmap
     * @param image image in blob format
     */
    public void setImageFromBlob(byte[] image) {
        this.image = BitmapFactory.decodeByteArray(image,0,image.length);
    }

    /**
     * @return the look text of an item
     */
    public String getLookText() {
        return lookText;
    }

    /**
     * @param lookText the look text of an item
     */
    public void setLookText(String lookText) {
        this.lookText = lookText;
    }

    /**
     * @return List containing creatures that drop it
     */
    public List<ItemDrop> getDroppedBy() {
        return droppedBy;
    }

    /**
     * @param drop monster that drops it
     */
    public void addDrop(ItemDrop drop){
        droppedBy.add(drop);
    }

    /**
     * @return number of creatures that drop it
     */
    public int getDropCount(){
        return droppedBy.size();
    }

    /**
     * @return List of NPCs that buy the item
     */
    public List<NpcOffer> getBuyers() {
        return buyers;
    }

    /**
     * @param offer NPC buy offer for the item
     */
    public void addBuyer(NpcOffer offer){
        buyers.add(offer);
    }

    /**
     * @return number of NPCs that buy the item
     */
    public int getBuyersCount(){
        return buyers.size();
    }

    /**
     * @return List of NPCs that sell the item
     */
    public List<NpcOffer> getSellers() {
        return sellers;
    }

    /**
     * @param offer NPC sell offer for the item
     */
    public void addSeller(NpcOffer offer){
        sellers.add(offer);
    }

    /**
     * @return number of NPCs that buy the item
     */
    public int getSellersCount(){
        return sellers.size();
    }
}
