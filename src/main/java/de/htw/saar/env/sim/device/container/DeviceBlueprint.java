package de.htw.saar.env.sim.device.container;

/**
 * Container class holding meta information
 * used for building DeviceContainers in a
 * Factory
 */
public class DeviceBlueprint {

    public DeviceBlueprint(String device, String behaviour, int amount){
        this.device = device;
        this.behaviour = behaviour;
        this.amount = amount;
    }

    /**
     * Type of device to build
     */
    private String device;

    /**
     * Behaviour of the device to build
     */
    private String behaviour;

    /**
     * Amount of devices to build
     */
    private int  amount;

    public String getDevice() {
        return device;
    }
    public String getBehaviour() {
        return behaviour;
    }
    public int getAmount() {
        return amount;
    }
}
