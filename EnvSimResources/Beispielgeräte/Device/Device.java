
import java.util.List;

public class Device{

    private List<String> topicList;
    private String currentValue;
    private Behavior behaviour;


    
    public Device (Behavior behavior){
        this.behaviour = behavior;
        
    }
    public String receive(String args){
        return behaviour.run(args);
    }

    public String send(){
        return behaviour.run();
    }
}
