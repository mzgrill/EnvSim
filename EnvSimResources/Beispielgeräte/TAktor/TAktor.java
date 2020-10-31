import de.htw.saar.env.sim.device.container.Device;
import de.htw.saar.env.sim.device.container.MessageContainer;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class TAktor extends Device {



    public TAktor(){
        this.setPublishList(new ArrayList<String>());
        this.setSubscribeList(new ArrayList<String>());
        this.getSubscribeList().add("data/Gruppe11/Temperatur");
    }


    @Override
    public MessageContainer receive(String message) {
        String s = (String) this.getBehaviour().run(message);
        if (!s.equals("not enough data")) {
            this.setCurrentValue("'" + s + "'");
        }
        return null;
    }

    @Override
    public MessageContainer send() {
        return null;
    }
}
