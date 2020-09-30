package net.chetch.engineroom.models;

import net.chetch.messaging.filters.DataFilter;

abstract public class DeviceNameDataFilter extends DataFilter {

    public DeviceNameDataFilter(String deviceName){
        super(EngineRoomMessageSchema.SERVICE_NAME, EngineRoomMessageSchema.DEVICE_NAME_KEY, deviceName);
    }
}