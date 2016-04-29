package com.galarza.tibiacompendium.data;

public class Item {
    private String name;
    private int vendor_value;
    private int actual_value;
    private int capacity;
    private boolean stackable;
    private byte[] image;

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

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
