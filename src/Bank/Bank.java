package Bank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import AuctionHouse.AuctionHouse;

public class Bank {
    private final List<AuctionHouse> auctionHouses;
    private final HashMap<Integer, Account> accounts;
    private final HashMap<Integer, Double> blockedMoney;
    private final double BALANCE = 9999999;
    private final int MAX_SIZE = 10;
    private final int portNumber;
    private final int bankPort;
    private final int currID = 0;

    /**
     * Instantiates a new Bank.
     */
    public Bank() {
        this.portNumber = portNumber;
        this.bankPort = portNumber;
        this.accounts = new HashMap<>();
        this.auctionHouses = new ArrayList<>();
        this.blockedMoney = new HashMap<>();
    }

    /**
     * Register ah account.
     * @param house the house
     * @return the account
     */
    public synchronized Account registerAH(AuctionHouse house) {
        int uniqueID = accounts.size() + 1;
        Account acct = new Account(accounts.size() + 1, 0);
        accounts.put(accounts.size() + 1, acct);
        System.out.println("Registered Auction House account " +
                "with ID of " + uniqueID);
        house.setAuctionID(uniqueID);
        auctionHouses.add(house);
        return acct;
    }


    /**
     * Register agent account.
     * @param accountName of type String
     * @return account
     */
    public Account registerAgent(String accountName) {
        int uniqueID = accounts.size() + 1;
        Account acct = new Account(uniqueID, BALANCE);
        acct.setName(accountName);
        accounts.put(uniqueID, acct);
        return acct;
    }

    /**
     * Contains auction house boolean.
     * @param id of type int
     * @return the boolean
     */
    public boolean containsAuctionHouse(int id){
        for(AuctionHouse auction: auctionHouses){
            if(auction.getAuctionID() == id ) return true;
        }
        return false;
    }

    /**
     * Remove auction house.
     * @param id of tyoe int
     */
    public void removeAuctionHouse(int id){
        ArrayList<AuctionHouse> houseCopy = new ArrayList<>(auctionHouses);
        for (int i = 0; i < houseCopy.size(); i++) {
            if(houseCopy.get(i).getAuctionID() == id) {
                auctionHouses.remove(i);
            }
        }
    }

    /**
     * Gets account.
     * @param id of type int
     * @return the account
     */
    public Account getAccount(int id) {
        return this.accounts.get(id);
    }

    /**
     * Gets auction houses.
     * @return the auction houses
     */
    public List<AuctionHouse> getAuctionHouses() {
        return auctionHouses;
    }

    /**
     * Hold action. Takes money from agent based off ID
     * and puts it into the blockedMoney hashmap
     * @param uID the u id of type Integer
     * @param amt the amt of type double
     */
    public void holdAction(Integer uID, double amt) {
        Account acct = accounts.get(uID);
        double total =amt;
        if (blockedMoney.containsKey(uID)){
            total += blockedMoney.get(uID);
        }
        acct.deductFunds(total);
        blockedMoney.put(uID, total);
    }

    /**
     * Outbid action. Removes the funds from blockedMoney Hashmap
     * based off ID and adds it to the
     *agent account again based off their ID
     * @param uID the u id of type int
     */
    public void outbidAction(int uID) {
        Account acct = accounts.get(uID);
        acct.addFunds(blockedMoney.get(uID));
        blockedMoney.remove(uID);
    }

    /**
     * Win action. Adds the funds to auction house account from respective bid
     * @param agentID of type int
     * @param auctionHouseID of type int
     */
    public void winAction(int agentID, int auctionHouseID) {
        Account auctionAcct = accounts.get(auctionHouseID);
        auctionAcct.addFunds(blockedMoney.get(agentID));
        blockedMoney.remove(agentID);
    }

    /**
     * Gets accounts.
     * @return the accounts
     */
    public HashMap<Integer, Account> getAccounts() {
        return accounts;
    }



    /**
     * Sufficient funds boolean. Returns false if bid amount is greater
     * than the available funds in the agent bank account. Returns true
     * if bid amount is less than available funds in the bank account.
     * @param id of type int
     * @param amount of type double
     * @return the boolean
     */
    public boolean sufficientFunds(int id, double amount) {

        double requiredAmount = amount;
        System.out.println(id);
        if(blockedMoney.containsKey(id)) {
            double holdOnMoney = blockedMoney.get(id);
            requiredAmount =   amount + holdOnMoney;
        }
        System.out.println("Input     +++ checking");
        System.out.println(amount + "   " + accounts.get(id).getBal());
        return Double.compare(requiredAmount,accounts.get(id).getBal()) <= 0;
    }

}
