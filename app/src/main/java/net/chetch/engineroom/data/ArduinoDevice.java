package net.chetch.engineroom.data;

import net.chetch.webservices.DataObject;

public class ArduinoDevice extends DataObject {

    public ArduinoDevice(String deviceID){
        setValue("device_id", deviceID);
    }

    public String getDeviceID(){
        return getCasted("device_id");
    }
}
