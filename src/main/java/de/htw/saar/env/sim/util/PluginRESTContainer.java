package de.htw.saar.env.sim.util;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
public class PluginRESTContainer {


    private String device;
    private List<String> behaviors;
    private String deviceName;
    private List<String> behaviorNames;


    @JsonCreator
    public PluginRESTContainer(@JsonProperty("device") String device, @JsonProperty("behaviors") List<String> behaviors, @JsonProperty("deviceName") String deviceName, @JsonProperty("behaviorNames") List<String> behaviorNames){
        this.device = device;
        this.behaviors = behaviors;
        this.deviceName = deviceName;
        this.behaviorNames= behaviorNames;
    }

    public PluginRESTContainer(String deviceName, List<String> behaviorNames){
        this.deviceName = deviceName;
        this.behaviorNames = behaviorNames;
    }
    public PluginRESTContainer(){
        this.behaviorNames = new ArrayList<String>();
        this.behaviors = new ArrayList<String>();
    }

    @XmlElement
    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
    @XmlElement
    public void setBehaviorNames(List<String> behaviorNames) {
        this.behaviorNames = behaviorNames;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public List<String> getBehaviorNames() {
        return behaviorNames;
    }

    public String getDevice() {
        return device;
    }

    public List<String> getBehaviors() {
        return behaviors;
    }

    @Override
    public String toString() {
        return getDevice();
    }
}