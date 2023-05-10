
package Messages;

import AuctionHouse.Item;

import java.io.Serializable;

public class ItemWonMessage implements Serializable {
    private double bid;
    private Item item;


    public ItemWonMessage(Item item, double bid){
        this.item = item;
        this.bid = bid;
    }

    /**
     * Gets bid
     * @return bid
     */
    public double getBid() {
        return bid;
    }

    /**
     * Gets items
     * @return item
     */
    public Item getItem() {
        return item;
    }
}
