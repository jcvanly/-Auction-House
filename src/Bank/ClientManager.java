package Bank;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

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

        }
    }
}
