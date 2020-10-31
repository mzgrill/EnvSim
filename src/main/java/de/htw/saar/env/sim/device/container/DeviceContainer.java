package de.htw.saar.env.sim.device.container;

import de.htw.saar.env.sim.device.management.StatusCollector;
import de.htw.saar.env.sim.device.scheduling.Scheduler;
import de.htw.saar.env.sim.io.SystemLogger;
import de.htw.saar.env.sim.mqtt.ProxyMQTTClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

import static de.htw.saar.env.sim.io.LogStrings.DEVICE_CHANGED_BEHAVIOUR;
import static de.htw.saar.env.sim.io.LogStrings.DEVICE_CONTAINER_FAILED;
import static java.lang.Thread.sleep;

/**
 * DeviceContainer holding a device class object,
 * an auto-generated header object containing meta-information
 * and methods to call and update devices
 */
public class DeviceContainer implements Runnable{

    Scheduler scheduler;
    StatusCollector collector;
    ProxyMQTTClient mqttProxy;
    SystemLogger logger;

    private DeviceHeader header;
    private Device device;

    private boolean receive;
    private String message;
    private String topic;
    /**
     * Inherited from runnable to run DeviceContainers in
     * new Threads, this.receive and this.message fields provide
     * options to pass incoming massages to the device
     * and switch between send and receive mode
     */
    public void run(){
        synchronized (this) {
            updateHeader(DeviceHeader.ContainerStatus.running);
            writeHeader();
            try {
                if (receive) {
                    receive(message);
                } else {
                    send();
                }
            } catch (Exception exception) {
                logger.logError(DEVICE_CONTAINER_FAILED + header.getId());
                exception.printStackTrace();
                updateHeader(DeviceHeader.ContainerStatus.failed);
            }
            message = null;
            receive = false;
            writeHeader();
            reschedule();
        }
    };

    /**
     * Reacts to an incoming message from a subscribed topic of the device
     * @param message Payload of the received message
     */
    public void receive(String message){
            publish(device.receive(message));
            updateHeader(DeviceHeader.ContainerStatus.idle);
    }

    /**
     * Called by auto-schedule, device will publish
     * a new value according to current behaviour
     */
    public void send(){
        synchronized (this) {
            if (this.device.getBehaviour().getCycleTime() != 0) {
                publish(device.send());
                updateHeader(DeviceHeader.ContainerStatus.idle);
            } else {
                logger.logInfo(DEVICE_CHANGED_BEHAVIOUR + this.header.getId());
                updateHeader(DeviceHeader.ContainerStatus.failed);
            }
        }
    }

    /**
     * Reschedules the device after previous call using
     * the provided cycleTime of the device's behaviour
     *
     * CycleTime = 0 -> results in no further scheduling
     */
    public void reschedule(){
        scheduler.writeback(this);
    };

    /**
     * Passes the header object to the StatusCollector
     */
    public void writeHeader(){
        collector.writeback(this.header);
    };

    public void updateHeader(DeviceHeader.ContainerStatus status){
        header.setCurrentValue(device.getCurrentValue());
        header.setStatus(status);
    }

    /**
     * Publishes a String message to the MQTTProxy
     * @param message Message to be published to the MQTT Service
     */
    public void publish(MessageContainer message){
        if (message != null){
            mqttProxy.publish(message);
        }
    }

    public DeviceHeader getHeader() {
        return header;
    }

    public void setHeader(DeviceHeader header) {
        this.header = header;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public boolean isReceive() {
        return receive;
    }

    public void setReceive(boolean receive) {
        this.receive = receive;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setContext(ProxyMQTTClient client, Scheduler scheduler, StatusCollector collector, SystemLogger logger){
        this.scheduler = scheduler;
        this.mqttProxy = client;
        this.collector = collector;
        this.logger = logger;
    }
}
