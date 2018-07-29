package Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chen on 7/14/18.
 */
public class MonitorService {

    private int numberOfFinish = 0;
    private static MonitorService instance = new MonitorService();

    private int targetNumberOfFinish = 0;

    public static MonitorService getInstance(){
        return instance;
    }

    /*
    *
    * Increase the number of nodes finished
    * */
    public synchronized void increNumberOfFinish(){
        numberOfFinish ++;
    }

    /*
    * Target number of nodes finished
    * */
    public void setTargetNumberOfFinish(int targetNumberOfFinish){
        this.targetNumberOfFinish = targetNumberOfFinish;
    }


    /*
    * Is finished
    * */
    public boolean isFinish(){
        return numberOfFinish == targetNumberOfFinish;
    }


}
