package Agent;

import java.io.Serializable;

public class Bid implements Serializable {
    private final int agentID;
    private final int itemID;
    private final double amount;

    public Bid(int agentID, int itemID, double amount){
        this.agentID = agentID;
        this.itemID = itemID;
        this.amount = amount;
    }

    /**
     * Get agent ID
     * @return agent's ID
     */
    public int getAgentID() {
        return agentID;
    }


    /**
     * Get amount
     * @return amount
     */
    public double getAmount() {
        return amount;
    }
}
