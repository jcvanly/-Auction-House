package Agent;

import AuctionHouse.AuctionHouse;

import java.util.List;

public class Agent {

    private List<AuctionHouse> availableHouses;
    private int accountNumber;

    public void setAccountNumber(int accountNumber) {
        this.accountNumber = accountNumber;
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public void setAvailableHouses(List<AuctionHouse> availableHouses) {
        this.availableHouses = availableHouses;
    }

    public List<AuctionHouse> getAvailableHouses() {
        return availableHouses;
    }
}
