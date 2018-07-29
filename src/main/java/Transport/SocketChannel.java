package Transport;

import Utils.Message;

/**
 * Created by chen on 7/8/18.
 *
 * Channel that stores the destination node, receives and sends message to the destination.
 *
 */
public abstract class SocketChannel {

    protected int channelId;

    public SocketChannel(Integer channelId) {
        this.channelId = channelId;
    }

    public abstract Message receive();
    public abstract void send(Message message);

}
