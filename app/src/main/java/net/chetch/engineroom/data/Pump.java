package net.chetch.engineroom.data;

import net.chetch.engineroom.models.EngineRoomMessageSchema;
import net.chetch.webservices.DataObject;

import java.util.Calendar;

public class Pump extends SwitchSensor {
    public Pump(String deviceID){
        super(deviceID);
    }
}
