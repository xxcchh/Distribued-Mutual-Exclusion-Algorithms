package Transport;


import Controller.Controller;


/**
 * Created by chen on 7/8/18.
 */
public class ServerMain {

    public static void main(String[] args) {

          String inputFile = args[0];
          Integer nodeId = Integer.parseInt(args[1]);
          String strategy = args[2];

          Controller controller = new Controller(nodeId, inputFile,
                                strategy);
          controller.init();
          controller.start();

    }



}


