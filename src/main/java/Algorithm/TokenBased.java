package Algorithm;

import Utils.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by chen on 7/26/18.
 */
public class TokenBased extends MutualExclusiveAlgorithm
                        implements MessageReceiver {

    // if it is node's turn to access the cs
    private boolean isTurn = false;

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

    public TokenBased() {
        messageSender = MessageSendService.getInstance();
        monitorService = MonitorService.getInstance();
        clockService = ClockService.getInstance();

        resetTurn();
    }

    // if it is not current node's turn
    private void resetTurn() {
        isTurn = false;
    }

    @Override
    public void csEnter() {

        clockService.increClock();
        while (!isTurn){
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

        MessageType type = message.type;

        switch(type) {
            case FINISH:
                dealWithFinish();
                break;
            case UNLOCK:
                dealWithUnlock();
                break;
        }

    }

    // send unlock
    public void unlock(int id) {
        if (id == nodeId){
            dealWithUnlock();
        }else {
            messageSender.send("unlock", id);
        }
    }

    // deal with finish
    private void dealWithFinish() {
        monitorService.increNumberOfFinish();
    }

    // deal with unlock
    private synchronized void dealWithUnlock() {
        isTurn = true;
    }

}
