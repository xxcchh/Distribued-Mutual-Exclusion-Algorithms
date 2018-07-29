import Algorithm.MutualExclusiveAlgorithm;
import Application.Application;
import Controller.Controller;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

/**
 * Created by chen on 7/10/18.
 */
public class ApplicationTest {

    private Application app;
    private Controller controller;

//    @Test
//    public void shouldOutputNumberOfRequestsLines(){
//
//        final int numberOfRequest = 20;
//        final String outputFile = "./output/config-2-appTest.txt";
//        final String plate = "test";
//        final int nodeId = 0;
//
//        Controller controllerTemp = new Controller(nodeId,
//                "./configure-file/config-2-appTest.txt",
//                "tcp", "distributed");
//        controller = spy(controllerTemp);
//
//        given(controller.getStrategy()).willReturn(new MutualExclusiveAlgorithm() {
//            @Override
//            public void csEnter() {
//                return;
//            }
//
//            @Override
//            public void csLeave() {
//                return;
//            }
//        });
//
//        Application appTemp = new Application(controller);
//        app = spy(appTemp);
//
//        doReturn(plate).when(app).writeLine(anyInt(), anyInt(), anyObject());
//
//        app.start();
//
//        testFilesCorrect(nodeId, outputFile, plate, numberOfRequest);
//    }

    public void testFilesCorrect(Integer nodeId, String outputFile,
                                 String plate, int numberOfRequest) {

        int number = 0;
        try {
            File f = new File(outputFile+String.valueOf("_" + nodeId));
            BufferedReader br = new BufferedReader(new FileReader(f));
            String line = "";
            while ((line = br.readLine()) != null){
                if (!plate.isEmpty()){
                    if (line.equals(plate)){
                        number ++;
                    }
                }else {
                    number ++;
                }
            }
            assertEquals(number, numberOfRequest);
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

//    @Test
//    public void oneNodeUsingLamportAlgorithm() {
//
//        final int numberOfRequest = 10;
//        final String outputFile = "./output/config-1-algoTest.txt";
//        final int nodeId = 0;
//
//        Controller controllerTemp = new Controller(nodeId, "./configure-file/config-1-algoTest.txt",
//                "tcp", "distributed");
//        controller = spy(controllerTemp);
//
//        controller.init();
//        controller.start();
//
//        testFilesCorrect(nodeId, outputFile, "", numberOfRequest);
//
//    }

//    @Test
//    public void twoNodesUsingLamportAlgorithm() {
//
////        worker for performing mutual exclusion in different ip
//        class worker implements Runnable {
//            Controller controller;
//            worker(Controller controller) {
//                this.controller = controller;
//            }
//            @Override
//            public void run() {
//                controller.init();
//                controller.start();
//            }
//        }
//
//        // node0
//        final String inputFile0 = "./configure-file/config-2-appTest-node0.txt";
//        final String outputFile0 = "./output/config-2-appTest-node0.txt";
//        final Integer node0 = 0;
//        final Integer numberOfRequest0 = 10;
//        Thread w0 = new Thread(new worker(new Controller(node0, inputFile0, "tcp", "distributed")));
//
//        // node1
//        final String inputFile1 = "./configure-file/config-2-appTest-node1.txt";
//        final String outputFile1 = "./output/config-2-appTest-node1.txt";
//        final Integer node1 = 1;
//        final Integer numberOfRequest1 = 15;
//        Thread w1 = new Thread(new worker(new Controller(node1, inputFile1, "tcp", "lamport")));
//
//        try {
//            w0.start();
//            w1.start();
//            w0.join();
//            w1.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        testFilesCorrect(node0, outputFile0, "", numberOfRequest0);
//        testFilesCorrect(node1, outputFile1, "", numberOfRequest1);
//
//
//    }



}
