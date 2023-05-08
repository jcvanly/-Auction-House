package AuctionHouse;

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
    private final String ahIPAddress;
    private final int ahPort;

    private ObjectOutputStream bankOutputStream;
    private ObjectInputStream bankInputStream;

    public AHServer(String bankIPAddress, String ahIPAddress, int ahPort) throws IOException, InterruptedException {
        this.bankIPAddress = bankIPAddress;
        this.ahIPAddress = ahIPAddress;
        this.ahPort = ahPort;

        ahServerSocket = new ServerSocket(ahPort);
        auctionHouse = new AuctionHouse();

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

    }



}
