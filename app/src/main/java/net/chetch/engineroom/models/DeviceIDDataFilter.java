package net.chetch.engineroom.models;

import net.chetch.messaging.filters.DataFilter;

abstract public class DeviceIDDataFilter extends DataFilter {

    public DeviceIDDataFilter(String deviceID){
        super(EngineRoomMessageSchema.SERVICE_NAME, EngineRoomMessageSchema.DEVICE_ID_KEY, deviceID);
    }

}