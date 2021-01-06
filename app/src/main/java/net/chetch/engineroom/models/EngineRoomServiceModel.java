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

    public EngineRoomServiceModel(){
        addRepo(engineRoomRepository);
    }

    public LiveData<EngineRoomStates> getStates(String stateSource, String stateName, Calendar fromDate, Calendar toDate, int interval){
        MutableLiveData<EngineRoomStates> liveDataEngineRoomStates = new MutableLiveData<>();
        engineRoomRepository.getStates(stateSource, stateName, fromDate, toDate, interval).add(liveDataEngineRoomStates);
        return liveDataEngineRoomStates;
    }

    public LiveData<EngineRoomEvents> getEvents(String eventSources, String eventTypes, Calendar fromDate, Calendar toDate, int interval){
        MutableLiveData<EngineRoomEvents> liveDataEngineRoomEvents = new MutableLiveData<>();
        engineRoomRepository.getEvents(eventSources, eventTypes, fromDate, toDate, interval).add(liveDataEngineRoomEvents);
        return liveDataEngineRoomEvents;
    }
}
