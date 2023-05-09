package AuctionHouse;

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

    /**
     * Win the bid
     * @param item of type Item
     */
    public void winBid(Item item) {

    }

}
