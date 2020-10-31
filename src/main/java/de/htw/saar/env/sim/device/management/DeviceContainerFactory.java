package de.htw.saar.env.sim.device.management;

import de.htw.saar.env.sim.device.container.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Factory class containing further factories for constructing runnable
 * DeviceContainers from Device and Behaviour .class files
 */
public class DeviceContainerFactory {

    DeviceFactory factory;
    long currentId;

    public DeviceContainerFactory(){
        currentId = new Long(0);
        factory = new DeviceFactory();
    }

    /**
     * Builds new DeviceContainers using information provided in a Blueprint object
     * @param blueprint Blueprint provided by client
     * @return List of new DeviceContainers with ascending IDs
     */
    public List<DeviceContainer> build(DeviceBlueprint blueprint) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        ArrayList<DeviceContainer> constructList = new ArrayList<>();
        for(int i = 0; i < blueprint.getAmount(); i++) {
            DeviceContainer construct = new DeviceContainer();
            construct.setDevice(factory.build(blueprint.getDevice(), blueprint.getBehaviour()));
            construct.setHeader(buildHeader(construct, blueprint.getDevice(), blueprint.getBehaviour()));
            constructList.add(construct);
            currentId++;
        }
        factory.latest = null;
        factory.factory.latest = null;
        return constructList;
    }

    /**
     * Constructs the information header containing meta-inf about the set of Behaviour, Device and Container
     * @return Finalized header
     */
    private DeviceHeader buildHeader(DeviceContainer container, String device, String behaviour){
        DeviceHeader construct = new DeviceHeader();
        construct.setId(this.currentId);
        construct.setBehaviour(behaviour);
        construct.setCycleTime(Long.toString(container.getDevice().getBehaviour().getCycleTime()));
        construct.setPublishList(container.getDevice().getPublishList());
        construct.setSubscribeList(container.getDevice().getSubscribeList());
        construct.setStatus(DeviceHeader.ContainerStatus.built);
        construct.setType(device);
        return construct;
    }

    public Behaviour buildBehaviour(String behaviour) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        return factory.factory.build(behaviour);
    }

    /**
     * Factory for constructing Device class Objects from a .class file implementing the Device abstract class
     */
    private class DeviceFactory {
        BehaviourFactory factory;
        Class latest;

        public DeviceFactory(){
            factory = new BehaviourFactory();
            latest = null;
        }

        public Device build(String device, String behaviour) throws ClassNotFoundException, IllegalAccessException, InstantiationException {

            if (latest == null) {
                DeviceClassLoader loader = new DeviceClassLoader(Device.class.getClassLoader());
                latest = loader.loadClass(device);
            }
            Device construct = (Device) latest.newInstance();
            construct.setBehaviour(factory.build(behaviour));
            return construct;
        }

    }

    /**
     * Factory for constructing Behaviour class Objects from a .class file implementing the Behaviour abstract class
     */
    private class BehaviourFactory {
        Class latest;
        public BehaviourFactory(){
            latest = null;
        }

        public Behaviour build(String behaviour) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
            if (latest == null) {
                DeviceClassLoader loader = new DeviceClassLoader(Behaviour.class.getClassLoader());
                latest = loader.loadClass(behaviour);
            }
            return (Behaviour) latest.newInstance();
        }
    }

}
