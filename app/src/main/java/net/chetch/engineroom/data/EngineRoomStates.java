package net.chetch.engineroom.data;

import net.chetch.webservices.DataObjectCollection;

import java.util.Calendar;

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


    public float getGadient(int idx1, int idx2){
        int i1 = idx1 <= idx2 ? idx1 : idx2;
        int i2 = idx1 <= idx2 ? idx2 : idx1;

        EngineRoomState s1 = get(i1);
        EngineRoomState s2 = get(i2);
        float x = s2.getCreated().getTimeInMillis() - s1.getCreated().getTimeInMillis();
        float y = s2.getStateAsFloat() - s1.getStateAsFloat();

        return y / x;
    }

    public float getInterpolatedState(long millis, int idx1, int idx2){
        int i1 = idx1 <= idx2 ? idx1 : idx2;
        int i2 = idx1 <= idx2 ? idx2 : idx1;

        EngineRoomState s1 = get(i1);
        EngineRoomState s2 = get(i2);
        if(s1.getCreated().getTimeInMillis() == millis){
            return s1.getStateAsFloat();
        } else if(s2.getCreated().getTimeInMillis() == millis){
            return s2.getStateAsFloat();
        } else {
            float grad = getGadient(i1, i2);
            float delta = grad * (millis - s1.getCreated().getTimeInMillis());
            return s1.getStateAsFloat() + delta;
        }
    }
}
