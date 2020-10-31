package de.htw.saar.env.sim.device.scheduling;

import de.htw.saar.env.sim.io.SystemLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;

import static de.htw.saar.env.sim.io.LogStrings.*;

/**
 * ThreadManager class managing references to running device worker-threads stored in a HashMap
 */
@Service
public class ThreadManager {

    @Autowired
    SystemLogger logger;

    private final int LAST_ELEMENT_REMAINING = 1;

    private ConcurrentHashMap<Long, Thread > threadMap;

    public ThreadManager(){
        threadMap = new ConcurrentHashMap<>();
    }

    /**
     * Adds a new DeviceContainer Thread to the Map
     */
    public void addWorker(Thread worker, long containerId){
        try {
            threadMap.put(containerId, worker);
            worker.start();
        } catch (Exception e){
            logger.logError(THREAD_MANAGER_ADD + containerId);
        }
    }

    /**
     * Removes a DeviceContainer Thread from the Map
     */
    public void removeWorker(long containerId){
        try {
            threadMap.remove(containerId);
        } catch (Exception e){
            logger.logError(THREAD_MANAGER_REMOVE + containerId);
        }
    }

    /**
     * Terminates a running thread and removes it form the Map
     * @param id ID of the DeviceContainer
     * @return true when a thread was terminated and false when no active thread with the provided ID was found
     */
    public boolean terminate(Long id){
        if(!threadMap.containsKey(id)){
            return false;
        }
        try {
            threadMap.get(id).interrupt();
            threadMap.remove(id).join();
        } catch (InterruptedException e) {
        } catch (Exception e){
            logger.logError(THREAD_MANAGER_TERMINATE + id);
        }
        return true;
    }

    /**
     * Terminates all running threads and clears the Map
     */
    public void terminateAll() {
        try {
            threadMap.forEach((Long id, Thread t) -> {
                try {
                    t.interrupt();
                    t.join();
                } catch (InterruptedException e) {
                }
            });
            threadMap.clear();
        } catch (Exception e){
            logger.logError(THREAD_MANAGER_REMOVE_ALL);
        }
    }

    /**
     * Returns true if key is part of Map
     */
    public boolean contains(long id){
        return threadMap.containsKey(id);
    }

    public int getThreadMapSize(){return threadMap.size();}
}
