package de.htw.saar.env.sim.Mocks;

import de.htw.saar.env.sim.device.scheduling.ThreadManager;
import static org.junit.jupiter.api.Assertions.*;

public class ThreadManagerMock extends ThreadManager {
    @Override
    public void addWorker(Thread worker, long containerId) {
    }

    @Override
    public void removeWorker(long containerId) {
    }

    @Override
    public boolean contains(long id) {
        return false;
    }

}
