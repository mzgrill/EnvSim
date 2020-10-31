package de.htw.saar.env.sim.device.container;

public interface IDevice {

    /**
     * Method called by the message-scheduling upon receiving
     * messages from a subscribed topic of the concrete device
     * @param message
     * @return Message produced by the device, ready to be wrapped
     * and published
     */
    public abstract MessageContainer receive(String message);

    /**
     * Method called by the auto-scheduling to
     * run the device behaviour
     * @return Message produced by the device, ready to be wrapped
     * and published
     */
    public abstract MessageContainer send();
}
