package net.chetch.engineroom.data;

import android.util.Log;

import net.chetch.utilities.Utils;
import net.chetch.webservices.AboutService;
import net.chetch.webservices.DataCache;
import net.chetch.webservices.DataStore;
import net.chetch.webservices.Webservice;
import net.chetch.webservices.WebserviceRepository;
import net.chetch.webservices.WebserviceViewModel;

import java.util.Calendar;
import java.util.TimeZone;

public class EngineRoomRepository extends WebserviceRepository<IEngineRoomService> {
    static final String DATE_FORMAT_FOR_REQUESTS = "yyyy-MM-dd HH:mm:ss";


    static private EngineRoomRepository instance = null;
    static public EngineRoomRepository getInstance(){
        if(instance == null)instance = new EngineRoomRepository();
        return instance;
    }

    public EngineRoomRepository(){
        this(DataCache.VERY_SHORT_CACHE);
    }
    public EngineRoomRepository(int defaultCacheTime){
        super(IEngineRoomService.class, defaultCacheTime);
    }

    public DataStore<AboutService> getAbout(){
        DataCache.CacheEntry<AboutService> entry = cache.getCacheEntry("about-service");

        if(entry.requiresUpdating()) {
            service.getAbout().enqueue(createCallback(entry));
        }

        return entry;
    }

    private String date4request(Calendar cal){
        return Utils.formatDate(cal, DATE_FORMAT_FOR_REQUESTS, TimeZone.getTimeZone("UTC"));
    }

    public DataStore<EngineRoomStates> getStates(String stateSource, String stateName, Calendar fromDate, Calendar toDate, int interval){
        final DataStore<EngineRoomStates> states = new DataStore<>();

        service.getStates(stateSource, stateName, date4request(fromDate), date4request(toDate), interval).enqueue(createCallback(states));

        return states;
    }

    public DataStore<EngineRoomEvents> getEvents(String eventSource, String eventTypes, Calendar fromDate, Calendar toDate, int interval){
        final DataStore<EngineRoomEvents> events = new DataStore<>();

        service.getEvents(eventSource, eventTypes, date4request(fromDate), date4request(toDate), interval).enqueue(createCallback(events));

        return events;
    }
}
