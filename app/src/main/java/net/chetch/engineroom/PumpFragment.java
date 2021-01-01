package net.chetch.engineroom;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import net.chetch.engineroom.data.Pump;
import net.chetch.engineroom.models.EngineRoomMessageSchema;
import net.chetch.engineroom.models.EngineRoomMessagingModel;
import net.chetch.utilities.Utils;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

public class PumpFragment extends IndicatorFragment implements IUIUpdatable {
    EngineRoomMessagingModel model;
    String pumpID;
    Pump pump;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setContextMenu();
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (model == null) {
            model = ViewModelProviders.of(getActivity()).get(EngineRoomMessagingModel.class);

            model.getPump(pumpID).observe(getViewLifecycleOwner(), pmp -> {
                pump = pmp;
                updatePump();
            });

            if(model.isClientConnected()) {
                model.sendCommand(EngineRoomMessageSchema.COMMAND_PUMP_STATUS, pumpID);
            }
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch(menuItem.getItemId()){
            case IndicatorFragment.MENU_ITEM_DISABLE:
                model.enablePump(pumpID, false);
                break;
            case IndicatorFragment.MENU_ITEM_ENABLE:
                model.enablePump(pumpID, true);
                break;
            case IndicatorFragment.MENU_ITEM_VIEW_STATS:
                ((MainPageFragment)getParentFragment()).openViewStats(pumpID);
                return true;
        }
        return super.onMenuItemClick(menuItem);
    }

    public void setPumpID(String pid){
        pumpID = pid;
    }

    private void updatePump(){
        long duration = pump.getOnDuration();
        String fduration = duration > 0 ? Utils.formatDuration(duration, Utils.DurationFormat.D_H_M_S) : "n/a";
        String details = "";
        IndicatorFragment.State state;

        if(pump.isEnabled()) {
            state = pump.isOn() ? State.ON : State.OFF;
            if (pump.isOn()) {
                details = "Pumping started on  " + Utils.formatDate(pump.getLastOn(), "dd MMM, HH:mm:ss") + " (running time " + fduration + ")";
            } else if (pump.getLastOn() != null) {
                details = "Last pumped on " + Utils.formatDate(pump.getLastOn(), "dd MMM, HH:mm:ss") + " (duration " + fduration + ")";
            }
        } else {
            details = "Pump is offline";
            state = State.DISABLED;
        }


        update(state, details);
    }

    @Override
    public void updateUI() {

    }
}
