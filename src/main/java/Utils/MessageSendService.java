package Utils;

import Algorithm.MutualExclusiveAlgorithm;
import Controller.Node;
import Transport.SocketChannel;

import java.util.logging.Logger;

/**
 * Created by chen on 7/8/18.
 */
public class MessageSendService {

    private static MessageSendService instance = new MessageSendService();
    public static MessageSendService getInstance() {
        return instance;
    }

    private Node node;
    private MutualExclusiveAlgorithm method;

    private static Logger logger = Logger.getLogger("MessageSendService");

    /*
    * Connect node
    * */
    public void connectNode (Node node){
        this.node = node;
    }

    /*
    * Strategy method
    * */
    public void setMethod (MutualExclusiveAlgorithm method){
        this.method = method;
    }

    /*
    * Send message to a channel
    * */
    public void send(String type, int channelId) {
        Message message = prepareMessage(channelId, type);
        logger.info(String.format("Send message %s (%s)", message, node));
        SocketChannel channel = node.getChannel(channelId);
        channel.send(message);
    }

    /*
    * Prepare message
    * */
    public Message prepareMessage(int channelId, String type) {
        Message message = new Message(node.getLocalInfo().nodeId, channelId, type);
        message = updateMessage(message);
        return message;
    }

    /*
    * Update message
    * */
    public Message updateMessage(Message message){
        message.setClock(ClockService.getInstance().getClock());
        return message;
    }

    /*
    * Broadcast message to others
    * */
    public void broadCast(String type){
        node.channelsMap.keySet().forEach(channelId ->
                                    send(type, channelId));
    }

    public Node getNode() {
        return node;
    }

    /*
    * Send to the coordinator
    * */
    public void sendToCoordinator(String type){
        int coordinatorId = node.getCoordinatorId();
        send(type, coordinatorId);
    }


}
