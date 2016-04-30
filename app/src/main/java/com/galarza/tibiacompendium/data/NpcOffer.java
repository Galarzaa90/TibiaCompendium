package com.galarza.tibiacompendium.data;

/**
 * Created by Allan Galarza on 30/04/2016.
 */
public class NpcOffer {
    public static int BUY;
    public static int SELL;

    private String npc;
    private String city;
    private String item;
    private int value;

    public String getNpc() {
        return npc;
    }

    public void setNpc(String npc) {
        this.npc = npc;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
