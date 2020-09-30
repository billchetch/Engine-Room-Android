package net.chetch.engineroom.models;

import android.util.Log;
import net.chetch.engineroom.data.PompaCelup;
import net.chetch.engineroom.data.RPMCounter;
import net.chetch.messaging.Message;
import net.chetch.messaging.MessagingViewModel;

import java.util.HashMap;
import java.util.Map;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class EngineRoomMessagingModel extends MessagingViewModel {

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
        }
    };

    MutableLiveData<PompaCelup> liveDataPompaCelup = new MutableLiveData<>();
    MutableLiveData<RPMCounter> liveDataRPMCounter = new MutableLiveData<>();

    public EngineRoomMessagingModel(){
        super();

        try {
            addMessageFilter(onPompaCelup);
            addMessageFilter(onRPM);
        } catch (Exception e){
            Log.e("ERMM", e.getMessage());
        }
    }

    public void sendCommand(String commandName){
        getClient().sendCommand(EngineRoomMessageSchema.SERVICE_NAME, commandName);
    }

    @Override
    public void onClientConnected() {
        super.onClientConnected();

        //sendCommand(EngineRoomMessageSchema.COMMAND_TEST);
        Log.i("ERMM", "Client connected");
    }


    public LiveData<PompaCelup> getPompaCelup(){
        return liveDataPompaCelup;
    }
    public LiveData<RPMCounter> getRPMCounter(){
        return liveDataRPMCounter;
    }

}
