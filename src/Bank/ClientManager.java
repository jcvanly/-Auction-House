

package Bank;

import Messages.*;

import java.io.*;
import java.net.Socket;

import static Messages.BankActions.*;

/**
 * Handles how messages are received within the bank.
 * Utilizes a switch statement to decide what to do with the
 * respective processes associated to an Auction House or an Agent message.
 */
public class ClientManager implements Runnable {
    private final Socket c_sock;
    private final ObjectOutputStream out;
    private final ObjectInputStream in;
    private boolean isConnected;
    private final Bank bank;
    private int accountID;

    /**
     * Instantiates a new Client handler.
     *
     * @param c_sock of type Socket
     * @param out of type ObjectOutputStream
     * @param in of type ObjectOutputStream
     * @param bank of type Bank
     * @param isConnected of type boolean
     */
    public ClientManager(Socket c_sock, ObjectOutputStream out,
                         ObjectInputStream in,Bank bank,boolean isConnected){
        this.c_sock = c_sock;
        this.out = out;
        this.in = in;
        this.isConnected = isConnected;
        this.bank = bank;
    }

    @Override
    public void run() {
        while (isConnected) {
            try {
                Object obj = in.readUnshared();
//                while ((obj = in.readUnshared()) != null) {

                System.out.println("Received message");
                System.out.println(obj.getClass());
                //Sent from the Agent
                if (obj instanceof AgentMessage) {
                    AgentMessage mess = (AgentMessage) obj;
                    switch ((AgentActions) mess.getMessageAction()){
                        //Case to register an Agent
                        //and send back available auction houses
                        case AGENT_REGISTER:
                            Account account = bank.registerAgent
                                    (mess.getReply());
                            BankMessage reply = new BankMessage(BANK_CONFIRM,
                                    account.getAcctNum(),
                                    bank.getAuctionHouses(),
                                    "Account Confirmed " +
                                            "You have ID of " +
                                            account.getAcctNum() +
                                            "  with initial balance of " +
                                            account.getBal() + " and name of "
                                            + account.getName());
                            out.writeUnshared(reply);
                            break;
                        //Case to update auction house list
                        case AGENT_UPDATE_AUCTION:
                            BankMessage update = new BankMessage(
                                    BANK_CONFIRM, bank.getAuctionHouses());
                            System.out.println(
                                    bank.getAuctionHouses().size());
                            out.reset();
                            out.writeUnshared(update);
                            break;
                        //Case to check funds for the agent
                        case AGENT_CHECK_FUNDS:
                            int acctNumber =
                                    mess.getAgent().getAccountNumber();
                            BankMessage updateFunds = new BankMessage
                                    (BANK_CONFIRM,acctNumber, "Your account "
                                            +acctNumber + " has $" + bank.
                                            getAccount(acctNumber).getBal());
                            out.writeUnshared(updateFunds);


                    }

                }
                if (obj instanceof AuctionHouseMessage) {
                    AuctionHouseMessage message = (AuctionHouseMessage) obj;
                    System.out.println(message.getMessageAction());
                    //switch statement that handles the types of
                    //messages sent from the Auction House
                    switch((AuctionHouseActions) message.getMessageAction()){
                        //Review the bid sent from the auction house,
                        // either accept or reject
                        case AUCTION_REVIEW_BID:
                            System.out.println("Review bid message received");
                            System.out.println(message.getBid().getAgentID() +
                                    " "+ message.getBid().getAmount());
                            if(bank.sufficientFunds(
                                    message.getBid().getAgentID(),
                                    message.getBid().getAmount())){
                                bank.holdAction(message.getBid().getAgentID(),
                                        message.getBid().getAmount());
                                BankMessage response = new BankMessage
                                        (BANK_ACCEPT, message.getBid(),
                                                "Bid was accepted");
                                out.reset();
                                out.writeUnshared(response);
                            }
                            else {
                                BankMessage response = new BankMessage
                                        (BANK_REJECT, message.getBid(),
                                                "Bid was rejected");
                                out.reset();
                                out.writeUnshared(response);
                            }
                            break;
                        //Outbid case, release funds
                        case AUCTION_OUTBID:
                            bank.outbidAction(message.getBid().getAgentID());
                            break;
                        //Win case, deduct funds from winning bid
                        case AUCTION_WIN:
                            bank.winAction(message.getAgentID(),
                                    message.getAuctionHouseID());
                            break;
                        //Register case, register a new Auction House;
                        // add to list that is sent to Agents
                        case AUCTION_REGISTER:
                            AuctionHouseMessage mess =(AuctionHouseMessage)obj;
                            Account account =bank.registerAH(mess.getHouse());
                            accountID = account.getAcctNum();
                            BankMessage reply = new BankMessage
                                    (BANK_CONFIRM, account.getAcctNum(),
                                            "Account Confirmed " +
                                                    "You have ID of " +
                                                    account.getAcctNum() +
                                                    "  with initial " +
                                                    "balance of " +
                                                    account.getBal());
                            System.out.println("Size of registered " +
                                    "Auction houses is/are " +
                                    (bank.getAuctionHouses().size()));
                            out.writeUnshared(reply);
                    }
                }
            } catch (Exception e) {
                System.out.println("Connected user disconnected");
                if(bank.containsAuctionHouse(accountID)){
                    bank.removeAuctionHouse(accountID);
                    System.out.println(accountID);
                    e.printStackTrace();
                }

                isConnected = false;
            }
        }
    }
}

