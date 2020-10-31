package de.htw.saar.env.sim.Mocks;

import de.htw.saar.env.sim.device.container.DeviceBlueprint;
import de.htw.saar.env.sim.device.container.DeviceContainer;
import de.htw.saar.env.sim.device.management.DeviceClassLoader;
import de.htw.saar.env.sim.device.management.DeviceContainerFactory;
import de.htw.saar.env.sim.device.management.DeviceManager;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.fail;

public class DeviceManagerMock extends DeviceManager {

    public DeviceManagerMock(){
        super();
        register(new DeviceBlueprint("TAktor", "TAktorF", 1));
    }

    @Override
    public void register(DeviceBlueprint blueprint){
        try {
            this.getRegister().put(((DeviceContainerFactory) ReflectionTestUtils.getField(this, "factory")).build(
                    new DeviceBlueprint("TAktor", "TAktorF", 1)).get(0));
        }catch (Exception ex){fail();}
    }


}
