package net.chetch.engineroom.models;

import net.chetch.messaging.filters.DataFilter;

abstract public class DeviceIDDataFilter extends ServiceDataFilter {

    public DeviceIDDataFilter(String deviceID){
        super(EngineRoomMessageSchema.DEVICE_ID_KEY, deviceID);
    }

}