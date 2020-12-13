package net.chetch.engineroom.data;

import net.chetch.webservices.DataObject;

import java.util.List;

public class WaterTanks extends DataObject {
    public enum WaterLevel
    {
        EMPTY,
        VERY_LOW,
        LOW,
        OK,
        FULL
    }

    public void setEnabled(boolean enabled){
        setValue("enabled", enabled);
    }

    public boolean isEnabled(){ return getCasted("enabled"); }

    public void setPercentFull(int percentFull){ setValue("percent_full", percentFull); }
    public int getPercentFull(){ return getCasted("percent_full"); }

    public void setTankIDs(List<String> ids){
        setValue("tank_ids", ids);
    }
    public List<String> getTankIDs(){
        return getCasted("tank_ids");
    }

    public void setCapacity(int capacity){
        setValue("capacity", capacity);
    }
    public int getCapacity(){
        return getCasted("capacity");
    }
    public void setLevel(WaterLevel level){
        setValue("level", level);
    }
    public WaterLevel getLevel(){
        return getCasted("level");
    }
    public void setRemaining(int remaining){
        setValue("remaining", remaining);
    }
    public int getRemaining(){
        return getCasted("remaining");
    }

}
