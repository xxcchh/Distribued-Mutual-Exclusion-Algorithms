package Transport;

import Controller.Node;
import Utils.Message;
import Utils.MessageReceiveService;
import Utils.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.logging.Logger;

/**
 * Created by chen on 7/8/18.
 *
 * Handle messages received from other servers
 *
 */
public class TCPClientHandler implements Runnable {

    private Node node;
    private int channelId;
    private TCPChannel client;
    private Socket socket;
    private boolean runFlag = true;
    private static Logger logger = Logger.getLogger("TCPClientHandler");
    private static final int WAIT_INTER = 500;
    private boolean isServer = false;

//    receive message service
    private MessageReceiveService messageReceiver;

    public TCPClientHandler (Socket socket, Node node, boolean isServer){
        this.node = node;
        this.socket = socket;
        this.messageReceiver = MessageReceiveService.getInstance();
        this.isServer = isServer;
    }

    public TCPClientHandler (Socket socket, Node node, boolean isServer, int channelId){
        this(socket, node, isServer);
        setClient(channelId);
    }

    /*
    * Set client information based on first message
    * */
    private synchronized void setClient (int channelId){
        try {
            this.channelId = channelId;
            client = new TCPChannel(socket, channelId);
//            add into channels map
            node.channelsMap.put(channelId, client);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

//        get the first handshake
        if (isServer) {
            try {
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                Message firstMessage = (Message) in.readObject();
                while (firstMessage == null || firstMessage.type != MessageType.HALO){
                    logger.info(String.format("Should receive halo first (%s)", node));
                    Thread.sleep(WAIT_INTER);
                    firstMessage = (Message) in.readObject();
                }
                setClient(firstMessage.sender);
            } catch (IOException | ClassNotFoundException | InterruptedException e) {
                e.printStackTrace();
            }
        }

//        deal with following messages
        Message message;

        while (runFlag){

            while ((message = client.receive()) != null){

                logger.info(String.format("Receive the message %s (%s)",
                        message.toString(), node.toString()));

                messageReceiver.receive(message);

            }

        }

    }

    /*
    * Close connection
    * */
    public synchronized void close(){
        runFlag = false;
    }


}
