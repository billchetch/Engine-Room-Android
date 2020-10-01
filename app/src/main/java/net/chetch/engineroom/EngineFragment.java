package net.chetch.engineroom;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import net.chetch.cmalarms.models.AlarmsMessagingModel;
import net.chetch.engineroom.models.EngineRoomMessagingModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

public class EngineFragment extends Fragment {
    EngineRoomMessagingModel model;

    String engineID;
    View contentView;
    LinearScaleFragment rpmFragment;
    LinearScaleFragment tempFragment;
    IndicatorFragment oilFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        contentView = inflater.inflate(R.layout.engine, container, false);

        rpmFragment = (LinearScaleFragment)getChildFragmentManager().findFragmentById(R.id.rpmFragment);
        rpmFragment.setName("RPM");
        tempFragment = (LinearScaleFragment)getChildFragmentManager().findFragmentById(R.id.tempFragment);
        tempFragment.setName("Temp");

        oilFragment = (IndicatorFragment) getChildFragmentManager().findFragmentById(R.id.oilFragment);
        oilFragment.setName("Oil");

        return contentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(model == null) {
            model = ViewModelProviders.of(getActivity()).get(EngineRoomMessagingModel.class);

            model.getRPMCounter().observe(getViewLifecycleOwner(), rpm->{
               if(isEngineForDevice(rpm.getDeviceID())){
                    updateRPM((int)rpm.getAverageRPM());
               }
            });

            model.getTemperatureSensor().observe(getViewLifecycleOwner(), sensor->{
                if(isEngineForDevice(sensor.sensorID)){
                    updateTemp(sensor.temperature.intValue());
                }
            });
        }
    }

    private boolean isEngineForDevice(String deviceID){
        String[] parts = deviceID.split("_");
        return parts.length >= 2 && parts[0].equals(engineID);
    }

    public void setEngineID(String engineID){
        this.engineID = engineID;
    }

    public void setName(String name){
        TextView tv = contentView.findViewById(R.id.engineName);
        tv.setText(name);
    }

    public void setMaxRPM(int maxRPM){
        rpmFragment.setLimits(0, maxRPM);
    }
    public void setRPMThresholds(int warn, int danger){
        rpmFragment.setThresholdValues(warn, danger);
    }
    public void updateRPM(int rpm){
        rpmFragment.updateValue(rpm);
    }
    public void setTempThresholds(int warn, int danger){
        tempFragment.setThresholdValues(warn, danger);
    }

    public void updateTemp(int temp){
        tempFragment.updateValue(temp);
    }
}
