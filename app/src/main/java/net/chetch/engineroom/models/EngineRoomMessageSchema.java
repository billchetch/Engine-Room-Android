package net.chetch.engineroom.models;

import net.chetch.engineroom.data.OilSensor;
import net.chetch.engineroom.data.RPMCounter;
import net.chetch.engineroom.data.Pump;
import net.chetch.engineroom.data.TemperatureSensor;
import net.chetch.engineroom.data.Engine;
import net.chetch.engineroom.data.WaterTank;
import net.chetch.engineroom.data.WaterTanks;
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
    static public final String COMMAND_LIST_ENGINES = "list-engines";
    static public final String COMMAND_ENGINE_STATUS = "engine-status";
    static public final String COMMAND_ENABLE_ENGINE = "enable-engine";
    static public final String COMMAND_PUMP_STATUS = "pump-status";
    static public final String COMMAND_ENABLE_PUMP = "enable-pump";
    static public final String COMMAND_WATER_STATUS = "water-status";
    static public final String COMMAND_ENABLE_WATER = "enable-water";
    static public final String COMMAND_WATER_TANK_STATUS = "water-tank-status";

    static public final String RPM_NAME = "RPM";
    static public final String TEMP_ARRAY_NAME = "DS18B20";
    static public final String OIL_SENSOR_NAME = "OIL";
    static public final String POMPA_CELUP_ID = "pmp_clp";
    static public final String POMPA_SOLAR_ID = "pmp_sol";
    static public final String PUMP_NAME = "PUMP";
    static public final String WATER_TANK_NAME = "JSN-SR04T";



    public EngineRoomMessageSchema(Message message){
        super(message);
    }


    public Pump getPump(){
        if(message.hasValue(DEVICE_ID_KEY)){
            Pump pmp = new Pump(message.getString(DEVICE_ID_KEY));
            pmp.setState(message.getBoolean("State"));
            pmp.setEnabled(message.getBoolean("Enabled"));
            pmp.setLastOn(message.getCalendar("LastOn"));
            pmp.setLastOff(message.getCalendar("LastOff"));
            return pmp;
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

    public TemperatureSensor getTemperatureSensor(){
        TemperatureSensor ts = new TemperatureSensor();
        ts.sensorID = message.getString("SensorID");
        ts.temperature = message.getDouble("Temperature");
        return ts;
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
            engine.setEnabled(message.getBoolean("EngineEnabled"));
            engine.setRunning(message.getBoolean("EngineRunning"));
            engine.setLastOn(message.getCalendar("EngineLastOn"));
            engine.setLastOff(message.getCalendar("EngineLastOff"));
            engine.setRPMID(message.getString("RPMDeviceID"));
            engine.setOilSensorID(message.getString("OilSensorDeviceID"));
            engine.setTempSensorID(message.getString("TempSensorID"));
            return engine;
        } else {
            return null;
        }
    }

    public WaterTanks getWaterTanks(){
        WaterTanks waterTanks = new WaterTanks();
        waterTanks.setEnabled(message.getBoolean("Enabled"));
        waterTanks.setPercentFull(message.getInt("PercentFull"));
        waterTanks.setTankIDs(message.getList("Tanks", String.class));
        waterTanks.setCapacity(message.getInt("Capacity"));
        waterTanks.setRemaining(message.getInt("Remaining"));
        waterTanks.setLevel(message.getEnum("Level", WaterTanks.WaterLevel.class));
        return waterTanks;
    }

    public WaterTank getWaterTank(){
        if(message.hasValue(DEVICE_ID_KEY)) {
            WaterTank waterTank = new WaterTank(message.getString(DEVICE_ID_KEY));
            waterTank.setPercentFull(message.getInt("PercentFull"));

            return waterTank;
        } else {
            return null;
        }
    }
}
