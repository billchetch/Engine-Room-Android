package net.chetch.engineroom.data;

import net.chetch.webservices.AboutService;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface IEngineRoomService {
    String SERVICE_NAME = "Engine Room";

    @GET("about")
    Call<AboutService> getAbout();

    @GET("states")
    Call<EngineRoomStates> getStates(@Query("state_source") String stateSource, @Query("state_name") String stateName, @Query("from") String fromDate, @Query("to") String toDate, @Query("interval") int interval);

    @GET("events")
    Call<EngineRoomEvents> getEvents(@Query("event_source") String eventSource, @Query("event_types") String eventTypes, @Query("from") String fromDate, @Query("to") String toDate, @Query("interval") int interval);

}
