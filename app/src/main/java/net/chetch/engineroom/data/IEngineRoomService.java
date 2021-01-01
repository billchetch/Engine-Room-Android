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

}
