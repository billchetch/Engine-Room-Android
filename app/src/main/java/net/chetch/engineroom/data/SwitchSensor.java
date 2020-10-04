package net.chetch.engineroom.data;

import java.util.Calendar;

public class SwitchSensor extends ArduinoDevice {
    public SwitchSensor(String deviceID){
        super(deviceID);
    }

    public void setState(boolean state){
        setValue("state", state);
    }
    public void setLastOn(Calendar cal){
        setValue("last_on", cal);
    }
    public void setLastOff(Calendar cal){
        setValue("last_off", cal);
    }
    public boolean isOn(){ return getState(); }
    public boolean getState(){
        return getCasted("state");
    }
    public Calendar getLastOn(){ return getCasted("last_on"); }
    public Calendar getLastOff(){ return getCasted("last_off"); }

    public long getOnDuration(){ //in millis
        if(getLastOn() == null)return 0;

        if(isOn()){
            return Calendar.getInstance().getTimeInMillis() - getLastOn().getTimeInMillis();
        } else {
            long useMillis = getLastOff() == null ? Calendar.getInstance().getTimeInMillis() : getLastOff().getTimeInMillis();
            return useMillis - getLastOn().getTimeInMillis();
        }
    }
}
