package net.chetch.engineroom.models;

import net.chetch.engineroom.data.RPMCounter;
import net.chetch.engineroom.data.PompaCelup;
import net.chetch.engineroom.data.TemperatureSensor;
import net.chetch.messaging.MessageSchema;
import net.chetch.messaging.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EngineRoomMessageSchema extends MessageSchema {
    static public final String SERVICE_NAME = "BBEngineRoom";

    static public final String DEVICE_ID_KEY = "DeviceID";
    static public final String DEVICE_NAME_KEY = "DeviceName";
    static public final String COMMAND_TEST = "test";

    static public final String RPM_NAME = "RPM";
    static public final String TEMP_ARRAY_NAME = "DS18B20";
    static public final String POMPA_CELUP_ID = "pmp_clp";
    static public final String GENSET1_RPM_ID = "gs1_rpm";
    static public final String GENSET2_RPM_ID = "gs2_rpm";
    static public final String INDUK_RPM_ID = "idk_rpm";
    static public final String BANTU_RP_ID = "bnt_rpm";


    public EngineRoomMessageSchema(Message message){
        super(message);
    }


    public PompaCelup getPompaCelup(){
        if(message.hasValue(DEVICE_ID_KEY)){
            PompaCelup pc = new PompaCelup(message.getString(DEVICE_ID_KEY));
            pc.setState(message.getBoolean("State"));
            pc.setLastOn(message.getCalendar("LastOn"));
            pc.setLastOff(message.getCalendar("LastOff"));
            return pc;
        } else {
            return null;
        }
    }

    public RPMCounter getRPMCounter(){
        if(message.hasValue(DEVICE_ID_KEY)){
            RPMCounter rpm = new RPMCounter(message.getString(DEVICE_ID_KEY));
            rpm.setAverageRPM(message.getDouble("AverageRPM"));
            return rpm;
        } else {
            return null;
        }
    }

    public List<TemperatureSensor> getTemperatureSensors(){
        List<TemperatureSensor>  sensors = new ArrayList<>();
        if(message.hasValue("Sensors")){
            Map<String, Double> tmap = message.getMap("Sensors", Double.class);
            for(Map.Entry<String, Double> sensor : tmap.entrySet()){
                TemperatureSensor ts = new TemperatureSensor();
                ts.sensorID = sensor.getKey();
                ts.temperature = sensor.getValue();
                sensors.add(ts);
            }
        }
        return sensors;
    }
}
