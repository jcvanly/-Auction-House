

package Messages;

import AuctionHouse.AuctionHouse;
import Agent.*;

import java.io.Serializable;

public class AuctionHouseMessage extends Message implements Serializable {

    private AuctionHouseActions actions;
    private AuctionHouse house;
    private int auctionHouseID;
    private int agentID;
    private Bid bid;
    private String reply;
    public AuctionHouseMessage(AuctionHouseActions actions, int agentID,
                               int auctionHouseID,String reply) {
        super(actions);
        this.auctionHouseID = auctionHouseID;
        this.agentID= agentID;
        this.reply = reply;
    }

    public AuctionHouseMessage(AuctionHouseActions actions, String reply, int aID) {
        super(actions);
        this.reply = reply;
        this.agentID = aID;
    }

    public AuctionHouseMessage(int aID, int aHID){
        this.agentID = aID;
        this.auctionHouseID = aHID;
    }
    public AuctionHouseMessage(AuctionHouseActions actions, AuctionHouse auctionHouse) {
        super(actions);
        this.house = auctionHouse;
    }
    public AuctionHouseMessage(AuctionHouseActions actions, AuctionHouse house,String reply) {
        super(actions);
        this.house = house;
        this.reply = reply;
    }


    public AuctionHouseMessage(AuctionHouseActions actions, Bid bid) {
        super(actions);
        this.bid = bid;
    }


    public int getAgentID() { return agentID; }
    public int getAuctionHouseID() {return auctionHouseID; }
    public AuctionHouse getHouse() { return house;    }

    public Bid getBid() {
        return bid;
    }
}

