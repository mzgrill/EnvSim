package de.htw.saar.env.sim.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static de.htw.saar.env.sim.io.LogStrings.PATH;

/**
 * IOManager providing paths, properties and InputStreams for all other Components using IO elements
 */
public class IOManager{

    private static IOManager manager;

    //Property names as constants
    public static final String MQTT_INPUT_BUFFER_SIZE = "MQTT.INPUT.BUFFER.SIZE";
    public static final String MQTT_OUTPUT_BUFFER_SIZE = "MQTT.OUTPUT.BUFFER.SIZE";
    public static final String MQTT_QOS = "MQTT.QOS";
    public static final String MQTT_BROKER = "MQTT.BROKER";
    public static final String MQTT_USERNAME = "MQTT.USERNAME";
    public static final String MQTT_PASSWORD = "MQTT.PASSWORD";
    public static final String MQTT_CLIENT_ID = "MQTT.CLIENT.ID";
    public static final String DISTRIBUTER_BUFFER_SIZE = "DISTRIBUTER.BUFFER.SIZE";
    public static final String AUTHENTICATOR_PROTOCOL = "AUTHENTICATOR.PROTOCOL";
    public static final String AUTHENTICATOR_CERTIFICATE_NAME = "AUTHENTICATOR.CERTIFICATE";
    public static final String AUTHENTICATOR_CERTIFICATE_TYPE = "AUTHENTICATOR.CERTIFICATE.TYPE";
    public static final String CLI_INPUT_PIPE = "CLI.INPUT";
    public static final String CLI_OUTPUT_PIPE = "CLI.OUTPUT";
    public static final String CLI_BENCHMARK_PIPE = "CLI.BENCHMARK";

    //Configuration
    public String path;
    public Properties properties;
    private final String PROPERTY_FILE = "config.properties";

    public IOManager(){
        this.properties  = new Properties();
    }


    public static IOManager getInstance(){
        if(manager == null){
            manager = new IOManager();
        }
        return manager;
    }


    /**
     * Loads configuration from provided property file at path location
     */
    public void loadConfig() throws IOException {
        File file = new File(path + PROPERTY_FILE);
        InputStream inputStream = new FileInputStream(file);
        //InputStream inputStream = new FileInputStream(new File("EnvSimResources//config.properties"));

        if (inputStream != null) {
            properties.load(inputStream);
        } else {
            throw new IOException();
        }
    }

    /**
     * Initializes the IOManager with the path to the resource folder
     */
    public boolean initialize(String[] args){
        try {
            path = "EnvSimResources//";//PATH;
            loadConfig();
        }catch (Exception e ){
            return false;
        }
        return true;
    }

}
