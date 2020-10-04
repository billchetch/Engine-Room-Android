package net.chetch.engineroom.models;

import net.chetch.engineroom.data.OilSensor;
import net.chetch.engineroom.data.RPMCounter;
import net.chetch.engineroom.data.PompaCelup;
import net.chetch.engineroom.data.TemperatureSensor;
import net.chetch.engineroom.data.Engine;
import net.chetch.messaging.MessageSchema;
import net.chetch.messaging.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EngineRoomMessageSchema extends MessageSchema {
    static public final String SERVICE_NAME = "BBEngineRoom";

    static public final String DEVICE_ID_KEY = "DeviceID";
    static public final String DEVICE_NAME_KEY = "DeviceName";
    static public final String ENGINE_KEY = "Engine";
    static public final String COMMAND_TEST = "test";

    static public final String RPM_NAME = "RPM";
    static public final String TEMP_ARRAY_NAME = "DS18B20";
    static public final String OIL_SENSOR_NAME = "OIL";
    static public final String POMPA_CELUP_ID = "pmp_clp";


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

    public OilSensor getOilSensor(){
        if(message.hasValue(DEVICE_ID_KEY)){
            OilSensor os = new OilSensor(message.getString(DEVICE_ID_KEY));
            os.setState(message.getBoolean("State"));
            return os;
        } else {
            return null;
        }
    }

    public Engine getEngine(){
        if(message.hasValue(ENGINE_KEY)){
            Engine engine = new Engine(message.getString(ENGINE_KEY));
            engine.setRunning(message.getBoolean("EngineRunning"));
            engine.setLastOn(message.getCalendar("EngineLastOn"));
            engine.setLastOff(message.getCalendar("EngineLastOff"));
            return engine;
        } else {
            return null;
        }
    }
}
