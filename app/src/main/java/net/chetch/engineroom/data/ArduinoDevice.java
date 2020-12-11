package net.chetch.engineroom.data;

import net.chetch.webservices.DataObject;

public class ArduinoDevice extends DataObject {

    public ArduinoDevice(String deviceID){
        setValue("device_id", deviceID);
    }

    public String getDeviceID(){
        return getCasted("device_id");
    }

    public void setEnabled(boolean enabled){
        setValue("enabled", enabled);
    }
    public boolean isEnabled(){ return getCasted("enabled"); }
}
