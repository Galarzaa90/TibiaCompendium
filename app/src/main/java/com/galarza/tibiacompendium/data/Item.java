package com.galarza.tibiacompendium.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.ArrayList;
import java.util.List;

public class Item {
    private String name;
    private int vendor_value;
    private int actual_value;
    private int capacity;
    private boolean stackable;
    private Bitmap image;
    private String lookText;
    private List<ItemDrop> droppedBy = new ArrayList<>();
    private List<NpcOffer> boughtBy = new ArrayList<>();
    private List<NpcOffer> soldBy = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getVendor_value() {
        return vendor_value;
    }

    public void setVendor_value(int vendor_value) {
        this.vendor_value = vendor_value;
    }

    public int getActual_value() {
        return actual_value;
    }

    public void setActual_value(int actual_value) {
        this.actual_value = actual_value;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public boolean isStackable() {
        return stackable;
    }

    public void setStackable(boolean stackable) {
        this.stackable = stackable;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImageFromBlob(byte[] image) {
        this.image = BitmapFactory.decodeByteArray(image,0,image.length);
    }

    public String getLookText() {
        return lookText;
    }

    public void setLookText(String lookText) {
        this.lookText = lookText;
    }

    public List<ItemDrop> getDroppedBy() {
        return droppedBy;
    }

    public void addDrop(ItemDrop drop){
        droppedBy.add(drop);
    }

    public int getDropCount(){
        return droppedBy.size();
    }

    public List<NpcOffer> getBuyers() {
        return boughtBy;
    }

    public void addBoughtBy(NpcOffer offer){
        boughtBy.add(offer);
    }

    public int getBuyersCount(){
        return boughtBy.size();
    }

    public List<NpcOffer> getSellers() {
        return soldBy;
    }

    public void addSoldBy(NpcOffer offer){
        soldBy.add(offer);
    }

    public int getSellersCount(){
        return soldBy.size();
    }
}
