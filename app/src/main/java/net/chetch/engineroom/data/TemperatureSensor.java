package net.chetch.engineroom.data;

import net.chetch.webservices.DataObject;

public class TemperatureSensor extends DataObject {
    public transient String sensorID;
    public transient Double temperature;
}
