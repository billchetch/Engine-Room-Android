package net.chetch.engineroom.models;

import android.bluetooth.BluetoothClass;
import android.util.Log;

import net.chetch.engineroom.data.OilSensor;
import net.chetch.engineroom.data.Pump;
import net.chetch.engineroom.data.RPMCounter;
import net.chetch.engineroom.data.TemperatureSensor;
import net.chetch.engineroom.data.Engine;
import net.chetch.engineroom.data.WaterTank;
import net.chetch.engineroom.data.WaterTanks;
import net.chetch.messaging.Message;
import net.chetch.messaging.MessagingViewModel;
import net.chetch.messaging.filters.CommandResponseFilter;
import net.chetch.webservices.network.Services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class EngineRoomMessagingModel extends MessagingViewModel {

    class LiveDataMap<T> extends HashMap<String, MutableLiveData<T>>{
        void postValue(String key, T newValue){
            if(containsKey(key))get(key).postValue(newValue);
        }

        @Nullable
        @Override
        public MutableLiveData<T> get(@Nullable Object key){
            if(!containsKey(key)){
                put((String)key, new MutableLiveData<>());
            }
            return super.get(key);
        }
    }

    public CommandResponseFilter onEngineStatus = new CommandResponseFilter(EngineRoomMessageSchema.SERVICE_NAME, EngineRoomMessageSchema.COMMAND_ENGINE_STATUS){
        @Override
        protected void onMatched(Message message) {
            EngineRoomMessageSchema schema = new EngineRoomMessageSchema(message);
            Engine engine = schema.getEngine();
            if(engine != null){
                liveDataEngines.postValue(engine.getEngineID(), engine);
            }
        }
    };

    public CommandResponseFilter onEngineEnabled = new CommandResponseFilter(EngineRoomMessageSchema.SERVICE_NAME, EngineRoomMessageSchema.COMMAND_ENABLE_ENGINE){
        @Override
        protected void onMatched(Message message) {
            EngineRoomMessageSchema schema = new EngineRoomMessageSchema(message);
            Engine engine = schema.getEngine();
            if(engine != null){
                liveDataEngines.postValue(engine.getEngineID(), engine);
            }
        }
    };


    public ServiceDataFilter onEngineRunning = new ServiceDataFilter("Engine,EngineRunning") {
        @Override
        protected void onMatched(Message message) {
            EngineRoomMessageSchema schema = new EngineRoomMessageSchema(message);
            Engine engine = schema.getEngine();
            if(engine != null){
                liveDataEngines.postValue(engine.getEngineID(), engine);
            }
        }
    };

    public CommandResponseFilter onPumpEnabled = new CommandResponseFilter(EngineRoomMessageSchema.SERVICE_NAME, EngineRoomMessageSchema.COMMAND_ENABLE_PUMP){
        @Override
        protected void onMatched(Message message) {
            EngineRoomMessageSchema schema = new EngineRoomMessageSchema(message);
            Pump pmp = schema.getPump();
            if(pmp != null) {
                liveDataPumps.postValue(pmp.getDeviceID(), pmp);
            }
        }
    };

    public DeviceNameDataFilter onPump = new DeviceNameDataFilter(EngineRoomMessageSchema.PUMP_NAME){
        @Override
        protected void onMatched(Message message) {
            EngineRoomMessageSchema schema = new EngineRoomMessageSchema(message);
            Pump pmp = schema.getPump();
            if(pmp != null) {
                liveDataPumps.postValue(pmp.getDeviceID(), pmp);
            }
        }
    };

    public DeviceNameDataFilter onRPM = new DeviceNameDataFilter(EngineRoomMessageSchema.RPM_NAME) {
        @Override
        protected void onMatched(Message message) {
            EngineRoomMessageSchema schema = new EngineRoomMessageSchema(message);
            RPMCounter rpm = schema.getRPMCounter();
            liveDataRPMCounters.postValue(rpm.getDeviceID(), rpm);

            Engine engine = schema.getEngine();
            if(engine != null){
                liveDataEngines.postValue(engine.getEngineID(), engine);
            }
        }
    };

    public DeviceNameDataFilter onTempArray = new DeviceNameDataFilter(EngineRoomMessageSchema.TEMP_ARRAY_NAME) {
        @Override
        protected void onMatched(Message message) {
            EngineRoomMessageSchema schema = new EngineRoomMessageSchema(message);

            List<TemperatureSensor> sensors = schema.getTemperatureSensors();
            for(TemperatureSensor sensor : sensors){
                liveDataTemperatureSensors.postValue(sensor.sensorID, sensor);
            }
        }
    };

    public ServiceDataFilter onTempSensor = new ServiceDataFilter("SensorID,Temperature") {
        @Override
        protected void onMatched(Message message) {
            EngineRoomMessageSchema schema = new EngineRoomMessageSchema(message);
            TemperatureSensor sensor = schema.getTemperatureSensor();
            liveDataTemperatureSensors.postValue(sensor.sensorID, sensor);
        }
    };

    public DeviceNameDataFilter onOilSensor = new DeviceNameDataFilter(EngineRoomMessageSchema.OIL_SENSOR_NAME) {
        @Override
        protected void onMatched(Message message) {
            EngineRoomMessageSchema schema = new EngineRoomMessageSchema(message);

            OilSensor oilSensor = schema.getOilSensor();
            liveDataOilSensors.postValue(oilSensor.getDeviceID(), oilSensor);
        }
    };

    public CommandResponseFilter onWaterStatus = new CommandResponseFilter(EngineRoomMessageSchema.SERVICE_NAME, EngineRoomMessageSchema.COMMAND_WATER_STATUS){
        @Override
        protected void onMatched(Message message) {
            EngineRoomMessageSchema schema = new EngineRoomMessageSchema(message);
            WaterTanks waterTanks = schema.getWaterTanks();
            if(waterTanks != null){
                liveDataWater.postValue(waterTanks);
            }
        }
    };

    public CommandResponseFilter onWaterEnabled = new CommandResponseFilter(EngineRoomMessageSchema.SERVICE_NAME, EngineRoomMessageSchema.COMMAND_ENABLE_WATER){
        @Override
        protected void onMatched(Message message) {
            EngineRoomMessageSchema schema = new EngineRoomMessageSchema(message);
            WaterTanks waterTanks = schema.getWaterTanks();
            if(waterTanks != null){
                liveDataWater.postValue(waterTanks);
            }
        }
    };

    public DeviceNameDataFilter onWaterTank = new DeviceNameDataFilter(EngineRoomMessageSchema.WATER_TANK_NAME){
        @Override
        protected void onMatched(Message message) {
            EngineRoomMessageSchema schema = new EngineRoomMessageSchema(message);
            WaterTank waterTank = schema.getWaterTank();
            if(waterTank != null){
                liveDataWaterTanks.postValue(waterTank.getDeviceID(), waterTank);
            }
        }
    };

    LiveDataMap<Pump> liveDataPumps = new LiveDataMap<>();
    LiveDataMap<RPMCounter> liveDataRPMCounters = new LiveDataMap<>();
    LiveDataMap<TemperatureSensor> liveDataTemperatureSensors = new LiveDataMap<>();
    LiveDataMap<OilSensor> liveDataOilSensors = new LiveDataMap<>();
    LiveDataMap<Engine> liveDataEngines = new LiveDataMap<>();
    MutableLiveData<WaterTanks> liveDataWater = new MutableLiveData<>();
    LiveDataMap<WaterTank> liveDataWaterTanks = new LiveDataMap<>();

    public EngineRoomMessagingModel(){
        super();

        //TODO: Remove this
        permissableServerTimeDifference = 60 * 2;

        try {
            addMessageFilter(onEngineStatus);
            addMessageFilter(onEngineEnabled);
            addMessageFilter(onEngineRunning);
            addMessageFilter(onPump);
            addMessageFilter(onPumpEnabled);
            addMessageFilter(onRPM);
            addMessageFilter(onTempArray);
            addMessageFilter(onTempSensor);
            addMessageFilter(onOilSensor);
            addMessageFilter(onWaterStatus);
            addMessageFilter(onWaterEnabled);
            addMessageFilter(onWaterTank);
        } catch (Exception e){
            Log.e("ERMM", e.getMessage());
        }
    }

    public void sendCommand(String commandName, Object ... args){
        getClient().sendCommand(EngineRoomMessageSchema.SERVICE_NAME, commandName, args);
    }

    @Override
    public void onClientConnected() {
        super.onClientConnected();

        Log.i("ERMM", "Client connected");
    }

    protected boolean configureServices(Services services) {
        return super.configureServices(services);
    }

    public LiveData<Pump> getPump(String key){
        return liveDataPumps.get(key);
    }
    public LiveData<RPMCounter> getRPMCounter(String key){
        return liveDataRPMCounters.get(key);
    }
    public LiveData<TemperatureSensor> getTemperatureSensor(String key){
        return liveDataTemperatureSensors.get(key);
    }
    public LiveData<OilSensor> getOilSensor(String key){
        return liveDataOilSensors.get(key);
    }
    public LiveData<Engine> getEngine(String key){
        return liveDataEngines.get(key);
    }

    public LiveData<WaterTanks> getWaterTanks(){
        return liveDataWater;
    }

    public LiveData<WaterTank> getWaterTank(String key){
        return liveDataWaterTanks.get(key);
    }

    public void enableEngine(String engineID, boolean enable) {
        sendCommand(EngineRoomMessageSchema.COMMAND_ENABLE_ENGINE, engineID, enable);
        if(enable){
            sendCommand(EngineRoomMessageSchema.COMMAND_ENGINE_STATUS, engineID);
        }
    }

    public void enablePump(String pumpID, boolean enable) {
        sendCommand(EngineRoomMessageSchema.COMMAND_ENABLE_PUMP, pumpID, enable);
        if(enable){
            sendCommand(EngineRoomMessageSchema.COMMAND_PUMP_STATUS, pumpID);
        }
    }

    public void enableWaterTanks(boolean enable) {
        sendCommand(EngineRoomMessageSchema.COMMAND_ENABLE_WATER, enable);
        if(enable){
            sendCommand(EngineRoomMessageSchema.COMMAND_WATER_STATUS);
        }
    }
}
