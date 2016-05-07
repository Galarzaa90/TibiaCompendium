package com.galarza.tibiacompendium.data;

/**
 * Constains information for NPC offers
 * @author Allan Galarza
 */
public class NpcOffer {
    private String npc;
    private String city;
    private String item;
    private int value;

    /**
     * @return name of the offer's NPC
     */
    public String getNpc() {
        return npc;
    }

    /**
     * @param npc name of the offer's NPC
     */
    public void setNpc(String npc) {
        this.npc = npc;
    }

    /**
     * @return city where the NPC is located
     */
    public String getCity() {
        return city;
    }

    /**
     * @param city city where the NPC is located
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * @return name of the offer's item
     */
    public String getItem() {
        return item;
    }

    /**
     * @param item name of the offer's item
     */
    public void setItem(String item) {
        this.item = item;
    }

    /**
     * @return value of the offer
     */
    public int getValue() {
        return value;
    }

    /**
     * @param value value of the offer
     */
    public void setValue(int value) {
        this.value = value;
    }
}
