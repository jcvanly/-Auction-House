package AuctionHouse;

import Messages.BidMessage;

import java.io.*;
import java.util.*;

public class AuctionHouse implements Serializable {

    private final List<Item> items;
    private final List<Item> itemsToSell;
    private int auctionID = 0;
    private String ip;
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

        loadItems(itemListFile);

        generateItem();
        generateItem();
        generateItem();
        counter();

    }

    /**
     * Counter
     */
    private void counter(){
        Thread count = new Thread(()->{
            while(true){
                try {
                    Thread.sleep(1000);
                    ArrayList<Item> itemListCopy=new ArrayList<>(itemsToSell);
                    for(Item i : itemListCopy){
                        i.increment();
                        if(i.getItemCount() >= winTime &&
                                i.getCurrBid() != 0){
                            //Do win bid stuff
                            if(!i.isWon()){
                                bidMap.get(i).winBid(i);
                                i.setWon(true);
                                System.out.println(i.getCurrBid());
                            }
                        }
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        count.start();
    }

    /**
     * Get items to be sold
     * @return Items to be sold
     */
    public List<Item> getItems(){
        System.out.println("Got items");
        return itemsToSell;
    }

    /**
     * Set auction ID
     * @param auctionID action house ID of type int
     */
    public void setAuctionID(int auctionID) {
        this.auctionID = auctionID;
    }

    /**
     * Bid
     * @param client client
     * @param bid bid message
     * @return true if bid successfully
     */
    public boolean bid(AHClientManager client, BidMessage bid){
        for(Item i: itemsToSell){
            if(i.getItemID() == bid.getItem().getItemID()){
                if(Double.compare( bid.getBid() , i.getMinBid())>= 0){
                    if (accountHistory.containsKey(bid.getAcctNum())) {
                        if(bid.getItem().getItemID() == i.getItemID()){
                            System.out.println("Bid already made");
                            return false;
                        }
                    }
                    if (bidMap.containsKey(i)) {
                        bidMap.get(i).outBid(i);
                    }
                    bidMap.put(i, client);
                    i.setCurrBid(bid.getBid());
                    i.setMinBid(i.getCurrBid() + BID_OFFSET);
                    i.resetTimer();
                    System.out.println("Successful bid on " +
                            i.getName() + " for " + bid.getBid());
                    System.out.println(i.getCurrBid());
                    return true;
                }
            }
        }
        System.out.println("Unsuccessful bid on " + bid.getItem().getName());
        return false;
    }

    /**
     * loadItems reads in the items from items.txt file.
     * @param file file of items of type String
     */
    private void loadItems(String file){
        try {
            BufferedReader bReader = new BufferedReader((new InputStreamReader(getClass().getResourceAsStream("/items.txt"))));
            String line;

            // Read in the items
            while ((line = bReader.readLine()) != null) {
                String[] x = line.split(",");
                Item item = new Item(x[0],Integer.parseInt(x[1]), x[2], 0, 0);
                items.add(item);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Picking random items from the items to sell list.
     */
    public void generateItem(){
        Random ran = new Random();
        Item i = items.get(ran.nextInt(items.size()));
        itemsToSell.add(new Item(i.getName(), i.getMinBid(),
                i.getDescription(), currItemId++, auctionID));
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

    public void setIP(String ahIP) {
        this.ip = ahIP;
    }
}
