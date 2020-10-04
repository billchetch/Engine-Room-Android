package net.chetch.engineroom.data;

import net.chetch.webservices.DataObject;

import java.util.Calendar;

public class Engine extends DataObject {
    public Engine(String engineID){
        setValue("engine_id", engineID);
    }

    public String getEngineID(){
        return getCasted("engine_id");
    }

    public void setRunning(boolean running){
        setValue("running", running);
    }

    public void setLastOn(Calendar cal){
        setValue("last_on", cal);
    }
    public void setLastOff(Calendar cal){
        setValue("last_off", cal);
    }

    public boolean isRunning(){ return getCasted("running"); }
    public Calendar getLastOn(){ return getCasted("last_on"); }
    public Calendar getLastOff(){ return getCasted("last_off"); }

    public long getRunningDuration(){ //in millis
        if(getLastOn() == null)return 0;

        if(isRunning()){
            return Calendar.getInstance().getTimeInMillis() - getLastOn().getTimeInMillis();
        } else {
            long useMillis = getLastOff() == null ? Calendar.getInstance().getTimeInMillis() : getLastOff().getTimeInMillis();
            return useMillis - getLastOn().getTimeInMillis();
        }
    }
}
