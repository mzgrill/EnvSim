package de.htw.saar.env.sim.device.management;

import de.htw.saar.env.sim.device.container.DeviceHeader;
import de.htw.saar.env.sim.device.scheduling.ThreadManager;
import de.htw.saar.env.sim.io.SystemLogger;
import de.htw.saar.env.sim.mqtt.ProxyMQTTClient;
import de.htw.saar.env.sim.util.RESTContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static de.htw.saar.env.sim.io.LogStrings.*;

/**
 * StatusCollector class used to collect and return the current values of existing devices.
 * statusMap containing these values is cleared every REFRESH_RATE milliseconds to reduce overhead.
 *
 * Clients connecting with a deprecated timestamp must fetch the full register before rejoining the
 * regular update cycle, in which only latest changes are transmitted.
 */
@Service
public class StatusCollector {


    private static final int REFRESH_RATE = 60000;
    private static final int BENCHMARK_RATE = 60000;

    @Autowired
    DeviceManager deviceManager;

    @Autowired
    ProxyMQTTClient client;

    @Autowired
    SystemLogger logger;

    @Autowired
    ThreadManager manager;

    private ConcurrentHashMap<Long, DeviceHeader> statusMap;
    private Long latestInterval;

    public StatusCollector(){
        latestInterval = new Long(0);
        statusMap = new ConcurrentHashMap<>();
    }

    /**
     * Method to collect the status of every device listed in the register.
     * @return List containing the status of all current devices
     */
    public List<DeviceHeader> getAllDevices(){
        ArrayList<DeviceHeader> headers = new ArrayList();
        deviceManager.getAllDevices().forEach(deviceContainer -> headers.add(deviceContainer.getHeader()));
        return headers;
    }

    /**
     * Writeback method called by terminating DeviceContainers to submit their changes
     * @param header The DeviceHeader object of the calling container
     */
    public void writeback(DeviceHeader header){
        statusMap.put(header.getId(), header);
    }

    /**
     * Method called by the client to request the state of currently listed devices.
     * Depending on the Client's lastPullTime returns either a list with latest changes or invokes getAllDevices()
     *
     * The access is synchronized so no illegal states can occur, when requesting the resource while it is being updated
     *
     * @param lastPullTime
     * @return
     */
    public RESTContainer getDeviceChanges(Long lastPullTime){
        synchronized (latestInterval) {
            if (lastPullTime < latestInterval) {
                return new RESTContainer(getAllDevices(), true);
            } else {
                ArrayList<DeviceHeader> headers = new ArrayList();
                headers.addAll(statusMap.values());
                return new RESTContainer(headers, false);
            }
        }
    }


    /**
     * CronJob resetting the statusMap and setting a new latestInterval timestamp
     */
    @Async
    @Scheduled(fixedRate = REFRESH_RATE)
    public void resetChanges(){
        synchronized (latestInterval) {
            statusMap.clear();
            latestInterval = System.currentTimeMillis();
        }
    }

    /**
     * CronJob logging benchmark information like buffer sizes and the throughput of messages
     */
    @Async
    @Scheduled(fixedRate = BENCHMARK_RATE)
    public void calculateThroughput(){
        logger.logInfo(STATUS_COLLECTOR_MPS + (client.getMessagesSent()/60));
        logger.logInfo(STATUS_COLLECTOR_OUTPUT + client.getOutputQueueSize() + '/' +
                (client.getOutputQueueSize()+client.getOutputQueueRemaining()));
        logger.logInfo(STATUS_COLLECTOR_INPUT + client.getInputQueueSize() + '/' +
                (client.getInputQueueSize()+client.getInputQueueRemaining()));
        logger.logInfo(STATUS_COLLECTOR_REGISTER + deviceManager.getRegister().getRegister().size());
        logger.logInfo(STATUS_COLLECTOR_THREAD + manager.getThreadMapSize());
    }
}
