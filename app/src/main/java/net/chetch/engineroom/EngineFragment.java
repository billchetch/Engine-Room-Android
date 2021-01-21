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
import net.chetch.engineroom.models.EngineRoomMessageSchema;
import net.chetch.engineroom.models.EngineRoomMessagingModel;
import net.chetch.utilities.Utils;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

public class EngineFragment extends Fragment  implements IUIUpdatable{
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

        MenuItem.OnMenuItemClickListener selectMenuItem = (item) -> {
            switch(item.getItemId()){
                case IndicatorFragment.MENU_ITEM_DISABLE:
                    model.enableEngine(engineID, false);
                    return true;
                case IndicatorFragment.MENU_ITEM_ENABLE:
                    model.enableEngine(engineID, true);
                    return true;
                case IndicatorFragment.MENU_ITEM_VIEW_STATS:
                    ((MainPageFragment)getParentFragment()).openViewStats(engineID, engineName);
                    return true;
            }
            return true;
        };

        titleFragment.setContextMenu(selectMenuItem);

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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(model == null) {
            model = new ViewModelProvider(getActivity()).get(EngineRoomMessagingModel.class);

            model.getRPMCounter(engineID + "_rpm").observe(getViewLifecycleOwner(), rpm->{
               if(engine != null && engine.isEnabled()){
                   updateRPM((int) rpm.getAverageRPM());
               }
            });

            model.getTemperatureSensor(engineID + "_temp").observe(getViewLifecycleOwner(), sensor->{
                if(engine != null && engine.isEnabled()){
                    updateTemp(sensor.temperature);
                }
            });

            model.getOilSensor(engineID + "_oil").observe(getViewLifecycleOwner(), sensor->{
                if(engine != null && engine.isEnabled()){
                    updateOilSensor(sensor.isOn());
                }
            });

            model.getEngine(engineID).observe(getViewLifecycleOwner(), eng->{
                if(eng != null){
                    engine  = eng;
                    updateEngineDetails();
                }
            });

            getEngineStatus();
        }
    }

    public void getEngineStatus(){
        if(model.isClientConnected()) {
            model.sendCommand(EngineRoomMessageSchema.COMMAND_ENGINE_STATUS, engineID);
        }
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

    public void updateTemp(double temp){
        tempFragment.updateValue(temp);
    }

    public void updateOilSensor(boolean isOn){
        oilFragment.update(isOn, null);
    }

    public void updateEngineDetails(){
        if(engine == null)return;

        String details = null;
        if(engine.isRunning()){
            details = "Started on " + Utils.formatDate(engine.getLastOn(), "dd MMM @ HH:mm");
            details += " (running for " + Utils.formatDuration(engine.getRunningDuration(), Utils.DurationFormat.D_H_M_S) + ")";
        } else {
            if(engine.isEnabled()) {
                if(engine.getLastOn() != null && engine.getLastOff() != null) {
                    details = "Last ran from " + Utils.formatDate(engine.getLastOn(), "dd MMM @ HH:mm") + " until " + Utils.formatDate(engine.getLastOff(), "dd MMM @ HH:mm") + " and ran for " + Utils.formatDuration(engine.getRunningDuration(), Utils.DurationFormat.D_H_M_S);
                } else {
                    details = "Engine has never been run";
                }
            } else {
                details = "Engine is offline";
            }
         }

        titleFragment.setName(engineName + (engine.isRunning() ? " (Running)" : ""));
        IndicatorFragment.State state;
        View edv = contentView.findViewById(R.id.engineDataLayout);
        if(engine.isEnabled()){
            state = engine.isRunning() ? IndicatorFragment.State.ON : IndicatorFragment.State.OFF;
            edv.setVisibility(View.VISIBLE);
        } else {
            state = IndicatorFragment.State.DISABLED;
            edv.setVisibility(View.GONE);
        }
        edv.invalidate();
        edv.requestLayout();
        titleFragment.update(state, details);
    }

    public void updateUI(){ //this is to be called by activity onTimer

        if(engine != null && engine.isEnabled() && engine.isRunning()){
            updateEngineDetails();
        }
    }
}
