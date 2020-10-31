package de.htw.saar.env.sim.device.management;

import de.htw.saar.env.sim.device.container.DeviceBlueprint;
import de.htw.saar.env.sim.device.container.DeviceContainer;
import de.htw.saar.env.sim.device.container.DeviceHeader;
import de.htw.saar.env.sim.device.scheduling.Distributor;
import de.htw.saar.env.sim.device.scheduling.Scheduler;
import de.htw.saar.env.sim.io.SystemLogger;
import de.htw.saar.env.sim.mqtt.ProxyMQTTClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import static de.htw.saar.env.sim.io.LogStrings.*;

/**
 * DeviceManager invoking any operations regarding the Device Register, scheduling and topic-management
 * Owns the DeviceRegister as well as the factory used to produce DeviceContainers
 */
@Service
public class DeviceManager {

    @Autowired
    Distributor distributor;

    @Autowired
    Scheduler scheduler;

    @Autowired
    SystemLogger logger;

    @Autowired
    StatusCollector collector;

    @Autowired
    ProxyMQTTClient client;


    private Register register;
    private DeviceContainerFactory factory;

    public DeviceManager(){
        register = new Register();
        factory = new DeviceContainerFactory();
    }

    /**
     * Method used to register new Devices
     * Devices are packaged in runnable DeviceContainers then added to the Register and finally scheduled
     * @param deviceBlueprint Blueprint containing information used in the factory
     */
    public void register(DeviceBlueprint deviceBlueprint){
        try {
            synchronized (factory) {
                factory.build(deviceBlueprint).forEach(container -> {
                    initializeDeviceContainer(container); });
            }
        } catch (IllegalAccessException e) {
            logger.logError(CLASS_INSTANTIATION_ERROR + deviceBlueprint.getDevice());
        } catch (InstantiationException e) {
            logger.logError(CLASS_INSTANTIATION_ERROR + deviceBlueprint.getDevice());
        } catch (ClassNotFoundException e) {
            logger.logError(CLASS_DEFINITION_ERROR + deviceBlueprint.getDevice());
            e.getException().printStackTrace();
        } catch (Exception e){
            logger.logError(CLASS_DEFINITION_ERROR + deviceBlueprint.getDevice());
            e.printStackTrace();
        }
    }

    /**
     * Publishes the device to other components
     * Injects the relevant Components from SpringContext
     * @param container
     */
    private void initializeDeviceContainer(DeviceContainer container){
        container.setContext(client,scheduler,collector,logger);
        register.put(container);
        distributor.addSubscription(container.getHeader());
        if (container.getDevice().getBehaviour().getCycleTime() != 0) {
            scheduler.reschedule(container.getHeader().getId());
        }
        logger.logInfo(MANAGER_SUCCESSFUL_REGISTER + container.getHeader().getId());
    }

    /**
     * Method to unregister a device
     * Device's subscriptions are cancelled, the container is removed from the Register and currently running threads
     * are terminated.
     * Finally status deleted is set.
     * @param containerId
     */
    public void unregister(long containerId){

        try {
            DeviceHeader tmp = register.getContainer(containerId).getHeader();
            if(!distributor.removeSubscription(tmp) == true) {
                logger.logError(MANAGER_UNREGISTER_ERROR_SUBS);
                throw new Exception();
            }
            register.remove(containerId);
            if(!scheduler.removeDevice(containerId)){
                logger.logError(MANAGER_UNREGISTER_ERROR_SCHEDULER);
                throw new Exception();
            }
            register.remove(containerId);
            tmp.setStatus(DeviceHeader.ContainerStatus.deleted);
            collector.writeback(tmp);
            logger.logInfo(MANAGER_SUCCESSFUL_UNREGISTER + containerId);

        } catch (Exception exception){
            logger.logError(MANAGER_UNREGISTER_ERROR);
        }
    }

    /**
     * Changes the behaviour of a specified device by modifying the device's register value, terminating any running
     * instances and rescheduling the device if not already scheduled
     */
    public void changeBehaviour(String behaviourId, long containerId){
        try {
            DeviceContainer tmp = register.getContainer(containerId);
            unregister(containerId);
            synchronized (tmp) {
                tmp.getDevice().setBehaviour(factory.buildBehaviour(behaviourId));
                tmp.getHeader().setBehaviour(behaviourId);
                tmp.getHeader().setCycleTime(String.valueOf(tmp.getDevice().getBehaviour().getCycleTime()));
            }
            initializeDeviceContainer(tmp);
        }catch (NullPointerException exception){
            logger.logError(MANAGER_CHANGE_ERROR);
        }catch (IllegalAccessException | InstantiationException | ClassNotFoundException exception){
            logger.logError(MANAGER_CHANGE_FACTORY_ERROR);
        }
        logger.logInfo(MANAGER_SUCCESSFUL_CHANGE + containerId);
    }

    /**
     * Removes all devices from the register, including their running threads and planned schedules
     */
    public void removeAll(){
        try {
        ArrayList<DeviceContainer> tmp = new ArrayList<>(getAllDevices());
        distributor.removeAll();
        scheduler.removeAllDevices();
        register.clear();
        tmp.forEach(h -> {
            h.getHeader().setStatus(DeviceHeader.ContainerStatus.deleted);
            collector.writeback(h.getHeader());
        });
        } catch (Exception exception){
            logger.logError(MANAGER_CLEAN_ERROR);
        }
        logger.logInfo(MANAGER_SUCCESSFUL_CLEAN);
    }

    public List<DeviceContainer> getAllDevices(){
        return register.getAllContainers();
    }

    public Register getRegister(){
        return register;
    }

    /**
     * Register class used to manage and hold a ConcurrentHashMap mapping ContainerIds to their matching DeviceContainer
     */
    public class Register{

        private ConcurrentHashMap<Long,DeviceContainer> register;

        private Register() {
            register = new ConcurrentHashMap<>();
        }

        public ConcurrentHashMap<Long, DeviceContainer> getRegister() {
            return register;
        }

        /**
         * Adds a DeviceContainer to the Map using it's id as key and itself as value
         * @param deviceContainer
         */
        public void put(DeviceContainer deviceContainer){
            register.put(deviceContainer.getHeader().getId(), deviceContainer);
        }

        public DeviceContainer getContainer(Long containerId){
            return register.get(containerId);
        }

        public void remove(long containerId){
            register.remove(containerId);
        }

        /**
         * Returns a List of all DeviceContainers currently listed in the Map
         * @return
         */
        public List<DeviceContainer> getAllContainers(){
            ArrayList<DeviceContainer> list = new ArrayList<>();
            register.forEach((id,container) -> list.add(container));
            return list;
        }

        public void clear(){
                register.clear();
        }

    }

}
