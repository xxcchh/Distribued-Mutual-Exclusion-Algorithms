package Transport;

import Utils.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by chen on 7/8/18.
 */
public class TCPChannel extends SocketChannel {

//    /*
//     * outputstream of client socket
//     * */
//    private ObjectInputStream in;
//
//    /*
//     * inputstream of client socket
//     * */
//    private ObjectOutputStream out;
    private Socket socket;


    public TCPChannel(Socket socket, Integer channelId)
            throws IOException {
        super(channelId);
        this.socket = socket;
    }

    /*
     * Send message
     * */
    public void send(Message message) {
        try {
            ObjectOutputStream obj = new ObjectOutputStream(socket.getOutputStream());
            obj.writeObject(message);
            obj.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    };

    /*
     * Receive message
     * */
    public Message receive() {
        try {
            ObjectInputStream obj = new ObjectInputStream(socket.getInputStream());
            return (Message) obj.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }

}
