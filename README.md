# Project 5: Distributed Auction

## Project Description
In this project, we are simulating a distributed auction system. The system includes multiple auction houses selling items, multiple agents buying items, and a bank to keep track of everyone's funds. The goal is to design and implement a system with a solid client-server architecture that manages the complexities of a distributed system.

## Components
### 1. Bank
The bank exists on one machine at a static known address. It's responsible for managing the accounts of both agents and auction houses. It also maintains a list of active auction houses and their addresses for the agents to connect to.

### 2. Auction House
Each auction house is dynamically created and registers with the bank upon creation. It hosts a list of items being auctioned and tracks the current bidding status of each item.

### 3. Agent
Agents are also dynamically created. They open a bank account and receive a list of active auction houses from the bank. Agents can make bids on items and receive status updates on the auction proceedings.

## Console Based User Interface
The agent user interface is console-based. It provides a simple and efficient way for users to interact with the system. Users can check the agent's bank balance, view items at the auction houses, place bids, and get the current bid status.

## How to Run the Program
1. Start the bank program.
2. Dynamically create auction houses and agents. This can be done by running the respective jar files for auction houses and agents.
3. Agents can now interact with the auction houses for bidding on items.


