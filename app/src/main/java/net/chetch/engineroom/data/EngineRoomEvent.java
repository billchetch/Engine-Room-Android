package net.chetch.engineroom.data;

import net.chetch.webservices.DataObject;

import java.util.Calendar;

public class EngineRoomEvent extends DataObject {

    static public final String ALL_TYPES = "*";


    public Calendar getCreated(){
        return getCasted("created");
    }

}
