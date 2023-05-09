package Agent;

import AuctionHouse.AuctionHouse;
import Messages.AgentMessage;
import Messages.BankMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
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

    private AuctionHouse findHouse(List<AuctionHouse> houses, int index){
        for(AuctionHouse house: houses){
            if(house.getAuctionID() == index) return house;
        }
        System.err.println("House not found");
        return null;
    }

    private void awaitAgent(){
        while(agent.getAvailableHouses().size() == 0){
            System.out.println("There are currently no Auction Houses " +
                    "available, currently updating list of Auction Houses ");
            try{
                refreshConnection();
                Thread.sleep(5000);
                AgentMessage updateHouses = new AgentMessage(
                        AGENT_UPDATE_AUCTION,agent,"");
                out.writeUnshared(updateHouses);
                BankMessage update =  (BankMessage) in.readUnshared();
                agent.setAvailableHouses(update.getHouses());
            }catch (IOException |
                    InterruptedException |
                    ClassNotFoundException sie){}
        }
    }

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
                System.out.println("Connection failed, trying again");
            }
            try{
                Thread.sleep(2000);
            }catch (InterruptedException ie){}
        }
        System.out.println("Failure");
        return  null;
    }

    private void validDateAgentAwait(Scanner sc){
        String confirm;
        while(true){
            System.out.println("There are currently " +
                    agent.getAvailableHouses().size()
                    +" Auction Houses available\nDo you want to wait for " +
                    "more Auction Houses to join? (yes/no)");
            confirm = sc.nextLine();
            if(confirm.equals("no"))break;
            refreshConnection();

            try{
                Thread.sleep(1000);
                AgentMessage updateHouses = new AgentMessage(
                        AGENT_UPDATE_AUCTION,agent,"");
                out.writeUnshared(updateHouses);
                BankMessage update =  (BankMessage) in.readUnshared();
                agent.setAvailableHouses(update.getHouses());
            }catch (IOException |
                    InterruptedException|
                    ClassNotFoundException sie){}
        }
    }

    private void refreshConnection(){
        try {
            s.close();
            in.close();
            out.close();
            s = new Socket("localhost", 55555);
            out = new ObjectOutputStream(s.getOutputStream());
            in = new ObjectInputStream(s.getInputStream());

            AgentMessage message = new AgentMessage(AGENT_UPDATE_AUCTION);
            out.writeUnshared(message);
            BankMessage reply = (BankMessage) in.readUnshared();
            agent.setAvailableHouses(reply.getHouses());
        }catch (Exception e){e.printStackTrace();}
    }

    private int validateAgentAuctionHouseChoice(Agent agent,Scanner sc) {
        int index;
        ArrayList<Integer> indices = new ArrayList<>();
        agent.getAvailableHouses().forEach(e ->
                indices.add(e.getAuctionID()));
        do {
            System.out.println("Please join an Auction house " +
                    "by typing in their ID below");
            System.out.print("IDs: ");
            agent.getAvailableHouses().forEach(e ->
                    System.out.print(e.getAuctionID() + " "));
            System.out.println();
            while (!sc.hasNextInt()) {
                System.out.println("Enter a valid Auction House number");
                sc.next();
            }
            index = sc.nextInt();
        } while (!indices.contains(index));
        return index;
    }

    @Override
    public void run() {

    }

    /**
     * Main method gets bank's IP address
     * @param args argument of type String[]
     */
    public static void main(String[] args)throws Exception {
        Scanner scan = new Scanner(System.in);
        System.out.println("PLease enter the IP address of the bank.");
        String IP = scan.nextLine();
        new Client(IP);
    }

}
