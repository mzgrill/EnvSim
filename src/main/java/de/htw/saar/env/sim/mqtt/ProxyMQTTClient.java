package de.htw.saar.env.sim.mqtt;

import de.htw.saar.env.sim.device.container.MessageContainer;
import de.htw.saar.env.sim.io.IOManager;
import de.htw.saar.env.sim.io.SystemLogger;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static de.htw.saar.env.sim.io.LogStrings.*;
import static de.htw.saar.env.sim.io.IOManager.*;

/**
 * Proxy MQTT client to encapsulate the actual Client
 * Offers methods to initialize and disconnect the client
 * Buffers in- and outgoing communication using thread-safe collections
 */
@Service
@DependsOn("SystemLogger")
@Order(Ordered.LOWEST_PRECEDENCE-1)
public class ProxyMQTTClient implements Runnable, MqttCallback {

    @Autowired
    SystemLogger logger;

    private String clientId;
    private String username;
    private String password;
    private int qos;
    private String broker;

    private IOManager ioManager;
    private Thread consumer;
    private Authenticator authenticator;
    private MqttClient client;

    private BlockingQueue<MessageContainer> outputQueue;
    private BlockingQueue<MessageContainer> inputQueue;

    private int messagesSent;
    //Loopback mode for messages (offline mode)
    private boolean loopback;


    public ProxyMQTTClient(){
        ioManager = IOManager.getInstance();
        outputQueue = new ArrayBlockingQueue<MessageContainer>(
                Integer.valueOf(ioManager.properties.getProperty(MQTT_OUTPUT_BUFFER_SIZE)));
        inputQueue = new ArrayBlockingQueue<MessageContainer>(
                Integer.valueOf(ioManager.properties.getProperty(MQTT_INPUT_BUFFER_SIZE)));
        qos = Integer.valueOf(ioManager.properties.getProperty(MQTT_QOS));
        broker = ioManager.properties.getProperty(MQTT_BROKER);
        clientId = ioManager.properties.getProperty(MQTT_CLIENT_ID);
        username = ioManager.properties.getProperty(MQTT_USERNAME);
        password = ioManager.properties.getProperty(MQTT_PASSWORD);
        authenticator = new Authenticator();
        consumer  = new Thread(this);
        messagesSent = 0;
    }

    /**
     * Method to configure a MQTT client and connect
     * @throws MqttException
     */
    @PostConstruct
    public void initialiseMQTT(){
        try {
            client = new MqttClient(broker, clientId, new MemoryPersistence());
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(false);
            connOpts.setUserName(username);
            connOpts.setPassword(password.toCharArray());
            connOpts.setSocketFactory(authenticator.getSSLSocketFactory());
            client.setCallback(this);
            client.connect(connOpts);
            logger.logInfo(CONNECTION_ESTABLISHED);
        } catch (MqttException ex){
            loopback = true;
            logger.logError(CONNECTION_FAILURE);
        } catch (Exception exception){
            loopback = true;
            logger.logError(CONNECTION_FAILURE);
        }
        if (!consumer.isAlive()) {
            consumer.start();
        }
    }

    /**
     * Closes the clients connection to the Broker
     */
    public void disconnectMQTT() throws MqttException {
            client.disconnect();
            logger.logInfo(CONNECTION_CLOSED);
    }

    /**
     * Publishes a message using an MQTT client
     * @param topic Topic the message will be published on
     * @param payload Payload of the message to be send
     * @return true on success / false on failure
     */
    private boolean cPublish(String topic, String payload){
        try{
        MqttMessage message = new MqttMessage(payload.getBytes());
        message.setQos(qos);
            synchronized (client) {
                client.publish(topic, message);
                messagesSent++;
            }
        } catch (Exception ex){
            return false;
        }
        return true;
    }

    /**
     * Adds a Message to the Output Queue
     * @param messageContainer Container class holding topic and payload
     */
    public void publish(MessageContainer messageContainer){
        try {
            outputQueue.put(messageContainer);
        } catch (InterruptedException ex){
        }
    }

    /**
     * Adds a subscription to the given topic for the MQTT client
     * @param topic Topic the client will subscribe to
     * @return true on success / false on failure
     */
    public boolean subscribe(String topic){
        try {
            synchronized (client) {
                client.subscribe(topic);
            }
        } catch (MqttException ex) {
            return false;
        }
        return true;
    }

    /**
     * Removes a subscription to the given topic for the MQTT client
     * @param topic Topic the client will unsubscribe to
     * @return true on success / false on failure
     */
    public boolean unsubscribe(String topic){
        try {
            synchronized (client) {
                client.unsubscribe(topic);
            }
        } catch (MqttException ex) {
            return false;
        }
        return true;
    }

    public Thread getConsumer() {
        return consumer;
    }

    /**
     * Returns the next value from the inputQueue
     * Blocks if queue is empty
     * @return MessageContainer from the input queue
     */
    public MessageContainer takeInput() {
        try {
            return inputQueue.take();
        } catch (InterruptedException e) {
        }
        return null;
    }

    /**
     * Consumer of outputQueue, unsuccessful publish attempts result in
     * re queuing of the message
     */
    @Override
    public void run() {
        while(!Thread.currentThread().isInterrupted()) {
            try {
                MessageContainer current = null;
                try {
                    current = outputQueue.take();
                    if (loopback){
                        inputQueue.put(current);
                    } else if ((!cPublish(current.getTopic(), current.getPayload()))) {
                        outputQueue.put(current);
                    }
                } catch (InterruptedException ex) {
                }
            } catch (Exception e) {
            logger.logError(MQTT_CONSUMER_ERROR);
            }
        }
    }

    /**
     * Behaviour on connection loss
     * @param throwable
     */
    @Override
    public void connectionLost(Throwable throwable) {
        logger.logError(CONNECTION_LOST);
        while(!Thread.currentThread().isInterrupted() && !client.isConnected()){
            logger.logInfo(CONNECTION_RECONNECT);
            initialiseMQTT();
        }
    }

    /**
     * On message arrival the key value pair of:
     * @param topic Topic of the incoming message
     * @param message Message received
     * is stored in a HashMap for synchronized access
     * @throws Exception
     */
    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        try {
            inputQueue.put(new MessageContainer(topic, new String(message.getPayload())));
        } catch (InterruptedException e) {
        }
    }

    /**
     * Called upon successful delivery of a published message
     * Not used in this version, could be used to ensure a higher quality of service
     * @param iMqttDeliveryToken
     */
    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
    }


    /**
     * Benchmark values
     */
    public int getMessagesSent() {
        int tmp=messagesSent;
        messagesSent = 0;
        return tmp;
    }
    public int getOutputQueueSize(){return outputQueue.size();}
    public int getInputQueueSize(){return inputQueue.size();}
    public int getOutputQueueRemaining(){return outputQueue.remainingCapacity();}
    public int getInputQueueRemaining(){return inputQueue.remainingCapacity();}
}
