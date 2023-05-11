/**
CS 351 - Auction House
Luke McDougal, Jack Vanlyssel, Spoorthi Menta
**/

package AuctionHouse;

import Agent.Agent;
import Messages.*;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AgentClient implements Runnable {
    private final Socket socket;
    private final ObjectInputStream auctionIn;
    private final ObjectOutputStream auctionOut;
    private final ObjectInputStream bankIn;
    private final ObjectOutputStream bankOut;
    private List<Item> items;
    private final Agent agent;

    public AgentClient(Agent agent,
                       Socket socket,
                       ObjectOutputStream  auctionOut,
                       ObjectInputStream auctionIn,
                       ObjectOutputStream bankOut,
                       ObjectInputStream bankIn){
        this.socket = socket;
        this.auctionIn = auctionIn;
        this.auctionOut = auctionOut;
        this.bankIn = bankIn;
        this.bankOut = bankOut;
        this.agent = agent;
        setItems();
    }

    @Override
    public void run() {
        while (true) {
            try {
                Object m = auctionIn.readUnshared();
                if (m instanceof GetItemMessage bM) {
                    items = bM.getItems();
                }
                if (m instanceof BidMessage BM) {
                    if (BM.isSuccessful()) {
                        System.out.println("Bid was successful");
                        AgentMessage checkFunds = new AgentMessage
                                (AgentActions.AGENT_CHECK_FUNDS, agent, "");
                        bankOut.writeUnshared(checkFunds);
                    } else {
                        System.out.println("Bid was not successful");
                        AgentMessage checkFunds = new AgentMessage(
                                AgentActions.AGENT_CHECK_FUNDS, agent, "");
                        bankOut.writeUnshared(checkFunds);
                    }
                    System.out.println(((BankMessage)
                            bankIn.readUnshared()).getReply());
                } else if (m instanceof OutBidMessage OB) {
                    System.out.println("Out bid on " + OB.getOutBid().getName());
                } else if (m instanceof ItemWonMessage IW) {
                    System.out.println("Won Item " + IW.getItem().getName() +
                            " for " + "$" + IW.getBid());
                    items.removeIf(e-> e.getItemID() ==
                            IW.getItem().getItemID());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * Validate input
     * @param sc of type Scanner
     * @param size of type int
     * @param prompt of type String
     * @param reply reply message of type String
     * @return index
     */
    private int validateInput(Scanner sc, int size,
                              String prompt, String reply) {
        int index;
        ArrayList<Integer> indices = new ArrayList<>();
        for (int i = 1; i < size+1; i++) {indices.add(i); }
        do {
            System.out.println(prompt);
            while (!sc.hasNextInt()) {
                System.out.println(reply);
                sc.next();
            }
            index = sc.nextInt();
        } while (!indices.contains(index));

        return index-1;
    }


    /**
     * Set items
     */
    private void setItems(){
        try {
            GetItemMessage GI = new GetItemMessage();
            auctionOut.reset();
            auctionOut.writeUnshared(GI);
        }catch(Exception e){e.printStackTrace();}

    }


    /**
     * Bid
     */
    public void bid() {
        try {
            System.out.printf("%-25s", "Name");
            items.forEach(e -> System.out.printf("%-25s", e.getName()));
            System.out.println();

            System.out.printf("%-25s", "Min bid:");
            items.forEach(e -> System.out.printf("%-25s", "$" + e.getMinBid()));
            System.out.println();

            System.out.printf("%-25s", "Selection:");
            for (int i = 1; i < items.size() + 1; i++)
                System.out.printf("%-25s", i);
            System.out.println();

            Scanner s = new Scanner(System.in);
            int item = validateInput(s, items.size(), "Enter valid item", "Enter valid item");
            ArrayList<Double> amounts = new ArrayList<>();
            double minAmount = items.get(item).getMinBid();
            double multiplier = 0;
            int choices = 3;
            NumberFormat formatter = NumberFormat.getCurrencyInstance();
            for (int i = 0; i < choices; i++) {
                amounts.add(minAmount + multiplier);
                System.out.printf("%-25s", formatter.format(minAmount + multiplier));
                multiplier += 1000;
            }
            System.out.println();
            for (int i = 1; i < choices + 1; i++) {
                System.out.printf("%-25s", i);
            }
            System.out.println();
            int bidChoice = validateInput(s, amounts.size(), "Enter bid amount out, ", " Enter valid bid choice");
            System.out.println();
            System.out.println("Enter minimum bid amount: ");
            double x = amounts.get(bidChoice);
            BidMessage b = new BidMessage(x, items.get(item), agent.getAccountNumber());
            auctionOut.writeUnshared(b);
            System.out.println("Sending that message");
            setItems();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
