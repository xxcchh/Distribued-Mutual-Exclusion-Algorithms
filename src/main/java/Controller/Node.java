package Controller;

import Transport.SocketChannel;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by chen on 7/7/18.
 *
 *  Node that stores the information about host, port, neighbors, channels.
 *
 */
public class Node {

    // node information
    private NodeInfo localInfo;
    //   total number of nodes in this distributed network
    private int numberOfNode;
    //   number of messages for this node
    private int numberOfRequest;
    //   neighbors map
    public HashMap<Integer, NodeInfo> neighborsMap;
    //   channels map
    public HashMap<Integer, SocketChannel> channelsMap;
    //   logger
    private static Logger logger = Logger.getLogger("Config");

    /*
     * Constructor
     *
     * @param fileName
     *       the name of configuration file
     *
     * */
    public Node(String fileName){
        localInfo = new NodeInfo();
        neighborsMap = new HashMap<>();
        channelsMap = new HashMap<>();
        readFile(fileName);
        printInfo();
    }


    /*
    *
    * Constructor
    *
    *  @param nodeId
    *       the id of this node
    *
    * */
    public Node (int nodeId, String fileName){
        localInfo = new NodeInfo();
        localInfo.nodeId = nodeId;
        neighborsMap = new HashMap<>();
        channelsMap = new HashMap<>();
        readFile(fileName);
        printInfo();
    }

    private void printInfo(){
        System.out.println("The node id is: " + localInfo.nodeId);
        System.out.println("The host name is: " + localInfo.hostName);
        System.out.println("The port is: " + localInfo.port);
    }

    public synchronized NodeInfo getLocalInfo(){
        return localInfo;
    }

    public int getNumberOfRequest() {
        return numberOfRequest;
    }

    public int getNumberOfNode(){return numberOfNode;}

    /*
    * Add to the channel map
    * */
    public synchronized void addChannel(SocketChannel channel, Integer channelId){
        channelsMap.put(channelId, channel);
    }

    /*
    * Get Channel size
    * */
    public synchronized int getChannelSize(){
        return channelsMap.size();
    }

    /*
    * Get client channel
    * */
    public synchronized SocketChannel getChannel(int channelId){
        return channelsMap.get(channelId);
    }

    /*
    * Add to the neighbor map
    * */
    public synchronized void addNeighbor(NodeInfo neighbor, Integer neighborId){
        neighborsMap.put(neighborId, neighbor);
    }

    /*
     * Read the configuration file and get the information
     *
     * @param fileName
     *       name of configuration file
     *
     * */
    private void readFile(String fileName){

        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String line;
            boolean isStart = true;
            while ((line = br.readLine()) != null){
                line = line.trim();
                if (line.isEmpty()){
                    continue;
                }else {
                    String[] lines = line.split("\\s+");
                    if (lines.length != 3){
                        logger.info("The line does not have 3.");
                        break;
                    }else {
                        if (isStart){
                            localInfo.nodeId = localInfo.nodeId == -1? Integer.parseInt(lines[0]):
                                    localInfo.nodeId;
                            // if the node id == 0, then make it a coordinator
                            if (localInfo.nodeId == 0){
                                localInfo.isCoordinator = true;
                            }
                            numberOfNode = Integer.parseInt(lines[1]);
                            numberOfRequest = Integer.parseInt(lines[2]);
                            isStart = false;
                        }else {
                            Integer otherId = Integer.parseInt(lines[0]);
                            String otherHostName = lines[1];
                            Integer otherPort = Integer.parseInt(lines[2]);
                            if (otherId == localInfo.nodeId){
                                localInfo.hostName = otherHostName;
                                localInfo.port = otherPort;
                            }else {
                                addNeighbor(new NodeInfo(otherId, otherHostName,
                                        otherPort), otherId);
                            }
                        }
                    }
                }
            }

            if (localInfo.nodeId >= numberOfNode || localInfo.nodeId < 0){
                throw new InvalidParameterException("The node id is invalid.");
            }

        } catch (FileNotFoundException e) {
            logger.info("The configuration file is not found!");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public String toString() {
        return localInfo.toString();
    }

    /*
     * Check if all connections have been built
     * */
    public boolean isAllSetUp(){

        if (neighborsMap.size() ==
                getChannelSize()){
            return true;
        }

        return false;

    }

    /*
    * Get coordinator id
    * */
    public int getCoordinatorId(){
        return neighborsMap.entrySet()
                .stream()
                .filter(e -> e.getValue().isCoordinator)
                .mapToInt(k -> k.getKey())
                .sum();
    }

}
