package de.htw.saar.env.sim.device.container;

/**
 * Container class for topic and payload pairs
 */
public class MessageContainer{

    private String topic;
    private String payload;

    public MessageContainer(MessageContainer container){
        this.payload = new String(container.payload);
        this.topic = new String(container.topic);
    }


    public MessageContainer(String topic, String payload){
        this.topic = topic;
        this.payload = payload;
    }

    public String getPayload() {
        return payload;
    }

    public String getTopic() {
        return topic;
    }
}