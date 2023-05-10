package AuctionHouse;

import Messages.AuctionHouseMessage;
import Messages.BankMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.Scanner;

import static Messages.AuctionHouseActions.AUCTION_REGISTER;

/**
 * AuctionHouseServer creates the Auction House Server
 */
public class AHServer {
    private ServerSocket ahs_sock;
    private AuctionHouse AH;
    private ObjectOutputStream bankOut;
    private ObjectInputStream bankIn;
    private final int bankPort = 55555;
    private final String bankIP;
    private String ahIP;

    /**
     * AuctionHouseServer constructor create the Auction House Server
     */
    public AHServer(int auctionHousePort, String ip) throws UnknownHostException {
        bankIP = ip;
        try {
            System.out.println("creating Auction House server socket.");
            ahs_sock = new ServerSocket(auctionHousePort);

            AH = new AuctionHouse(ahIP, auctionHousePort,"resources/items.txt");

            connectionThread();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Safely handles socket connections, tries again on failure
     * @param ip ip number of type String
     * @param port port number of type int
     * @return socket
     */
    private Socket safeConnect(String ip,int port)throws IOException{
        Socket s;
        boolean connected = false;
        while(!connected) {
            try{
                s = new Socket(ip, port);
                connected = true;
                System.out.println("Connection successful");
                return s;

            }catch (ConnectException e) {
                System.out.println("Connect failed, trying again");
            }
            try{
                Thread.sleep(2000);
            }catch (InterruptedException ie){}
        }
        System.out.println("Failure");
        return  null;
    }

    /**
     * connectionThread creates a connection thread
     */
    private void connectionThread(){
        try{
            Socket bankSocket = safeConnect(bankIP, bankPort);
            System.out.println("Connected to the Bank");
            ahIP = bankSocket.getLocalAddress().getHostAddress();
            AH.setIP(ahIP);
            bankOut= new ObjectOutputStream(bankSocket.getOutputStream());
            bankIn = new ObjectInputStream(bankSocket.getInputStream());

            AuctionHouseMessage registerAH = new AuctionHouseMessage(AUCTION_REGISTER, AH,"");
            bankOut.writeUnshared(registerAH);
            try{
                BankMessage message = (BankMessage)bankIn.readUnshared();
                System.out.println(message.getReply());
                AH.setAuctionID(message.getAccountNumber());
            }catch (ClassNotFoundException e){e.printStackTrace();}
            catch (Exception e){e.printStackTrace();}

            //flush output
            bankOut.flush();


        }catch (IOException e ){e.printStackTrace();}

        //Create a thread to listen in on agent connections
        Thread listener = new Thread(()->{
            try {
                while(true){
                    System.out.println("waiting for agent connection");
                    //Connection from agent or auction house
                    Socket ah_sock = ahs_sock.accept();
                    ObjectOutputStream agentOut= new
                            ObjectOutputStream(ah_sock.getOutputStream());
                    ObjectInputStream agentIn= new
                            ObjectInputStream(ah_sock.getInputStream());
                    System.out.println("Agent has connected to this server");
                    AHClientManager aClient = new AHClientManager(ah_sock,
                            agentOut, agentIn,bankOut,bankIn,AH);
                    Thread thread = new Thread(aClient);
                    System.out.println("starting thread.");
                    thread.start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        listener.start();
    }

    /**
     * main starts the program
     * @param args argument
     */
    public static void main (String[] args) throws UnknownHostException {
        Scanner scan = new Scanner(System.in);
        System.out.println("Enter the Bank's IP:");
        String bankIP = scan.nextLine();
        System.out.println("Set the port for this AH:");
        int port = scan.nextInt();
        new AHServer(port, bankIP);

    }
}
