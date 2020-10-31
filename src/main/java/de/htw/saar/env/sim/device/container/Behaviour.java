package de.htw.saar.env.sim.device.container;

/**
 * Behaviour class to implement exchangeable behaviour
 * for device objects
 *
 * CycleTime = 0 -> no auto scheduling
 */
public abstract class Behaviour implements IBehaviour{

    /**
     * Pause time before the container is relaunched
     * cycleTime == 0 -> no relaunching
     */
    private long cycleTime;

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
    @Override
    public abstract Object run(String message);

    @Override
    public abstract Object run();

    public long getCycleTime() {
        return cycleTime;
    }

    public void setCycleTime(long cycleTime) {
        this.cycleTime = cycleTime;
    }
}
