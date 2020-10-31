package de.htw.saar.env.sim.device.scheduling;

import org.junit.jupiter.api.Test;
import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.*;

class ThreadManagerTest {

    ThreadManager manager = new ThreadManager();

    @Test
    void addWorker() {
        manager.addWorker(new Thread(new Runnable() {
            @Override
            public void run() {
            }
        }), new Long(1));

        assertEquals(1, manager.getThreadMapSize());
    }

    @Test
    void removeWorker() {
        this.addWorker();
        int tmp = manager.getThreadMapSize();
        manager.removeWorker(1);
        assertEquals(tmp - 1, manager.getThreadMapSize());
    }

    @Test
    void terminate() throws InterruptedException {
        manager.addWorker(new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        sleep(100);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }), 2);
        sleep(100);
        if (!manager.contains(2)){fail();}
        manager.terminate((long) 2);
        assertFalse(manager.contains((long) 2));
    }

}