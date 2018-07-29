package Algorithm;

import Utils.*;

import java.util.logging.Logger;

/**
 * Created by chen on 7/15/18.
 */
public class Centralized extends MutualExclusiveAlgorithm
                            implements MessageReceiver {

    // tell if it is replied
    private boolean isReplied;

    // message sender
    private MessageSendService messageSender;

    // performance service
    private MonitorService monitorService;

    // clock service
    private ClockService clockService;

    // wait interval
    private static final int WAIT_INTER = 100;

    // is coordinator
    private static Logger logger = Logger.getLogger("Centralized");

    public Centralized() {
        clockService = ClockService.getInstance();
        monitorService = MonitorService.getInstance();
        messageSender = MessageSendService.getInstance();

        resetReply();
    }

    /*
    * Reset the reply status
    * */
    private synchronized void resetReply() {
        isReplied = false;
    }

    @Override
    public void csEnter() {

        resetReply();
        clockService.increClock();

        messageSender.sendToCoordinator("request");

        while (!isReplied){
            try {
                Thread.sleep(WAIT_INTER);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void csLeave() {

        clockService.increClock();

    }

    @Override
    public void receive(Message message) {

        clockService.updateClock(message.clock);

        // give response according to message type
        MessageType type = message.type;

        switch(type){

            case REQUEST:
                dealWithRequest(message);
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
    * Deal with finish message
    * */
    private void dealWithFinish() {
        monitorService.increNumberOfFinish();
    }

    /*
    * Deal with request message
    * */
    private void dealWithRequest(Message message) {
        messageSender.send("reply", message.sender);
    }

    /*
    * Deal with reply message
    * */
    private void dealWithReply() {
        isReplied = true;
    }


}
