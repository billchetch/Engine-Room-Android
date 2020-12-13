package net.chetch.engineroom.data;

public class WaterTank extends ArduinoDevice {
    public WaterTank(String deviceID) {
        super(deviceID);
    }

    public void setPercentFull(int percentFull){ setValue("percent_full", percentFull); }
    public int getPercentFull(){ return getCasted("percent_full"); }
}
