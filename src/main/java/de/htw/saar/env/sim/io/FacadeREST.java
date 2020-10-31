package de.htw.saar.env.sim.io;

import de.htw.saar.env.sim.device.container.DeviceBlueprint;
import de.htw.saar.env.sim.device.container.DeviceContainer;
import de.htw.saar.env.sim.device.container.DeviceHeader;
import de.htw.saar.env.sim.device.management.DeviceManager;
import de.htw.saar.env.sim.device.management.StatusCollector;
import de.htw.saar.env.sim.util.RESTContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Facade class, separating the REST API from the components of the simulation
 */
@Component
public class FacadeREST {

    @Autowired
    DeviceManager deviceManager;

    @Autowired
    StatusCollector statusCollector;

    public FacadeREST(){

    }
    public void addDevice(DeviceBlueprint blueprint){
        deviceManager.register(blueprint);
        
    }

    public void removeDevice(Long deviceId){
        deviceManager.unregister(deviceId);
    }

    public void changeBehaviour(String behaviour, Long deviceId){
        deviceManager.changeBehaviour(behaviour, deviceId);
    }

    public void removeAllDevices(){
        deviceManager.removeAll();
    }

    public RESTContainer getDeviceChanges(Long lastPullTime){
        return statusCollector.getDeviceChanges(lastPullTime);
    }

    public RESTContainer getAllDevices(){
        List<DeviceHeader> headers = new ArrayList<>();
        List<DeviceContainer> deviceContainers = deviceManager.getAllDevices();
        for(DeviceContainer dc : deviceContainers){
            headers.add(dc.getHeader());
        }
        return new RESTContainer(headers, true);
    }



}
