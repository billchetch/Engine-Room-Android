package net.chetch.engineroom.data;

import net.chetch.webservices.DataObjectCollection;

public class EngineRoomStates extends DataObjectCollection<EngineRoomState> {
    public EngineRoomStates() {
        super(EngineRoomStates.class);
    }

    public EngineRoomStates sortLatestFirst(){
        return sort("created", DataObjectCollection.SortOptions.DESC);
    }

    public EngineRoomStates sortEarliestFirst(){
        return sort("created", DataObjectCollection.SortOptions.ASC);
    }
}
