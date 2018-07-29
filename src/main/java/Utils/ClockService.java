package Utils;

/**
 * Created by chen on 7/9/18.
 *
 * Clock service used in message, to store the clock or to
 * increase the clock and get local clock number
 */
public class ClockService {

    private static ClockService instance = new ClockService();
    private int clock = 0;

    public static ClockService getInstance (){
        return instance;
    }

    public synchronized void increClock(){
        clock ++;
    }

    public synchronized int getClock (){
        return clock;
    }

    public synchronized void resetClock(){
        clock = 0;
    }

    public synchronized void updateClock(int newClock){
        clock = Math.max(clock, newClock);
    }

}
