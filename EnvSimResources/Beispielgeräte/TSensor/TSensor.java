
import de.htw.saar.env.sim.device.container.Device;
import de.htw.saar.env.sim.device.container.MessageContainer;

import java.util.ArrayList;

public class TSensor extends Device {

    public TSensor(){
        this.setPublishList(new ArrayList<String>());
        this.setSubscribeList(new ArrayList<String>());
        this.getPublishList().add("data/Gruppe11/Temperatur");
    }


    @Override
    public MessageContainer receive(String message) {
        return null;
    }

    @Override
    public MessageContainer send() {
        Object returned = this.getBehaviour().run();
        this.setCurrentValue("'" + returned.toString() + "°C'");
        return(new MessageContainer(this.getPublishList().get(0),returned.toString()));
    }
}
