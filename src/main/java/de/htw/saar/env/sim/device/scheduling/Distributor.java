package de.htw.saar.env.sim.device.scheduling;

import de.htw.saar.env.sim.device.container.DeviceHeader;
import de.htw.saar.env.sim.device.container.MessageContainer;
import de.htw.saar.env.sim.io.IOManager;
import de.htw.saar.env.sim.io.SystemLogger;
import de.htw.saar.env.sim.mqtt.ProxyMQTTClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.concurrent.*;
import static de.htw.saar.env.sim.io.LogStrings.*;

/**
 * Distributor class, offering methods to modify subscriptions of an MQTT-Proxy object
 *
 * Runnable thread acts as an consumer to incoming messages;
 * Messages are distributed to all devices owning a subscription on the message's topic
 */
@Service
public class Distributor implements Runnable{

    @Autowired
    ProxyMQTTClient proxy;

    @Autowired
    Scheduler scheduler;

    @Autowired
    SystemLogger logger;

    @Autowired
    ThreadManager threadManager;

    private final int LAST_ELEMENT_REMAINING = 1;
    private int bufferSize;


    ConcurrentHashMap<String, ArrayList<Long>> topicMap;
    private Thread worker;
    private MessageBuffer buffer;


    public Distributor(){
        bufferSize = Integer.valueOf(IOManager.getInstance().properties.getProperty(IOManager.DISTRIBUTER_BUFFER_SIZE));
        topicMap = new ConcurrentHashMap<>();
        buffer = new MessageBuffer();
    }

    @PostConstruct
    public void initialize(){
        new Thread(this).start();
    }

    /**
     * Calls the Scheduler with the list of devices subscribed to the topic of the incoming message
     * Blocked devices are returned as a list and then buffered in the messageBuffer
     * @param messageContainer Incoming message containing topic and payload
     */
    private void scheduleDevices(MessageContainer messageContainer){
        if (topicMap.containsKey(messageContainer.getTopic())) {
            ArrayList<Long> devices = topicMap.get(messageContainer.getTopic());
            ArrayList<Long> toBuffer = new ArrayList<>();
            ArrayList<Long> toSchedule = new ArrayList<>();
            if (devices != null) {
                devices.forEach(l -> {
                    if (buffer.buffer.get(l) != null) {
                        toBuffer.add(l);
                    } else {
                        toSchedule.add(l);
                    }
                });
                buffer.enqueue(toBuffer, messageContainer);
                buffer.enqueue(new ArrayList<Long>(scheduler.schedule(toSchedule, messageContainer)), messageContainer);
            }
        }
    }

    /**
     * Override of run method to launch the Distributor in a new thread
     */
    @Override
    public void run() {
        while(!Thread.currentThread().isInterrupted()) {
            try {
                scheduleDevices(new MessageContainer(proxy.takeInput()));
            } catch (Exception e) {
               logger.logError(DISTRIBUTER_RUN_FAIL);
               e.printStackTrace();
            }
        }
    }

    /**
     * Adds a device's subscription to the topicMap
     * If the map doesn't contain the topic yet, it is added and
     * the MQTT-Proxy will subscribe to it
     * @param deviceHeader Header the device containing containerID and subscribe-topic list
     */
    public boolean addSubscription(DeviceHeader deviceHeader){
        try {
            ArrayList<String> topics = (ArrayList<String>) deviceHeader.getSubscribeList();
            if (!topics.isEmpty()) {
                topics.forEach(topic -> {
                    ArrayList<Long> tmp;
                    proxy.subscribe(topic);
                    if (topicMap.containsKey(topic)) {
                        tmp = topicMap.get(topic);
                    } else {
                        tmp = new ArrayList<>();
                    }
                    synchronized (tmp) {
                        tmp.add(deviceHeader.getId());
                        topicMap.put(topic, tmp);
                    }
                });
            }
        }catch (Exception exception){
            logger.logError(DISTRIBUTER_ADD_SUB);
            return false;
        }
        return true;
    }

    /**
     * Removes a device's subscription from the topicMap
     * If the device is the only subscriber the topic is deleted from the map
     * and the MQTT-Proxy unsubscribes from it
     ** The related buffer bucket will be cleared and removed
     * @param deviceHeader Header the device containing containerID and subscribe-topic list
     */
    public boolean removeSubscription(DeviceHeader deviceHeader){
        try {
            ArrayList<String> topics = (ArrayList<String>) deviceHeader.getSubscribeList();
            if (!topics.isEmpty()) {
                topics.forEach(topic -> {
                    ArrayList<Long> tmp;
                    tmp = topicMap.get(topic);
                    if (tmp.size() <= LAST_ELEMENT_REMAINING) {
                        topicMap.remove(topic);
                        proxy.unsubscribe(topic);
                    } else {
                        tmp.remove(deviceHeader.getId());
                        topicMap.put(topic, tmp);
                    }
                    if (buffer.buffer.containsKey(deviceHeader.getId())) {
                        buffer.buffer.remove(deviceHeader.getId());
                    }
                });
            }
            } catch(Exception exception){
                logger.logError(DISTRIBUTER_REM_SUB);
                exception.printStackTrace();
                return false;
            }
        return true;
    }

    /**
     * Removes all subscriptions
     */
    public boolean removeAll(){
        try {
            topicMap.clear();
            buffer.buffer.clear();
        } catch (Exception exception){
            logger.logError(DISTRIBUTER_REM_ALL);
            return false;
        }
        return true;
    }

    public Thread getWorker() {
        return worker;
    }

    /**
     * Buffer class used to store messages
     */
    public class MessageBuffer implements Runnable {

        ConcurrentHashMap<Long, BlockingQueue<MessageContainer> > buffer;
        Thread worker;

        public MessageBuffer(){
            buffer = new ConcurrentHashMap<>();
            worker = new Thread(this);
            worker.start();
        }

        /**
         * Adds devices and the corresponding messages to the buffer
         * @param ids List of Device ID's
         * @param messageContainer Received message that could not be distributed
         */
        public void enqueue(ArrayList<Long> ids, MessageContainer messageContainer){
            try {
                for (Long id : ids) {
                    BlockingQueue<MessageContainer> tmp;
                    if (buffer.containsKey(id)) {
                        tmp = buffer.get(id);
                        try {
                            tmp.put(messageContainer);
                        } catch (InterruptedException e) {
                        }
                    } else {
                        tmp = new ArrayBlockingQueue<>(bufferSize);
                        try {
                            tmp.put(messageContainer);
                        } catch (InterruptedException e) {
                            logger.logInfo(DISTRIBUTER_BUFFER_OVERFLOW + id);
                            tmp.take();
                        }
                    }
                    buffer.put(id, tmp);
                }
            } catch (Exception e){
                logger.logError(DISTRIBUTER_ADD_TO_BUFFER);
            }
        }

        /**
         * Checks devices in the buffer for a free slot in the ThreadManager
         * and schedules them if available
         */
        public void dequeue(){
            try {
                buffer.forEach((id, messageContainers) -> {
                    if (!threadManager.contains(id)) {
                        try {
                            scheduler.scheduleSingle(id, messageContainers.take());
                            if (buffer.get(id).isEmpty()) {
                                buffer.remove(id);
                            }
                        } catch (InterruptedException e) {
                        }
                    }
                });
            } catch (Exception exception){
                logger.logError(DISTRIBUTER_REM_FROM_BUFFER);
            }
        }


        @Override
        public void run() {
            while(!Thread.currentThread().isInterrupted()){
                dequeue();
            }
        }
    }
}
