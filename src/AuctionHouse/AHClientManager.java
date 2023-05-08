package AuctionHouse;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class AHClientManager implements Runnable {
    public AHClientManager(Socket connectionSocket, ObjectOutputStream agentOutputStream,
                           ObjectInputStream agentInputStream, ObjectOutputStream bankOutputStream,
                           ObjectInputStream bankInputStream, AuctionHouse auctionHouse) {
        
    }

    @Override
    public void run() {

    }
}
