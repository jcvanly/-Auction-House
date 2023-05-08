package AuctionHouse;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class AHServer {
    private final ServerSocket ahServerSocket;
    private final AuctionHouse auctionHouse;
    private final String bankIPAddress;
    private final String ahIPAddress;
    private final int ahPort;

    public AHServer(String bankIPAddress, String ahIPAddress, int ahPort) throws IOException {
        this.bankIPAddress = bankIPAddress;
        this.ahIPAddress = ahIPAddress;
        this.ahPort = ahPort;

        ahServerSocket = new ServerSocket(ahPort);
        auctionHouse = new AuctionHouse();

    }

    public static void main(String[] args) throws IOException {
        Scanner scnr = new Scanner(System.in);
        System.out.println("Enter the IP ADDRESS of the Bank:");
        String bankIP = scnr.nextLine();
        System.out.println("Enter the IP ADDRESS of this Auction House:");
        String ahIP = scnr.nextLine();
        System.out.println("Enter the PORT of this Auction House:");
        int ahPort = scnr.nextInt();
        new AHServer(bankIP, ahIP, ahPort);
    }

    private Socket connectTo(String ip, int port) {
        return null;
    }





}
