package net.chetch.engineroom.models;

import net.chetch.engineroom.data.EngineRoomEvents;
import net.chetch.engineroom.data.EngineRoomRepository;
import net.chetch.engineroom.data.EngineRoomStates;
import net.chetch.webservices.WebserviceViewModel;

import java.util.Calendar;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class EngineRoomServiceModel extends WebserviceViewModel {
    EngineRoomRepository engineRoomRepository = EngineRoomRepository.getInstance();

    MutableLiveData<EngineRoomStates> liveDataEngineRoomStates = new MutableLiveData<>();
    MutableLiveData<EngineRoomEvents> liveDataEngineRoomEvents = new MutableLiveData<>();

    public EngineRoomServiceModel(){
        addRepo(engineRoomRepository);
    }

    public LiveData<EngineRoomStates> getStates(String stateSource, String stateName, Calendar fromDate, Calendar toDate, int interval){
        engineRoomRepository.getStates(stateSource, stateName, fromDate, toDate, interval).add(liveDataEngineRoomStates);
        return liveDataEngineRoomStates;
    }

    public LiveData<EngineRoomEvents> getEvents(String eventSource, String eventTypes, Calendar fromDate, Calendar toDate, int interval){
        engineRoomRepository.getEvents(eventSource, eventTypes, fromDate, toDate, interval).add(liveDataEngineRoomEvents);
        return liveDataEngineRoomEvents;
    }
}
