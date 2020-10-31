package de.htw.saar.env.sim.device.container;

public interface IBehaviour {

    /**
     * Abstract run method initiating the actual behaviour
     * Overloaded by concrete behaviours
     *
     * @param message Optional subscribe message when launching
     * as a reaction to an incoming message from the MQTT Broker
     *
     * @return Value produced by the behaviour class object
     *         Optional return value -> may be null
     */
    public abstract Object run(String message);
    public abstract Object run();
}
