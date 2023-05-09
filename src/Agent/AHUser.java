package Agent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class AHUser {
    ObjectOutputStream auctionOut;
    ObjectInputStream auctionIn;
    ObjectOutputStream bankOut;
    ObjectInputStream bankIn;
    Agent agent;
    Socket bankSocket;
    int auctionHouseID;
    int PORT = 5555;
    String IP;


    public void refreshConnection(Socket socket) throws IOException {
        bankOut.close();
        bankIn.close();
        socket.close();
        socket = new Socket(IP, PORT);
        bankOut = new ObjectOutputStream(socket.getOutputStream());
        bankIn = new ObjectInputStream(socket.getInputStream());
    }

    public AHUser(Agent agent, int auctionHouseId,
                  Socket bankSocket, String ip, int port,
                  ObjectOutputStream bankOut,
                  ObjectInputStream bankIn) {
        this.bankIn = bankIn;
        this.bankOut = bankOut;
        this.bankSocket = bankSocket;
        this.agent = agent;
        this.auctionHouseID = auctionHouseId;
        System.out.println("Connecting to Auction House Server");
        Socket sock;
        try {
            sock = new Socket(ip, port);
            auctionOut = new ObjectOutputStream(sock.getOutputStream());
            auctionIn = new ObjectInputStream(sock.getInputStream());
            System.out.println("Connection Successful");
            AgentClient client = new AgentClient(agent,sock,
                    auctionOut,auctionIn,bankOut,bankIn);
            new Thread(client).start();

            Scanner sc = new Scanner(System.in);
            while (true) {

                System.out.println("Would you like to make " +
                        "another bid or would yo" +
                        "u like to switch to another " +
                        "auction house? (Bid/Switch)");
                String choice = sc.nextLine();
                System.out.println("Choice is: " + choice);
                if(choice.equals("Bid") || choice.equals("bid")){
                    client.bid();
                }else if(choice.equals("Switch") || choice.equals("switch")){
                    try {
                        //Read in the message
                        agentAuctionChoice(sc);
                        //print the auctions
                        agent.getAvailableHouses().forEach(e->
                                System.out.print(e.getAuctionID() + "   "));
                        //Get the choice
                        int auctionHouseChoice =
                                validateAgentAuctionHouseChoice(agent,sc);
                        AuctionHouse houseChoice =
                                agent.getAvailableHouses().
                                        get(auctionHouseChoice);
                        //create the socket
                        auctionHouseID = houseChoice.getAuctionID();
                        int choicePort = houseChoice.getPort();
                        String  host = houseChoice.getIp();
                        Socket socket = new Socket(host,choicePort);
                        ObjectOutputStream out = new
                                ObjectOutputStream(socket.getOutputStream());
                        ObjectInputStream in = new ObjectInputStream
                                (socket.getInputStream());
                        //Clone the agent
                        Agent clone = new Agent();
                        clone.setAvailableHouses(agent.getAvailableHouses());
                        clone.setAccountNumber(agent.getAccountNumber());

                        AgentClient agentClient = new AgentClient(clone,
                                sock,out,in,bankOut,bankIn);
                        new Thread(agentClient).start();

                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println("Closed gracefully");
        }
    }

}
