package AuctionHouse;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuctionHouse implements Serializable {

    private final List<Item> items;
    private final List<Item> itemsToSell;
    private int auctionID = 0;
    private final String ip;
    private final int port;
    private final boolean running;
    private int currItemId;

    private final Map<Item, AHClientManager> bidMap;
    private final Map<Integer, Item> accountHistory;

    private final int winTime = 30;
    private final double BID_OFFSET = 1000;

    public AuctionHouse(String IP, int port, String itemListFile) {
        bidMap = new HashMap<>();
        accountHistory = new HashMap<>();
        running = true;
        this.ip = IP;
        this.port = port;
        this.items  = new ArrayList<>();
        this.itemsToSell = new ArrayList<>();
        this.currItemId = 0;
    }

    public int getAuctionID() {
        return auctionID;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }
}
