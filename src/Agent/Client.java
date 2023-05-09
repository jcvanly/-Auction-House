package Agent;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

import static Messages.AgentActions.*;

public class Client implements Runnable{

    String IP;
    final Integer PORT = 55555;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Socket s;
    private final Agent agent;

    public Client(String IP) throws Exception {
        this.IP = IP;
        agent = new Agent();
        s = safeConnect(IP,PORT);
        Scanner sc = new Scanner(System.in);
        System.out.println("Please enter your account name");
        String name = sc.nextLine();

        AgentMessage message = new AgentMessage(AGENT_REGISTER, agent, name);
        out = new ObjectOutputStream(s.getOutputStream());
        in = new ObjectInputStream(s.getInputStream());
        out.writeUnshared(message);
        BankMessage reply =  (BankMessage) in.readUnshared();
        System.out.println(reply.getReply());
        agent.setAvailableHouses(reply.getHouses());
        agent.setAccountNumber(reply.getAccountNumber());

        //Wait for at least one auction house to join
        if(agent.getAvailableHouses().size()==0){
            awaitAgent();
        }
        validDateAgentAwait(sc);

        //one last refresh to see if anything has changed and check
        refreshConnection();
        AgentMessage updateHouses = new AgentMessage(
                AGENT_UPDATE_AUCTION,agent,"");
        out.writeUnshared(updateHouses);
        BankMessage update =  (BankMessage) in.readUnshared();

        if(update.getHouses().size() != agent.getAvailableHouses().size()){
            System.out.println("One or more Auction Houses have disconnected"+
                    " during this time, updating list " +
                    "of available Auction houses");
            agent.setAvailableHouses(update.getHouses());
        }

        int auctionHouseIndex = validateAgentAuctionHouseChoice(agent,sc);
        AuctionHouse houseToJoin = findHouse(
                agent.getAvailableHouses(),auctionHouseIndex);
        System.out.println("Joining Auction House server number "
                + auctionHouseIndex );
        agent.getAvailableHouses().removeIf(e ->
                e.getAuctionID() == auctionHouseIndex);
        System.out.println(houseToJoin.getIp());
        System.out.println(houseToJoin.getPort());
        new AHUser(agent,auctionHouseIndex,s,houseToJoin.getIp(),
                houseToJoin.getPort(),out,in);
    }

}
