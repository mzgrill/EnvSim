package de.htw.saar.env.sim.device.management;

import de.htw.saar.env.sim.device.container.DeviceBlueprint;
import org.junit.jupiter.api.Test;
import testUtil.testUtils;

import static org.junit.jupiter.api.Assertions.*;

class DeviceContainerFactoryTest {

    DeviceContainerFactory factory = new DeviceContainerFactory();

    @Test
    void build() throws IllegalAccessException, ClassNotFoundException, InstantiationException {
        testUtils.initializeIOManager();
        assertEquals(1,factory.build(new DeviceBlueprint("TAktor","TAktorF",1)).size());
    }

    @Test
    void buildBehaviour() throws IllegalAccessException, ClassNotFoundException, InstantiationException {
        testUtils.initializeIOManager();
        assertEquals(0, factory.buildBehaviour("TAktorF").getCycleTime());
    }
}