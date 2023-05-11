/**
CS 351 - Auction House
Luke McDougal, Jack Vanlyssel, Spoorthi Menta
**/

package Messages;

import AuctionHouse.Item;

import java.io.Serializable;
import java.util.List;

public class GetItemMessage implements Serializable {
    private List<Item> items;
    public GetItemMessage(){}
    public GetItemMessage(List<Item> items){
        this.items = items;
    }

    public List<Item> getItems() {
        return items;
    }
}

