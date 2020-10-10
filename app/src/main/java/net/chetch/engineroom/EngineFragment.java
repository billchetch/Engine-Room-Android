package net.chetch.engineroom;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import net.chetch.cmalarms.models.AlarmsMessagingModel;
import net.chetch.engineroom.data.Engine;
import net.chetch.engineroom.models.EngineRoomMessagingModel;
import net.chetch.utilities.Utils;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

public class EngineFragment extends Fragment {
    static final int MENU_ITEM_ONLINE = 1;
    static final int MENU_ITEM_OFFLINE = 2;

    EngineRoomMessagingModel model;

    String engineID;
    String engineName;
    Engine engine;
    View contentView;
    IndicatorFragment titleFragment;
    LinearScaleFragment rpmFragment;
    LinearScaleFragment tempFragment;
    IndicatorFragment oilFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        contentView = inflater.inflate(R.layout.engine, container, false);

        titleFragment = (IndicatorFragment) getChildFragmentManager().findFragmentById(R.id.engineTitleFragment);
        Map<IndicatorFragment.State, Map<Integer, String>> menuItems = new HashMap<>();

        Map<Integer, String> online = new HashMap<>();
        online.put(MENU_ITEM_ONLINE, "Bring engine online");

        Map<Integer, String> offline = new HashMap<>();
        offline.put(MENU_ITEM_OFFLINE, "Take engine offline");

        menuItems.put(IndicatorFragment.State.DISABLED, online);
        menuItems.put(IndicatorFragment.State.OFF, offline);
        menuItems.put(IndicatorFragment.State.ON, offline);

        MenuItem.OnMenuItemClickListener selectMenuItem = (item) -> {
            switch(item.getItemId()){
                case MENU_ITEM_OFFLINE:
                    model.setEngineOnline(engineID,false);
                    return true;
                case MENU_ITEM_ONLINE:
                    model.setEngineOnline(engineID,true);
                    return true;
            }
            return true;
        };

        titleFragment.setContextMenu(menuItems, selectMenuItem);

        rpmFragment = (LinearScaleFragment)getChildFragmentManager().findFragmentById(R.id.rpmFragment);
        rpmFragment.setName("RPM");
        rpmFragment.setThresholdColours(
                ContextCompat.getColor(getContext(), R.color.bluegreen2),
                ContextCompat.getColor(getContext(), R.color.bluegreen),
                ContextCompat.getColor(getContext(), R.color.age2),
                ContextCompat.getColor(getContext(), R.color.age4));

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
               if(isEngineForDevice(rpm.getDeviceID()) && engine != null && engine.isOnline()){
                   updateRPM((int) rpm.getAverageRPM());
               }
            });

            model.getTemperatureSensor().observe(getViewLifecycleOwner(), sensor->{
                if(isEngineForDevice(sensor.sensorID) && engine != null && engine.isOnline()){
                    updateTemp(sensor.temperature.intValue());
                }
            });

            model.getOilSensor().observe(getViewLifecycleOwner(), sensor->{
                if(isEngineForDevice(sensor.getDeviceID()) && engine != null && engine.isOnline()){
                    updateOilSensor(sensor.isOn());
                }
            });

            model.getEngine().observe(getViewLifecycleOwner(), eng->{
                if(engineID != null && engineID.equals(eng.getEngineID())){
                    engine  = eng;
                    updateEngineDetails();
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
        if(engineName == null)engineName = name;

        titleFragment.setName(engineName);
    }

    public void setMaxRPM(int maxRPM){
        rpmFragment.setLimits(0, maxRPM);
    }
    public void setRPMThresholds(int sweetSpot, int warn, int danger){
        rpmFragment.setThresholdValues(sweetSpot, warn, danger);
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

    public void updateOilSensor(boolean isOn){
        oilFragment.update(isOn, null);
    }

    public void updateEngineDetails(){
        if(engine == null)return;

        String details = null;
        if(engine.isRunning()){
            details = "Started on " + Utils.formatDate(engine.getLastOn(), "dd/MM/yyyy HH:mm");
            details += " (running for " + Utils.formatDuration(engine.getRunningDuration(), Utils.DurationFormat.D_H_M_S) + ")";
        } else {
            if(engine.isOnline()) {
                details = "Last ran on " + Utils.formatDate(engine.getLastOn(), "dd/MM/yyyy HH:mm") + " for " + Utils.formatDuration(engine.getRunningDuration(), Utils.DurationFormat.D_H_M_S);
            } else {
                details = "Engine is offline";
            }
         }

        titleFragment.setName(engineName + (engine.isRunning() ? " (Running)" : ""));
        IndicatorFragment.State state;
        View edv = contentView.findViewById(R.id.engineDataLayout);
        if(engine.isOnline()){
            state = engine.isRunning() ? IndicatorFragment.State.ON : IndicatorFragment.State.OFF;
            edv.setVisibility(View.VISIBLE);
        } else {
            state = IndicatorFragment.State.DISABLED;
            edv.setVisibility(View.GONE);
        }
        titleFragment.update(state, details);
    }

    public void updateUI(){ //this is to be called by activity onTimer
        updateEngineDetails();
    }
}
