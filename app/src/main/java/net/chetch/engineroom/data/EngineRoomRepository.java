package net.chetch.engineroom.data;

import net.chetch.webservices.AboutService;
import net.chetch.webservices.DataCache;
import net.chetch.webservices.DataStore;
import net.chetch.webservices.WebserviceRepository;

public class EngineRoomRepository extends WebserviceRepository<IEngineRoomService> {

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
}
