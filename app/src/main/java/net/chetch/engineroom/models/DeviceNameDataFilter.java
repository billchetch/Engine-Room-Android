package net.chetch.engineroom.models;

import net.chetch.messaging.filters.DataFilter;

abstract public class DeviceNameDataFilter extends ServiceDataFilter {

    public DeviceNameDataFilter(String deviceName){
        super(EngineRoomMessageSchema.DEVICE_NAME_KEY, deviceName);
    }
}