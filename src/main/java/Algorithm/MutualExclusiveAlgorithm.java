package Algorithm;

import Controller.Node;
import Controller.NodeInfo;
import Utils.Message;

/**
 * Created by chen on 7/8/18.
 */
public abstract class MutualExclusiveAlgorithm {

    /*
    * Enter critical section strategy.
    * */
    abstract public void csEnter();

    /*
    * Leave critical section strategy.
    * */
    abstract public void csLeave();

    /*
    * Receive the message
    * */
    abstract public void receive(Message message);

    /*
    * Node id and target number of reply
    * */
    protected int nodeId;
    protected int N;
    protected int targetNumberOfReply;
    protected NodeInfo nodeInfo;


    /*
    * Set node info
    * */
    public void setNode(Node node) {

        this.N = node.getNumberOfNode();
        this.nodeId = node.getLocalInfo().nodeId;
        this.nodeInfo = node.getLocalInfo();

        targetNumberOfReply = N - 1;
    }


}
