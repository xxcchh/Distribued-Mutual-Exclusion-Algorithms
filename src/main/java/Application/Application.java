package Application;

import Algorithm.*;
import Controller.Controller;
import Controller.Node;
import Utils.MonitorService;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;
import java.util.logging.Logger;

/**
 * Created by chen on 7/8/18.
 *
 * Simulation application
 *
 */
public class Application {

    private Controller controller;
    private Node node;
    private Random random = new Random();
    private MutualExclusiveAlgorithm strategy;
    private String fileName = "./output";
    private static final int LIMIT = 5;
    private static int WAIT_INTER = 200;

    private MonitorService monitorService;
    private static Logger logger = Logger.getLogger("Application");

    /*
    * Constructor
    *
    * @param: controller
    *       Passing controller
    *
    * */
    public Application (Controller controller){
        this.controller = controller;
        this.node = controller.getNode();
        this.strategy = controller.getStrategy();
        this.monitorService = MonitorService.getInstance();
        this.monitorService.setTargetNumberOfFinish(node.getNumberOfNode()-1);
    }

    public void start() {

        /*
        * Different strategy
        * */
        if (this.strategy.getClass() == Distributed.class){
            processRequests();
            controller.sendFinish(true);
        }else if (this.strategy.getClass() == Centralized.class) {
            if (node.getLocalInfo().isCoordinator){
                controller.sendFinish(true);
            }else {
                processRequests();
                controller.sendFinish(false);
            }
        }else if (this.strategy.getClass() == TokenBased.class) {
            int currentId = node.getLocalInfo().nodeId;
            int nextId = (currentId + 1) % node.getNumberOfNode();
            if (node.getLocalInfo().isCoordinator) {
                // only for token-based strategy
                ((TokenBased) this.strategy).unlock(currentId);
            }
            processRequests();
            ((TokenBased) this.strategy).unlock(nextId);
            controller.sendFinish(true);
        }else if (this.strategy.getClass() == Decentralized.class){
            processRequests();
            controller.sendFinish(true);
        }

        while (!monitorService.isFinish()){
            try {
                Thread.sleep(WAIT_INTER);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

//        close all connections
        controller.close();

//        exit
        System.exit(0);

    }

    /*
    * Go through each request
    * */
    private void processRequests() {

        final int numberOfRequest = node.getNumberOfRequest();
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < numberOfRequest; i++){

            int delay = random.nextInt(LIMIT);
            int time = random.nextInt(LIMIT);

            try {
                Thread.sleep(delay);
                strategy.csEnter();
                Thread.sleep(time);
                strategy.csLeave();
                writeOutput(controller.getFileName(), writeLine(delay, time, node));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        long endTime = System.currentTimeMillis();
        writeOutput(controller.getFileName(),
                writeLine(0, (int) (endTime-startTime), node));

    }

    /*
    * Prepare the line to write
    * */
    public String writeLine (int delay, int time, Node node){
        return String.format("Delay: %d Time: %d (%s)", delay, time, node);
    }

    /*
    * Write to the output file
    * */
    private void writeOutput(String f, String line){

        int index = f.lastIndexOf("/");
        f = f.substring(index+1);
        f = f + "_" + node.getLocalInfo().nodeId;

        Path currentPath = Paths.get(System.getProperty("user.dir"));
        Path filePath = Paths.get(currentPath.toString(), "output", f);

        try {
            File temp = new File(filePath.toString());
            if (!temp.exists()){
                temp.createNewFile();
            }
            BufferedWriter bw = new BufferedWriter(new FileWriter(temp, true));

            bw.write(line);
            bw.write("\n");
            bw.flush();
            bw.close();
            logger.info(String.format("Write line %s (%s)", line, node));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }




}
