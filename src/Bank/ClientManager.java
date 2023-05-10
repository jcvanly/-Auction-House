package Bank;

import Messages.*;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import static Messages.BankActions.*;
import static Messages.BankActions.BANK_CONFIRM;

public class ClientManager implements Runnable {

    private final Socket clientSocket;
    private final ObjectInputStream inputStream;
    private final ObjectOutputStream outputStream;
    private final Bank bank;

    private int accountID;
    private boolean connected;

    public ClientManager(Socket clientSocket, ObjectOutputStream outputStream, ObjectInputStream inputStream, Bank bank) {
        this.clientSocket = clientSocket;
        this.outputStream = outputStream;
        this.inputStream = inputStream;
        this.bank = bank;
        this.connected = true;
    }

    @Override
    public void run() {
        while (connected) {
            try {
                Object obj = inputStream.readUnshared();
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
                            outputStream.writeUnshared(reply);
                            break;
                        //Case to update auction house list
                        case AGENT_UPDATE_AUCTION:
                            BankMessage update = new BankMessage(
                                    BANK_CONFIRM, bank.getAuctionHouses());
                            System.out.println(
                                    bank.getAuctionHouses().size());
                            outputStream.reset();
                            outputStream.writeUnshared(update);
                            break;
                        //Case to check funds for the agent
                        case AGENT_CHECK_FUNDS:
                            int acctNumber =
                                    mess.getAgent().getAccountNumber();
                            BankMessage updateFunds = new BankMessage
                                    (BANK_CONFIRM,acctNumber, "Your account "
                                            +acctNumber + " has $" + bank.
                                            getAccount(acctNumber).getBal());
                            outputStream.writeUnshared(updateFunds);


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
                                outputStream.reset();
                                outputStream.writeUnshared(response);
                            }
                            else {
                                BankMessage response = new BankMessage
                                        (BANK_REJECT, message.getBid(),
                                                "Bid was rejected");
                                outputStream.reset();
                                outputStream.writeUnshared(response);
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
                            outputStream.writeUnshared(reply);
                    }
                }
            } catch (Exception e) {
                System.out.println("Connected user disconnected");
                if(bank.containsAuctionHouse(accountID)){
                    bank.removeAuctionHouse(accountID);
                    System.out.println(accountID);
                    e.printStackTrace();
                }

                connected = false;
            }
        }
    }
}
