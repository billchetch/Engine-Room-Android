package net.chetch.engineroom.models;

import android.util.Log;

import net.chetch.engineroom.data.OilSensor;
import net.chetch.engineroom.data.PompaCelup;
import net.chetch.engineroom.data.RPMCounter;
import net.chetch.engineroom.data.TemperatureSensor;
import net.chetch.engineroom.data.Engine;
import net.chetch.messaging.Message;
import net.chetch.messaging.MessagingViewModel;
import net.chetch.messaging.filters.CommandResponseFilter;
import net.chetch.messaging.filters.DataFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class EngineRoomMessagingModel extends MessagingViewModel {
    public CommandResponseFilter onEngineStatus = new CommandResponseFilter(EngineRoomMessageSchema.SERVICE_NAME, EngineRoomMessageSchema.COMMAND_ENGINE_STATUS){
        @Override
        protected void onMatched(Message message) {
            EngineRoomMessageSchema schema = new EngineRoomMessageSchema(message);
            Engine engine = schema.getEngine();
            if(engine != null){
                liveDataEngine.postValue(engine);
            }
        }
    };

    public CommandResponseFilter onEngineOnline = new CommandResponseFilter(EngineRoomMessageSchema.SERVICE_NAME, EngineRoomMessageSchema.COMMAND_SET_ENGINE_ONLINE){
        @Override
        protected void onMatched(Message message) {
            EngineRoomMessageSchema schema = new EngineRoomMessageSchema(message);
            Engine engine = schema.getEngine();
            if(engine != null){
                liveDataEngine.postValue(engine);
            }
        }
    };

    public DeviceIDDataFilter onPompaCelup = new DeviceIDDataFilter(EngineRoomMessageSchema.POMPA_CELUP_ID){
        @Override
        protected void onMatched(Message message) {
            EngineRoomMessageSchema schema = new EngineRoomMessageSchema(message);
            PompaCelup pc = schema.getPompaCelup();
            liveDataPompaCelup.postValue(pc);
        }
    };

    public DeviceNameDataFilter onRPM = new DeviceNameDataFilter(EngineRoomMessageSchema.RPM_NAME) {
        @Override
        protected void onMatched(Message message) {
            EngineRoomMessageSchema schema = new EngineRoomMessageSchema(message);
            RPMCounter rpm = schema.getRPMCounter();
            liveDataRPMCounter.postValue(rpm);

            Engine engine = schema.getEngine();
            if(engine != null){
                liveDataEngine.postValue(engine);
            }
        }
    };

    public DeviceNameDataFilter onTempArray = new DeviceNameDataFilter(EngineRoomMessageSchema.TEMP_ARRAY_NAME) {
        @Override
        protected void onMatched(Message message) {
            EngineRoomMessageSchema schema = new EngineRoomMessageSchema(message);

            List<TemperatureSensor> sensors = schema.getTemperatureSensors();
            for(TemperatureSensor sensor : sensors){
                liveDataTemperatureSensor.postValue(sensor);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    public DeviceNameDataFilter onOilSensor = new DeviceNameDataFilter(EngineRoomMessageSchema.OIL_SENSOR_NAME) {
        @Override
        protected void onMatched(Message message) {
            EngineRoomMessageSchema schema = new EngineRoomMessageSchema(message);

            OilSensor oilSensor = schema.getOilSensor();
            liveDataOilSensor.postValue(oilSensor);
        }
    };

    MutableLiveData<PompaCelup> liveDataPompaCelup = new MutableLiveData<>();
    MutableLiveData<RPMCounter> liveDataRPMCounter = new MutableLiveData<>();
    MutableLiveData<TemperatureSensor> liveDataTemperatureSensor = new MutableLiveData<>();
    MutableLiveData<OilSensor> liveDataOilSensor = new MutableLiveData<>();
    MutableLiveData<Engine> liveDataEngine = new MutableLiveData<>();

    List<String> engineIDs = new ArrayList<>();

    public EngineRoomMessagingModel(){
        super();

        try {
            addMessageFilter(onEngineStatus);
            addMessageFilter(onEngineOnline);
            addMessageFilter(onPompaCelup);
            addMessageFilter(onRPM);
            addMessageFilter(onTempArray);
            addMessageFilter(onOilSensor);
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

        for(String engineID : engineIDs) {
            sendCommand(EngineRoomMessageSchema.COMMAND_ENGINE_STATUS, engineID);
        }
        Log.i("ERMM", "Client connected");
    }

    public void addEngine(String engineID){
        if(!engineIDs.contains(engineID))engineIDs.add(engineID);
    }

    public LiveData<PompaCelup> getPompaCelup(){
        return liveDataPompaCelup;
    }
    public LiveData<RPMCounter> getRPMCounter(){
        return liveDataRPMCounter;
    }
    public LiveData<TemperatureSensor> getTemperatureSensor(){
        return liveDataTemperatureSensor;
    }
    public LiveData<OilSensor> getOilSensor(){
        return liveDataOilSensor;
    }
    public LiveData<Engine> getEngine(){
        return liveDataEngine;
    }

    public void setEngineOnline(String engineID, boolean online){
        sendCommand(EngineRoomMessageSchema.COMMAND_SET_ENGINE_ONLINE, engineID, online);
    }
}
