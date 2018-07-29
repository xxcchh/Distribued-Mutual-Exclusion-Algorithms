package Algorithm;

import Utils.*;

import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.logging.Logger;

/**
 * Created by chen on 7/8/18.
 */
public class Distributed extends MutualExclusiveAlgorithm
                            implements MessageReceiver {

    /*
    * Message comparator
    * */
    class MessageComparator implements Comparator<Message>{
        @Override
        public int compare(Message o1, Message o2) {
            if (o1.clock < o2.clock){
                return -1;
            }else if (o1.clock > o2.clock){
                return 1;
            }else if (o1.clock == o2.clock && o1.sender < o2.sender){
                return -1;
            }
            return 1;
        }
    }

//  concurrent message priority queue
    private PriorityBlockingQueue<Message> messageQueue;

//    number of replies received
    private int numberOfReply = 0;

//    message sender service
    private MessageSendService messageSender;

//    clock service
    private ClockService clockService;

//    performance service
    private MonitorService monitorService;

//    maximum message at a time
    private static final int MAX_RATE = 1000;

//    wait time
    private static final int WAIT_INTER = 100;

    private static Logger logger = Logger.getLogger("Distributed");

    public Distributed(){
        messageSender = MessageSendService.getInstance();
        clockService = ClockService.getInstance();
        monitorService = MonitorService.getInstance();

        messageQueue = new PriorityBlockingQueue<>(MAX_RATE, new MessageComparator());
        resetReply();
    }

    /*
    * Reset the number of reply
    * */
    private synchronized void resetReply(){
        numberOfReply = 0;
    }

    /*
    * Increment the number of reply
    * */
    private synchronized void increReply(){
        numberOfReply ++;
    }

    @Override
    public void csEnter() {

        resetReply();
        updateQueue(messageSender.prepareMessage(nodeId, "request"), true);

        clockService.increClock();

        messageSender.broadCast("request");

//        wait until it is permitted to enter the cs
        while (!readyToEnter()){
            try {
                Thread.sleep(WAIT_INTER);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private boolean readyToEnter(){
        return numberOfReply == targetNumberOfReply &&
                !messageQueue.isEmpty() &&
                messageQueue.peek().sender == nodeId;
    }

    @Override
    public void csLeave() {

        updateQueue(null, false);
        clockService.increClock();

        messageSender.broadCast("remove");

    }

    /*
    * Update the message queue
    * */
    private void updateQueue(Message message, boolean isAdd){
        if (isAdd){
            messageQueue.offer(message);
        }else {
            messageQueue.poll();
        }
    }

    @Override
    public void receive(Message message) {

//        increment the clock
        clockService.updateClock(message.clock);

//        give response according to message type
        MessageType type = message.type;
        switch (type){
            case REQUEST: dealWithRequest(message);
                            break;
            case REPLY: dealWithReply(message);
                            break;
            case REMOVE: dealWithRemove(message);
                            break;
            case FINISH: dealWithFinish();
                            break;
        }

    }

    /*
    * When receive a request message
    * */
    private void dealWithRequest(Message message){

        updateQueue(message, true);
        clockService.increClock();

        messageSender.send("reply", message.sender);

    }

    /*
    * When receive a reply message
    * */
    private void dealWithReply(Message message) {

        increReply();

    }


    /*
    * When receive a remove message
    * */
    private void dealWithRemove(Message message) {

        updateQueue(null, false);

    }

    /*
     * When receive a finish message
     * */
    private void dealWithFinish() {

        monitorService.increNumberOfFinish();

    }



}
