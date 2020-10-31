package de.htw.saar.env.sim.device.scheduling;

import de.htw.saar.env.sim.device.container.DeviceContainer;
import de.htw.saar.env.sim.device.container.MessageContainer;
import de.htw.saar.env.sim.device.management.DeviceManager;
import de.htw.saar.env.sim.io.SystemLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import static de.htw.saar.env.sim.io.LogStrings.*;

/**
 * Scheduler class, managing the execution and timed invocation of DeviceContainers
 * Acts as mediator between running workers and the Register
 *
 * Runnable thread manages a DelayQueue to schedule Containers
 */
@Service
public class Scheduler implements Runnable{

    @Autowired
    DeviceManager deviceManager;

    @Autowired
    ThreadManager threadManager;

    @Autowired
    SystemLogger logger;

    DelayQueue<DelayQueueContainer> rescheduleQueue;
    Thread worker;

    public Scheduler(){
        rescheduleQueue = new DelayQueue<>();
        worker = new Thread(this);
        worker.start();
    }

    /**
     * Method called by Distributor class objects to invoke DeviceContainers to process a incoming message
     *
     * @param devices List of devices that own a subscription on the message's topic
     * @param message Concrete message received by the MQTT-Client
     */
    public ArrayList<Long> schedule(List<Long> devices, MessageContainer message) {
        ArrayList<Long> toBuffer = new ArrayList<>();
        devices.forEach(device -> {
            try {
                if (threadManager.contains(device)) {
                toBuffer.add(device);
                } else {
                scheduleSingle(device,message);
                }
            } catch (Exception ex) {
                logger.logError(SCHEDULER_SCHEDULE_MESSAGE);
            }

        });
        return toBuffer;
    }

    /**
     * Schedules a single Device
     * @param device the ID of the device
     * @param message the received message
     */
    public void scheduleSingle(Long device, MessageContainer message){
        DeviceContainer current = deviceManager.getRegister().getContainer(device);
        synchronized (current) {
            current.setMessage(message.getPayload());
            current.setTopic(message.getTopic());
            current.setReceive(true);
        }
            threadManager.addWorker(new Thread(current), current.getHeader().getId());
    }

    /**
     * Method to invoke a Device not present in the DelayQueue
     * After execution, worker will add itself to the Queue and join the reschedule cycle
     */
    public void reschedule(long containerId){
        try {
            DeviceContainer toBeScheduled = deviceManager.getRegister().getContainer(containerId);
            threadManager.addWorker(new Thread(toBeScheduled), containerId);
        } catch (Exception ex)  {

        }
    }

    /**
     * Writeback method called by terminating workers to update their state in the Register and requeue themselves
     * Containers that have been deleted or changed their behaviour are not rescheduled
     */
    public void writeback(DeviceContainer container){
        threadManager.removeWorker(container.getHeader().getId());
        try {
            deviceManager.getRegister().put(container);
            if (container.getDevice().getBehaviour().getCycleTime() != 0) {
                rescheduleQueue.add(new DelayQueueContainer(container));
            }
        }catch (NullPointerException exception){
            logger.logInfo(SCHEDULER_RESCHEDULE_FAILED + container.getHeader().getId());
        }
    }

    /**
     * Signal the ThreadManager to terminate all running instances of a DeviceContainer
     * @return true when a thread was terminated and false when no active thread with the provided ID was found
     */
    public boolean removeDevice(long containerId){
        try {
            threadManager.terminate(containerId);
            return true;
        }catch (Exception exception){
            logger.logError(SCHEDULER_REMOVE_DEVICE);
            return false;
        }
    }

    /**
     * Signal ThreadManager to terminate all running workers
     * Empty the DelayQueue
     */
    public void removeAllDevices(){
        rescheduleQueue.clear();
        threadManager.terminateAll();
    }

    /**
     * Method checks the DelayQueue for Devices that need to be scheduled
     * Invokes the threads of taken DeviceContainers
     */
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()){
            try {
                reschedule(rescheduleQueue.take().id);
            } catch (InterruptedException e) {
            }
        }
    }

    public Thread getWorker() {
        return worker;
    }

    /**
     * ContainerClass used by the DelayQueue
     * Implements the Delayed interface and contains the ID of the container it represents
     */
    private class DelayQueueContainer implements Delayed{
        private long time;
        private long id;

        /**
         * Constructor to build a DelayQueueContainer from a provided DeviceContainer
         * @param deviceContainer
         */
        public DelayQueueContainer(DeviceContainer deviceContainer){
            this.time = System.currentTimeMillis() + Long.parseLong(deviceContainer.getHeader().getCycleTime());
            this.id = deviceContainer.getHeader().getId();
        }

        @Override
        public long getDelay(TimeUnit timeUnit) {
            long diff = time - System.currentTimeMillis();
            return timeUnit.convert(diff, TimeUnit.MILLISECONDS);
        }

        @Override
        public int compareTo(Delayed delayed) {
            if (this.time < ((DelayQueueContainer)delayed).time) {
                return -1;
            }
            if (this.time > ((DelayQueueContainer)delayed).time) {
                return 1;
            }
            return 0;
        }
    }
}
