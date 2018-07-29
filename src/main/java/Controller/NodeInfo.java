package Controller;

/**
 * Created by chen on 7/8/18.
 *
 * Node information
 *
 */
public class NodeInfo {

    //   node id
    public int nodeId = -1;
    //   host name
    public String hostName;
    //   port id
    public int port;
    //  is coordinator
    public boolean isCoordinator = false;

    public NodeInfo(){};

    public NodeInfo(int nodeId, String hostName, int port){
        this.nodeId = nodeId;
        this.hostName = hostName;
        this.port = port;
    }

    public NodeInfo(int nodeId, String hostName, int port, boolean isCoordinator){
        this(nodeId, hostName, port);
        this.isCoordinator = isCoordinator;
    }

    @Override
    public String toString() {
        return String.format("nodeId: %d, hostName: %s, port: %d", nodeId, hostName, port);
    }

}
