package de.htw.saar.env.sim.util;

import de.htw.saar.env.sim.device.container.DeviceHeader;

import java.util.List;

/**
 * Container class containing any information provided by the StatusCollector
 * Is returned to the frontend-clients when getDeviceChanges() is called
 */
public class RESTContainer {

    /**
     * True when the StatusCollector returned a full list of devices
     * False when the StatusCollector returned a list of deice changes only
     */
    private boolean allFetched;

    private List<DeviceHeader> headerList;

    public RESTContainer(List<DeviceHeader> headers, Boolean allFetched){
        this.headerList = headers;
        this.allFetched = allFetched;
    }

    public boolean isAllFetched() {
        return allFetched;
    }

    public List<DeviceHeader> getHeaderList() {
        return headerList;
    }
}
