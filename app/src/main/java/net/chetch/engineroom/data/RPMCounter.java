package net.chetch.engineroom.data;

public class RPMCounter extends ArduinoDevice {

    public RPMCounter(String deviceID){
        super(deviceID);
    }

    public void setAverageRPM(double rpm){
        setValue("average_rpm", rpm);
    }

    public double getAverageRPM(){
        return getCasted("average_rpm");
    }
}


