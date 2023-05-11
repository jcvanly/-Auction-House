package AuctionHouse;

import java.io.Serializable;

public class Item implements Serializable {

    private final String name;
    private final double price;
    private final String description;
    private final int itemID;
    private double minBid;
    private double currBid;
    private int houseId;
    private int itemCount;
    private boolean won;

    /**
     * Item to be created from qualifying information.
     * @param name name of item of type String
     * @param description description of item of type String
     */
    public Item(String name, double price, String description, int itemID, int houseId){
        this.name = name;
        this.price = price;
        this.description = description;
        this.itemID = itemID;
        this.houseId = houseId;
        this.currBid = 0;
        this.minBid = price;
        this.itemCount = 0;
        this.won = false;
    }

    /**
     * If won
     * @return won
     */
    public boolean isWon() {
        return won;
    }

    /**
     * Sets the win
     * @param won of type boolean
     */
    public void setWon(boolean won) {
        this.won = won;
    }

    /**
     * Gets the item count
     * @return itemCount
     */
    public int getItemCount() {
        return itemCount;
    }

    /**
     * Increments
     */
    public void increment(){
        itemCount++;
    }

    /**
     * Resets the timer
     */
    public void resetTimer(){
        this.itemCount = 0;
    }

    /**
     * Gets description
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets name
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * getMinBid gets minBid
     * @return minBid
     */
    public double getMinBid(){
        return minBid;
    }

    /**
     * getCurrBid gets currBid
     * @return currBid
     */
    public double getCurrBid(){return currBid;}

    /**
     * setMinBid sets the minBid
     * @param minBid minBid
     */
    public void setMinBid(double minBid) {
        this.minBid = minBid;
    }

    /**
     * setCurrBid sets the currBid
     * @param currBid current Bid of type double
     */
    public void setCurrBid(double currBid){
        this.currBid = currBid;
    }

    /**
     * Gets the ItemID
     * @return Item ID
     */
    public int getItemID() {
        return itemID;
    }
}
