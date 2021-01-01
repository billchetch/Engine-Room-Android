package net.chetch.engineroom.models;

import net.chetch.engineroom.data.EngineRoomRepository;
import net.chetch.webservices.WebserviceViewModel;

public class EngineRoomServiceModel extends WebserviceViewModel {
    EngineRoomRepository engineRoomRepository = EngineRoomRepository.getInstance();

    public EngineRoomServiceModel(){
        addRepo(engineRoomRepository);
    }
}
