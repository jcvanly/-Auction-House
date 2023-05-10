package AuctionHouse;

import Messages.ItemWonMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class AHClientManager implements Runnable {

    private final Socket connectionSocket;
    private final ObjectOutputStream agentOutputStream;
    private final ObjectInputStream agentInputStream;
    private final ObjectOutputStream bankOutputStream;
    private final ObjectInputStream bankInputStream;
    private final AuctionHouse auctionHouse;

    public AHClientManager(Socket connectionSocket, ObjectOutputStream agentOutputStream,
                           ObjectInputStream agentInputStream, ObjectOutputStream bankOutputStream,
                           ObjectInputStream bankInputStream, AuctionHouse auctionHouse) {
        this.connectionSocket = connectionSocket;
        this.agentOutputStream = agentOutputStream;
        this.agentInputStream = agentInputStream;
        this.bankOutputStream = bankOutputStream;
        this.bankInputStream = bankInputStream;
        this.auctionHouse = auctionHouse;

    }

    @Override
    public void run() {

    }

    public void outBid(Item item) {

    }

    public void winningBid(Item item) {

    }


    public void winBid(Item item) {
        System.out.println("call to win bid");
        ArrayList<Item> itemCopy = new ArrayList<>(auctionHouse.getItems());
        ItemWonMessage WM = new ItemWonMessage(item, item.getCurrBid());
        System.out.println(WM.getItem().getCurrBid());
        System.out.println("Before");
        System.out.println(auctionHouse.getItems().size());
        for (int i = 0; i < itemCopy.size(); i++) {
            if(itemCopy.get(i).getItemID() == item.getItemID()) {
                auctionHouse.getItems().remove(i);
                auctionHouse.generateItem();
            }
        }
        System.out.println("After");
        System.out.println(auctionHouse.getItems().size());
        try {
            agentOutputStream.writeUnshared(WM);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
