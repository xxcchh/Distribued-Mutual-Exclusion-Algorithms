package Algorithm;

import Utils.*;

import java.util.logging.Logger;

/**
 * Created by chen on 7/28/18.
 */
public class Decentralized extends MutualExclusiveAlgorithm
                        implements MessageReceiver {

    // the number of yes
    private int numberOfYes = 0;

    // whether it is using the resource
    private boolean inUse = false;

    //    message sender service
    private MessageSendService messageSender;

    //    clock service
    private ClockService clockService;

    //    performance service
    private MonitorService monitorService;

    //    wait time
    private static final int WAIT_INTER = 100;

    private static Logger logger = Logger.getLogger("Decentralized");

    public Decentralized() {
        messageSender = MessageSendService.getInstance();
        clockService = ClockService.getInstance();
        monitorService = MonitorService.getInstance();

        resetReply();
    }

    public void resetReply() {
        numberOfYes = 0;
        inUse = false;
    }

    @Override
    public void csEnter() {
        clockService.increClock();
        messageSender.broadCast("request");

        while (numberOfYes < N/2){
            try {
                Thread.sleep(WAIT_INTER);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        inUse = true;
    }

    @Override
    public void csLeave() {
        inUse = false;
    }

    @Override
    public void receive(Message message) {

        MessageType messageType = message.type;
        switch (messageType) {
            case REQUEST:
                dealWithRequest(message);
                break;
            case DENY:
                dealWithDeny(message);
                break;
            case REPLY:
                dealWithReply();
                break;
            case FINISH:
                dealWithFinish();
                break;
        }

    }

    /*
    * Send requests to other replica coordinators.
    * */
    private void dealWithRequest(Message message) {
        clockService.increClock();
        int sender = message.sender;
        if (inUse){
            messageSender.send("deny", sender);
        }else {
            messageSender.send("reply", sender);
        }
    }

    /*
     * Deal with finish message
     * */
    private void dealWithFinish() {
        monitorService.increNumberOfFinish();
    }

    /*
    * If current replica is used by other process.
    * */
    private void dealWithDeny(Message message) {
        int sender = message.sender;
        messageSender.send("request", sender);
    }

    /*
    * If current replica is available.
    * */
    private void dealWithReply() {
        numberOfYes ++;
    }


}
