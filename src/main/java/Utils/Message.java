package Utils;

import java.io.Serializable;

/**
 * Created by chen on 7/8/18.
 */
public class Message implements Serializable {

    public final long serialVersionId = 1L;
    //    sender id
    public final int sender;
    //    receiver id
    public int receiver;
    //    type of the message connect, request, reply, remove
    public final MessageType type;
    //    current time of this message
    public int clock;

    public Message (int sender, int receiver, String type) {
        this(sender, type);
        this.receiver = receiver;
    }

    public Message (int sender, String type) {
        this.sender = sender;
        this.type = MessageType.getType(type);
    }

    /*
    * Set the message clock
    * */
    public synchronized void setClock (int clock){
        this.clock = clock;
    }

    /*
    * Set the receiver
    * */
    public synchronized void setReceiver(int receiver){this.receiver = receiver;}

    @Override
    public String toString() {
        return String.format("clock: %d, sender: %d, receiver: %d, type: %s", clock, sender, receiver, type);
    }



}
