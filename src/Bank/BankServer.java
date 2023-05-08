package Bank;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class BankServer {
    private boolean connected;
    private final int bankPort;
    private final ServerSocket bankServerSocket;
    private final Bank bank;

    public BankServer() throws IOException {
        connected = true;
        bankPort = 55555;
        System.out.println("Starting the bank server...");

        bankServerSocket = new ServerSocket(bankPort);
        bank = new Bank();
        runBank();
    }

    public static void main(String[] args) throws IOException {
        new BankServer();
    }

    private void runBank() throws IOException {
        System.out.println("Bank server is running, waiting for connections...");
        while (connected) {
            Socket connectionSocket = bankServerSocket.accept();
            System.out.println("Connection established with " + connectionSocket.getInetAddress() + ":" + connectionSocket.getPort());
            ObjectOutputStream outputStream = new ObjectOutputStream(connectionSocket.getOutputStream());
            ObjectInputStream inputStream = new ObjectInputStream(connectionSocket.getInputStream());
            ClientManager clientManager = new ClientManager(connectionSocket, outputStream, inputStream, bank);
            Thread bankThread = new Thread(clientManager);
            bankThread.start();
        }
        //Disconnected
        bankServerSocket.close();
    }

}
