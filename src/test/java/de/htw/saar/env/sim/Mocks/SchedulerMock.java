package de.htw.saar.env.sim.Mocks;

import de.htw.saar.env.sim.device.container.MessageContainer;
import de.htw.saar.env.sim.device.scheduling.Scheduler;

import java.util.ArrayList;
import java.util.List;

public class SchedulerMock extends Scheduler {

    @Override
    public ArrayList<Long> schedule(List<Long> devices, MessageContainer message) { return new ArrayList<Long>(); }

    @Override
    public void scheduleSingle(Long device, MessageContainer message) { }

    @Override
    public boolean removeDevice(long containerId) { return true; }

    @Override
    public void removeAllDevices() { }
}
