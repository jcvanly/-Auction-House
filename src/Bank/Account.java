/**
CS 351 - Auction House
Luke McDougal, Jack Vanlyssel, Spoorthi Menta
**/

package Bank;

public class Account {
    private final int accountNumber;
    private String accountName;
    private double balance;

    public Account(int accountNumber,double balance) {
        this.accountNumber = accountNumber;
        accountName = "";
        this.balance = balance;
    }

    /**
     * Sets name.
     * @param name of type String
     */
    public void setName(String name) {
        this.accountName = name;
    }

    /**
     * Gets acct number
     * @return the account number
     */
    public int getAcctNum() {
        return accountNumber;
    }

    /**
     * Gets the balance
     * @return balance
     */
    public double getBal() {
        return balance;
    }

    /**
     * Gets name
     * @return name
     */
    public String getName() {
        return accountName;
    }

    /**
     * Deducts funds when a bid is made.
     * @param amount of type double
     */
    public void deductFunds(double amount){
        this.balance -= amount;
    }


    /**
     * Adds funds to the account for an outbid or win action.
     * @param amount
     */
    public void addFunds(double amount){
        this.balance += amount;
    }
}
