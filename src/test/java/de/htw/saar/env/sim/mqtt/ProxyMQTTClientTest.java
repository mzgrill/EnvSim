package de.htw.saar.env.sim.mqtt;

import de.htw.saar.env.sim.device.container.MessageContainer;
import de.htw.saar.env.sim.io.IOManager;
import de.htw.saar.env.sim.io.SystemLogger;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.junit.jupiter.api.Test;
import testUtil.testUtils;
import static de.htw.saar.env.sim.io.IOManager.*;
import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.assertEquals;


class ProxyMQTTClientTest {

    ProxyMQTTClient proxy;

    @Test
    /**
     * Tests the connection to a broker using the credentials provided in the config.properties file in PATH
     * May fail because the MQTT-Broker is unavailable.
     */
    void run() throws MqttException, InterruptedException {
        testUtils.initializeIOManager();
        proxy = new ProxyMQTTClient();
        proxy.logger = new SystemLogger();
        proxy.initialiseMQTT();
        proxy.subscribe("data/Gruppe11/MQTTExamples");
        producer();
        producer();
        try {
            sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            proxy.disconnectMQTT();
        } catch (Exception exception) {

        }
        assertEquals(Integer.valueOf(IOManager.getInstance().properties.getProperty(MQTT_INPUT_BUFFER_SIZE))-2,(proxy.getInputQueueRemaining()));
    }

    void producer() {
        proxy.publish(new MessageContainer("data/Gruppe11/MQTTExamples", String.valueOf(System.currentTimeMillis())));
    }

}