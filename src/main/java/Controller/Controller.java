package Controller;

import Algorithm.AlgorithmFactory;
import Algorithm.MutualExclusiveAlgorithm;
import Application.Application;
import Transport.TCPChannel;
import Transport.TCPClientHandler;
import Transport.TCPServerListener;
import Utils.*;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 * Created by chen on 7/8/18.
 */
public class Controller {

    private Node node;
    private String fileName;
    private String algorithmStrategy;
    private MutualExclusiveAlgorithm algorithmMethod;
    private static final int WAIT_INTER = 1000;
    private static final int THREAD_NUMBER = 10;
    private ExecutorService pool;
    private List<TCPServerListener> serverThreads;
    private List<TCPClientHandler> clientThreads;
    private static Logger logger = Logger.getLogger("Controller");
    private MessageReceiveService messageReceiver;
    private MessageSendService messageSender;
    private ClockService clockService;

    /*
    * Constructor
    *
    * @param: nodeId
    *           node's id
    * @param: fileName
    *           file's path where configuration file lies
    * @param: trans
    *           transport method
    * @param: algo
    *           mutual exclusion method
    *
    * */
    public Controller (int nodeId, String fileName, String algo){
        this.node = new Node(nodeId, fileName);
        this.fileName = fileName;
        this.algorithmStrategy = algo;
        this.algorithmMethod = AlgorithmFactory.getInstance().getAlgorithm(this.algorithmStrategy);
        this.algorithmMethod.setNode(node);

        this.pool = Executors.newFixedThreadPool(THREAD_NUMBER);
        this.serverThreads = new ArrayList<>();
        this.clientThreads = new ArrayList<>();

        this.messageReceiver = MessageReceiveService.getInstance();
        this.messageReceiver.setMethod((MessageReceiver) this.algorithmMethod);
//        register the message receiving service
        messageReceiver.connectNode(node);

        this.messageSender = MessageSendService.getInstance();
        this.messageSender.setMethod(this.algorithmMethod);
//        register the message sending service
        messageSender.connectNode(node);

        this.clockService = ClockService.getInstance();
    }

    /*
    * Get node
    *
    * @return: node
    *           the node setting up
    * */
    public Node getNode(){
        return node;
    }

    /*
    * Get fileName of the input
    *
    * @return: fileName
    *           the name and path of the file
    * */
    public String getFileName() {
        return fileName;
    }

    /*
    * Get strategy
    *
    * @return: algorithmStrategy
    *           the method used to achieve mutual exclusion
    * */
    public MutualExclusiveAlgorithm getStrategy (){
        return algorithmMethod;
    }

    /*
    *
    * Method to initiate the setup method
    *
    * */
    public void init() {

        initTCP();

//        check if the node has built all connections
        while (!node.isAllSetUp()){
            try {
                logger.info(String.format("Not all connections have been built neighbor: %d channel: %d (%s)",
                        node.neighborsMap.size(), node.channelsMap.size(), node.toString()));
                Thread.sleep(WAIT_INTER);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        logger.info(String.format("All connections have been built (%s)", node.toString()));

        logger.info("Register message receiving service");

//        reset the clock service
        clockService.resetClock();

        logger.info(String.format("Set up is ready (%s)", node));

    }

    /*
    *  Start the service and
    * */
    public void start(){

        Application app = new Application(this);
        app.start();

    }

    /*
    * Setup tcp connection
    * */
    public void initTCP (){

//        open server listener
        initTCPServer();

//        connect to other servers
        initTCPClient();

    }

    /*
    * Setup tcp server listener
    * */
    public void initTCPServer (){

//        open server and listen for the incoming clients
        TCPServerListener serverListener =
                new TCPServerListener(node);

        serverThreads.add(serverListener);
        pool.execute(serverListener);

    }

    /*
    * Setup tcp client, which sends join request to nodes which have larger id
    * */
    public void initTCPClient () {

        logger.info(String.format("Begin to send connections to others (%s)", node));
        for (int otherId: node.neighborsMap.keySet()){

            NodeInfo otherNode = node.neighborsMap.get(otherId);

//            send halo message to node whose id is larger
            if (otherId > node.getLocalInfo().nodeId) {

                Socket target = null;
                while (target == null) {

                    String otherHost = otherNode.hostName;
                    int otherPort = otherNode.port;

                    try {
                        target = new Socket(InetAddress.getByName(otherHost),
                                            otherPort);

                        TCPChannel targetChannel = new TCPChannel(target, otherId);

//                    add channels to the channels map
                        node.addChannel(targetChannel, otherId);

//                    send shake hand message to the node
                        Message handshake = new Message(node.getLocalInfo().nodeId,
                                                            otherId, "halo");
                        TCPChannel channel = (TCPChannel) node.getChannel(otherId);
                        channel.send(handshake);

                        logger.info(String.format("Handshake with node %d (%s)", otherId,
                                node.toString()));


                    }catch (ConnectException e){
                        try {
                            logger.info(String.format("Waiting for other nodes to join (%s)", node));
                            Thread.sleep(WAIT_INTER);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                // listen to the server
                TCPClientHandler clientListener = new TCPClientHandler(target, node, false, otherId);
                clientThreads.add(clientListener);

                pool.execute(clientListener);
            }

        }

    }

//  send to all it is finish
    public void sendFinish(boolean isBroadCast){
        if (isBroadCast){
            messageSender.broadCast("finish");
        }else {
            messageSender.sendToCoordinator("finish");
        }
    }

// safely close all connections
    public synchronized void close() {
        serverThreads.forEach(t -> t.close());
        clientThreads.forEach(t -> t.close());
    }

}
