package AuctionHouse;

import Messages.BankMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class AHServer {
    private final ServerSocket ahServerSocket;
    private final AuctionHouse auctionHouse;
    private final String bankIPAddress;

    private ObjectOutputStream bankOutputStream;
    private ObjectInputStream bankInputStream;

    public AHServer(String bankIPAddress, String ahIPAddress, int ahPort) throws IOException, InterruptedException {
        this.bankIPAddress = bankIPAddress;

        ahServerSocket = new ServerSocket(ahPort);
        auctionHouse = new AuctionHouse(ahIPAddress, ahPort, "items.txt");

        connectToBankServer();

    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Scanner scnr = new Scanner(System.in);
        System.out.println("Enter the IP ADDRESS of the Bank:");
        String bankIP = scnr.nextLine();
        System.out.println("Enter the IP ADDRESS of this Auction House:");
        String ahIP = scnr.nextLine();
        System.out.println("Enter the PORT of this Auction House:");
        int ahPort = scnr.nextInt();
        new AHServer(bankIP, ahIP, ahPort);
    }

    private Socket connectTo(String ip, int port) throws InterruptedException {
        Socket socket = null;
        boolean connected = false;
        while (!connected) {
            try {
                socket = new Socket(ip, port);
                connected = true;
                System.out.println("Connection successful");
            } catch (IOException e) {
                System.out.println("Connection failed, trying again...");
                Thread.sleep(1000);
            }
        }
        return socket;
    }


    private void connectToBankServer() throws InterruptedException, IOException {
        Socket bankSocket = connectTo(bankIPAddress, 55555);
        System.out.println("Connection established with bank @ " + bankSocket.getInetAddress() + ":" + bankSocket.getPort());

        bankOutputStream = new ObjectOutputStream(bankSocket.getOutputStream());
        bankInputStream = new ObjectInputStream(bankSocket.getInputStream());

        AuctionHouseMessage registerAH = new AuctionHouseMessage(AUCTION_REGISTER, auctionHouse,"");
        bankOutputStream.writeUnshared(registerAH);
        try{
            BankMessage message = (BankMessage)bankInputStream.readUnshared();
            System.out.println(message.getReply());
            auctionHouse.setAuctionID(message.getAccountNumber());
        }
        catch (ClassNotFoundException e){e.printStackTrace();}
        catch (Exception e){e.printStackTrace();}

        //flush output
        bankOutputStream.flush();



        Thread clientListener = new Thread(this::clientConnection);
        clientListener.start();
    }

    private void clientConnection() {
        while(true){
            //Connection from agent or auction house
            try {
                System.out.println("Waiting for an agent to connect...");
                Socket connectionSocket = ahServerSocket.accept();
                ObjectOutputStream agentOutputStream = new ObjectOutputStream(connectionSocket.getOutputStream());
                ObjectInputStream agentInputStream= new ObjectInputStream(connectionSocket.getInputStream());
                System.out.println("Agent Connection established with " + connectionSocket.getInetAddress() + ":" + connectionSocket.getPort());
                AHClientManager ahClientManager = new AHClientManager(connectionSocket, agentOutputStream,
                        agentInputStream,bankOutputStream,
                        bankInputStream,auctionHouse);

                Thread thread = new Thread(ahClientManager);
                System.out.println("Starting new thread for the connected agent");
                thread.start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }



}
