package de.htw.saar.env.sim.Mocks;

import de.htw.saar.env.sim.device.container.MessageContainer;
import de.htw.saar.env.sim.mqtt.ProxyMQTTClient;

public class ProxyMQTTClientMock extends ProxyMQTTClient {

    @Override
    public boolean subscribe(String topic) { return true; }

    @Override
    public boolean unsubscribe(String topic) { return true; }

    @Override
    public MessageContainer takeInput() {
        return new MessageContainer("TestTopic", "TestPayload");
    }
}
