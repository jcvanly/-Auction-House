package Bank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import AuctionHouse.AuctionHouse;

public class Bank {

    private final List<AuctionHouse> auctionHouses;
    private final HashMap<Integer, Account> accounts;
    private final HashMap<Integer, Double> blockedMoney;

    public Bank() {
        accounts = new HashMap<>();
        auctionHouses = new ArrayList<>();
        blockedMoney = new HashMap<>();
    }

}
