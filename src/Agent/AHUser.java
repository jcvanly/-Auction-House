package Agent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class AHUser {
    ObjectOutputStream auctionOut;
    ObjectInputStream auctionIn;
    ObjectOutputStream bankOut;
    ObjectInputStream bankIn;


    public void refreshConnection(Socket socket) throws IOException {
        bankOut.close();
        bankIn.close();
        socket.close();
        socket = new Socket(IP, PORT);
        bankOut = new ObjectOutputStream(socket.getOutputStream());
        bankIn = new ObjectInputStream(socket.getInputStream());
    }

}
