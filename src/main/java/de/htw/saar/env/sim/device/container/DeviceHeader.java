package de.htw.saar.env.sim.device.container;

import java.util.List;

/**
 * Header class holding meta information about containers.
 */
public class DeviceHeader {

    /**
     * Running state of the container
     */
    public enum ContainerStatus {
        running,
        failed,
        deleted,
        built,
        idle
    }

    private long id;
    private String type;
    private String behaviour;
    private ContainerStatus status;
    private Object currentValue;
    private List<String> publishList;
    private List<String> subscribeList;
    private String cycleTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBehaviour() {
        return behaviour;
    }

    public void setBehaviour(String behaviour) {
        this.behaviour = behaviour;
    }

    public ContainerStatus getStatus() {
        return status;
    }

    public void setStatus(ContainerStatus status) {
        this.status = status;
    }

    public Object getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(Object currentValue) {
        this.currentValue = currentValue;
    }

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

    public String getCycleTime() {
        return cycleTime;
    }

    public void setCycleTime(String cycleTime) {
        this.cycleTime = cycleTime;
    }
}
