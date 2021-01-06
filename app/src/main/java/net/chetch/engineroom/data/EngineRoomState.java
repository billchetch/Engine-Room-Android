package net.chetch.engineroom.data;

import net.chetch.webservices.DataObject;

import java.util.Calendar;

public class EngineRoomState extends DataObject {

    public Calendar getCreated(){
        return getCasted("created");
    }

    public String getDescription() { return getCasted("state_description"); }

    public String getStateValue(){
        Object sv = getValue("state");
        return sv == null ? null : sv.toString();
    }

    public Double getStateAsDouble(){
        String sv = getStateValue();
        return sv == null ? null : Double.parseDouble(sv);
    }

    public Float getStateAsFloat(){
        String sv = getStateValue();
        return sv == null ? null : Float.parseFloat(sv);
    }

    public Integer getStateAsInt(){
        String sv = getStateValue();
        return sv == null ? null : Integer.parseInt(sv);
    }
}
