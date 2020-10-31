
import de.htw.saar.env.sim.device.container.Behaviour;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class TAktorF extends Behaviour {

private BlockingQueue queue;

public TAktorF(){
    this.setCycleTime(0);
    queue = new ArrayBlockingQueue(10);
}

@Override
public Object run(String message) {
    ArrayList<Double> tmp = new ArrayList<>();
    double tmpR = 0;
    try {
        queue.put(Double.parseDouble(message));
        if (queue.remainingCapacity() == 0) {
            queue.drainTo(tmp);

            for(Double d : tmp){
                tmpR += d;
            };
            return ((((tmpR/10)*9/5)+32)+ "°F");
        }
    } catch (InterruptedException ex){}
    return "not enough data";
}

@Override
public Object run() {
    return null;
}
}
