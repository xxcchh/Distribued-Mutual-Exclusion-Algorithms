package Algorithm;

/**
 * Created by chen on 7/8/18.
 */
public class AlgorithmFactory {

    private static AlgorithmFactory instance = new AlgorithmFactory();

    public static AlgorithmFactory getInstance() {
        return instance;
    }

    /*
    * Get mutual exclusion strategy
    * */
    public MutualExclusiveAlgorithm getAlgorithm(String name){

        if (name.equals("distributed")){
            return new Distributed();
        }else if (name.equalsIgnoreCase("centralized")){
            return new Centralized();
        }else if (name.equalsIgnoreCase("tokenbased")) {
            return new TokenBased();
        }else if (name.equalsIgnoreCase("decentralized")) {
            return new Decentralized();
        }

        return null;

    }

}
