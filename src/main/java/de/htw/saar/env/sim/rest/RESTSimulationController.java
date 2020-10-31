package de.htw.saar.env.sim.rest;

import de.htw.saar.env.sim.device.container.DeviceBlueprint;
import de.htw.saar.env.sim.io.FacadeREST;
import de.htw.saar.env.sim.util.RESTContainer;
import org.apache.logging.log4j.core.tools.picocli.CommandLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class RESTSimulationController {

    @Autowired
    FacadeREST fassade;

    @PutMapping("/addDevice")
    public void addDevice(@RequestBody DeviceBlueprint blueprint){
        System.out.println("addDevice " + blueprint.getBehaviour());
        fassade.addDevice(blueprint);
    }

    @DeleteMapping("/deleteDevice")
    public void removeDevice(@RequestParam (value="id", defaultValue  = "1") Long deviceId){
        System.out.println("deleteDevice: " + deviceId);
        fassade.removeDevice(deviceId);

    }



    @PostMapping("/changeBehavior")
    public void changeBehavior(@RequestParam("name") String behavior, @RequestParam("id") long deviceId){
        System.out.println("changeBehavior  " + deviceId);
        //fassade.changeBehaviour(behavior, deviceId);
    }

    @DeleteMapping("/deleteAllDevices")
    public void deleteAllDevices(){
        System.out.println("deleteAllDevices");
        fassade.removeAllDevices();
    }

    @GetMapping("/deviceChanges")
    public RESTContainer getDeviceChanges(@RequestParam("time") long lastPullTime){
        System.out.println("deviceChanges" + fassade.getDeviceChanges(lastPullTime).getHeaderList());
        return fassade.getDeviceChanges(lastPullTime);

    }
    @GetMapping("/getAllDevices")
    public RESTContainer getDeviceChanges(){
        return fassade.getAllDevices();

    }

}


/**


**/