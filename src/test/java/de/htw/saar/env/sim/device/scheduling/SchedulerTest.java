package de.htw.saar.env.sim.device.scheduling;

import de.htw.saar.env.sim.Mocks.DeviceManagerMock;
import de.htw.saar.env.sim.Mocks.ThreadManagerMock;
import de.htw.saar.env.sim.device.container.DeviceContainer;
import de.htw.saar.env.sim.device.container.MessageContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtil.testUtils;

import static org.junit.jupiter.api.Assertions.*;

class SchedulerTest {

    Scheduler scheduler;

    @BeforeEach
    public void setUp(){
        testUtils.initializeIOManager();
        scheduler = new Scheduler();
        scheduler.deviceManager = new DeviceManagerMock();
        scheduler.threadManager = new ThreadManagerMock();
    }

    @Test
    public void scheduleSingle() {
        scheduler.scheduleSingle((long)0, new MessageContainer("TestTopic", "TestPayload"));
    }

    @Test
    public void reschedule(){
        scheduler.reschedule(0);
    }

    @Test
    public void writeback(){
        scheduler.writeback(scheduler.deviceManager.getRegister().getContainer((long)0));
    }

    @Test
    public void writebackWithCycleTime(){
        DeviceContainer tmp = scheduler.deviceManager.getRegister().getContainer((long)0);
        tmp.getDevice().getBehaviour().setCycleTime((long) 10000);
        scheduler.writeback(tmp);
        assertEquals(1,scheduler.rescheduleQueue.size());
    }
}