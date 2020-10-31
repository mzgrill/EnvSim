package de.htw.saar.env.sim.io;

public class LogStrings {

    public static final String PATH = "/tmp/EnvSimResources/";

    // Logger Constants

    //MQTT Proxy
    public static final String CONNECTION_ESTABLISHED = "MQTTClient: Connection to Broker established";
    public static final String CONNECTION_LOST = "MQTTClient: Connection to Broker lost";
    public static final String CONNECTION_RECONNECT = "MQTTClient: Attempting to reconnect";
    public static final String CONNECTION_CLOSED = "MQTTClient: Closed connection to Broker";
    public static final String CONNECTION_FAILURE = "MQTTClient: Could not establish connection to Broker";
    public static final String MQTT_CONSUMER_ERROR = "MQTTClient Consumer: An error occurred while publishing messages from the OutputQueue";
    public static final String MQTT_FULL_INPUT_BUFFER = "MQTTClient: InputQueue is full! Dropping oldest message.";


    //ClassLoader/Factory
    public static final String CLASS_INSTANTIATION_ERROR = "ClassLoader: Can't create instance of class ";
    public static final String CLASS_DEFINITION_ERROR = "ClassLoader: Error defining class ";


    //Manager
    public static final String MANAGER_UNREGISTER_ERROR = "DeviceManager: Unregister Device does not exist";
    public static final String MANAGER_UNREGISTER_ERROR_SUBS = "DeviceManager: Unregister Device failed to unsubscribe";
    public static final String MANAGER_UNREGISTER_ERROR_SCHEDULER = "DeviceManager: Unregister Device failed to unschedule";
    public static final String MANAGER_CHANGE_ERROR = "DeviceManager: Change Device does not exist";
    public static final String MANAGER_CHANGE_FACTORY_ERROR = "DeviceManager: Build new Behaviour failed";
    public static final String MANAGER_SUCCESSFUL_REGISTER = "DeviceManager: Successfully added device ";
    public static final String MANAGER_SUCCESSFUL_UNREGISTER = "DeviceManager: Successfully removed device ";
    public static final String MANAGER_SUCCESSFUL_CHANGE = "DeviceManager: Successfully changed behaviour of device ";
    public static final String MANAGER_SUCCESSFUL_CLEAN = "DeviceManager: Successfully removed all devices ";
    public static final String MANAGER_CLEAN_ERROR = "DeviceManager: Removal of all devices failed";

    //Distributor
    public static final String DISTRIBUTER_ADD_SUB = "Distributor: Failed to add subscription";
    public static final String DISTRIBUTER_REM_SUB = "Distributor: Failed to remove subscription";
    public static final String DISTRIBUTER_REM_ALL = "Distributor: Failed to remove subscription";
    public static final String DISTRIBUTER_RUN_FAIL = "Distributor: Thread encountered an error; Messages might have been lost";
    public static final String DISTRIBUTER_ADD_TO_BUFFER = "Distributor: Failed to add message to buffer";
    public static final String DISTRIBUTER_BUFFER_OVERFLOW = "Distributor: Input buffer overflow, dropping oldest message for device ";
    public static final String DISTRIBUTER_REM_FROM_BUFFER = "Distributor: Failed to remove message from buffer";


    //Scheduler
    public static final String SCHEDULER_SCHEDULE_MESSAGE = "Scheduler: Failed to schedule device with message";
    public static final String SCHEDULER_RESCHEDULE_FAILED = "Scheduler: Device may have been updated or deleted. Failed to reschedule device ";
    public static final String SCHEDULER_REMOVE_DEVICE = "Scheduler: Failed to remove device";


    //ThreadManager
    public static final String THREAD_MANAGER_ADD = "ThreadManager: Failed to remove worker ";
    public static final String THREAD_MANAGER_REMOVE = "ThreadManager: Failed to remove worker ";
    public static final String THREAD_MANAGER_TERMINATE = "ThreadManager: Failed to terminate worker ";
    public static final String THREAD_MANAGER_REMOVE_ALL = "ThreadManager: Failed to remove all workers";

    //StatusCollector
    public static final String STATUS_COLLECTOR_MPS = "Throughput (Messages per second): ";
    public static final String STATUS_COLLECTOR_OUTPUT = "OutputQueue status: ";
    public static final String STATUS_COLLECTOR_INPUT = "InputQueue status: ";
    public static final String STATUS_COLLECTOR_REGISTER = "Currently registered devices: ";
    public static final String STATUS_COLLECTOR_THREAD = "Currently running devices: ";

    //DeviceContainer
    public static final String DEVICE_CHANGED_BEHAVIOUR = "DeviceContainer: Device behaviour change detected, 'Failed' state will be written for device ";
    public static final String DEVICE_CONTAINER_FAILED = "DeviceContainer: Container execution failed, 'Failed' state will be written for device  ";


    //CLIHandler
    public static final String CLI_COMMAND_ERROR = "CLIHandler: Error processing command, refer to 'he' for a list of commands";
    public static final String CLI_PIPE_ERROR = "CLIHandler: Error, Pipe could not be found";

        //Output
        public static final String CLI_HELP = "List of commands: \n" +
                "ps: List all devices \n" +
                "rn: Run new device [rn DEVICE_NAME BEHAVIOUR_NAME COUNT] \n" +
                "rm: Remove device [rm DEVICE_ID] \n" +
                "ra: Remove all devices \n" +
                "cb: Change behaviour of device [BEHAVIOUR_NAME DEVICE_ID] \n" +
                "he: Show a list of all commands \n";

        //Commands
        public static final String CLI_COMMAND_PS = "ps";
        public static final String CLI_COMMAND_RUN = "rn";
        public static final String CLI_COMMAND_RM = "rm";
        public static final String CLI_COMMAND_RA = "ra";
        public static final String CLI_COMMAND_HE = "he";
        public static final String CLI_COMMAND_CB = "cb";
}
