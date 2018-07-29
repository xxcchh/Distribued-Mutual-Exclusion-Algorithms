package Utils;

import Algorithm.MutualExclusiveAlgorithm;
import Controller.Node;

/**
 * Created by chen on 7/8/18.
 */
public class MessageReceiveService implements MessageReceiver {

    private static MessageReceiveService instance = new MessageReceiveService();
    public static MessageReceiveService getInstance(){
        return instance;
    }

    private Node node;
    private MessageReceiver method;

    /*
    * Set the node
    * */
    public void connectNode (Node node){
        this.node = node;
    }

    /*
    * Get the node
    * */
    public Node getNode(){return node;}

    /*
     * Strategy method
     * */
    public void setMethod (MessageReceiver method){
        this.method = method;
    }

    // should synchronized in order to receive message one by one
    @Override
    public synchronized void receive(Message message) {
        method.receive(message);
    }


}
