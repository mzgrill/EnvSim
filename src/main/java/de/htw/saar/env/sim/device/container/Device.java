package de.htw.saar.env.sim.device.container;

import java.util.List;

/**
 * Device class containing logic, exchangeable behaviour and
 * MQTT-connection information
 */

public abstract class Device implements IDevice{
    private List<String> publishList;
    private List<String> subscribeList;
    private Object currentValue;
    private Behaviour behaviour;

    /**
     * Method called by the message-scheduling upon receiving
     * messages from a subscribed topic of the concrete device
     * @param message
     * @return Message produced by the device, ready to be wrapped
     * and published
     */
    @Override
    public abstract MessageContainer receive(String message);

    /**
     * Method called by the auto-scheduling to
     * run the device behaviour
     * @return Message produced by the device, ready to be wrapped
     * and published
     */
    @Override
    public abstract MessageContainer send();

    public List<String> getPublishList() {
        return publishList;
    }

    public void setPublishList(List<String> publishList) {
        this.publishList = publishList;
    }

    public List<String> getSubscribeList() {
        return subscribeList;
    }

    public void setSubscribeList(List<String> subscribeList) {
        this.subscribeList = subscribeList;
    }

    public Object getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(Object currentValue) {
        this.currentValue = currentValue;
    }

    public Behaviour getBehaviour() {
        return behaviour;
    }

    public void setBehaviour(Behaviour behaviour) {
        this.behaviour = behaviour;
    }
}
