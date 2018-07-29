package Transport;

import Controller.Node;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 * Created by chen on 7/8/18.
 *
 * TCP transport listener, will handle each connection.
 *
 */
public class TCPServerListener implements Runnable {

    private Node node;
    private ServerSocket server;
    private boolean runFlag = true;
    private static final int WAIT_INTER = 500;

    private static Logger logger = Logger.getLogger("TCPServerListener");

    public TCPServerListener (Node node){
        try {
            this.node = node;
            this.server = new ServerSocket(node.getLocalInfo().port);
            logger.info(String.format("Server is listening at port %d (%s)",
                                                    node.getLocalInfo().port, node.toString()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        while (runFlag) {

            try {
                Socket client = server.accept();
                logger.info(String.format("Get a client (%s)", node));

                new Thread(new TCPClientHandler(client, node,
                                    true)).start();

            } catch (IOException e) {
                e.printStackTrace();
            }


        }

    }

    /*
    * Stopping listening to other servers
    * */
    public synchronized void close(){
        runFlag = false;
    }


}
