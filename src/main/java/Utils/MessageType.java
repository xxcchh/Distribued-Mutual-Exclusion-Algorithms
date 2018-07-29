package Utils;

/**
 * Created by chen on 7/9/18.
 */

public enum MessageType {

    HALO("halo"),
    REQUEST("request"),
    REPLY("reply"),
    REMOVE("remove"),
    UNLOCK("unlock"),
    DENY("deny"),
    FINISH("finish");

    private String type;
    MessageType(String type){
        this.type = type;
    }

    public String thisType (){
        return type;
    }

    /*
    * Return the type of the message
    * */
    public static MessageType getType(String type){
        if (type.equalsIgnoreCase("halo")){
            return HALO;
        }else if (type.equalsIgnoreCase("request")){
            return REQUEST;
        }else if (type.equalsIgnoreCase("reply")){
            return REPLY;
        }else if (type.equalsIgnoreCase("remove")){
            return REMOVE;
        }else if (type.equalsIgnoreCase("finish")){
            return FINISH;
        }else if (type.equalsIgnoreCase("unlock")) {
            return UNLOCK;
        }else if (type.equalsIgnoreCase("deny")) {
            return DENY;
        }
        return null;
    }

}
