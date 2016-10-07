package com.galarza.tibiacompendium.data;

/**
 * Constains information of an item drop
 * @author Allan Galarza
 */
public class ItemDrop {
    private String creature;
    private double chance;
    private byte[] image;

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
     * @return the bitmap of the image
     */
    public byte[] getImage() {
        return image;
    }

    /**
     * @param image image bytes
     */
    public void setImage(byte[] image) {
        this.image = image;
    }
}
