package net.chetch.engineroom;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import net.chetch.engineroom.data.WaterTank;
import net.chetch.engineroom.data.WaterTanks;
import net.chetch.engineroom.models.EngineRoomMessageSchema;
import net.chetch.engineroom.models.EngineRoomMessagingModel;
import net.chetch.utilities.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

public class WaterTanksFragment extends Fragment {
    EngineRoomMessagingModel model;

    View contentView;
    IndicatorFragment titleFragment;
    WaterTanks waterTanks = null;
    Map<String, WaterTankFragment> waterTankFragments = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        contentView = inflater.inflate(R.layout.water_tanks, container, false);

        titleFragment = (IndicatorFragment) getChildFragmentManager().findFragmentById(R.id.waterTanksTitle);

        MenuItem.OnMenuItemClickListener selectMenuItem = (item) -> {
            switch(item.getItemId()){
                case IndicatorFragment.MENU_ITEM_DISABLE:
                    model.enableWaterTanks(false);
                    return true;
                case IndicatorFragment.MENU_ITEM_ENABLE:
                    model.enableWaterTanks(true);
                    return true;
                case IndicatorFragment.MENU_ITEM_VIEW_STATS:
                    ((MainPageFragment)getParentFragment()).openViewStats(null);
                    return true;
            }
            return true;
        };
        titleFragment.setContextMenu(selectMenuItem);

        return contentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(model == null) {
            model = ViewModelProviders.of(getActivity()).get(EngineRoomMessagingModel.class);

            model.getWaterTanks().observe(getViewLifecycleOwner(), wts ->{
                if(wts != null){
                    if(waterTanks == null){
                        for(String wtid : wts.getTankIDs()){
                            int wtfid = getResources().getIdentifier(wtid, "id", getActivity().getPackageName());
                            WaterTankFragment wtf = (WaterTankFragment)getChildFragmentManager().findFragmentById(wtfid);
                            waterTankFragments.put(wtid, wtf);

                            model.getWaterTank(wtid).observe(getViewLifecycleOwner(), wt->{
                                updateWaterTank(wt);
                            });

                            model.sendCommand(EngineRoomMessageSchema.COMMAND_WATER_TANK_STATUS, wtid);
                        }
                    }
                    waterTanks = wts;
                    updateWaterTanks();
                }
            });

            if(model.isClientConnected()) {
                model.sendCommand(EngineRoomMessageSchema.COMMAND_WATER_STATUS);
            }
        }
    }

    public void updateWaterTanks(){
        if(waterTanks == null)return;

        String title = "Water Tanks";
        String details = null;
        if(waterTanks.isEnabled()) {
            title += " @ " + waterTanks.getPercentFull() + "% (Level is " + waterTanks.getLevel() + ")";
            details = waterTanks.getTankIDs().size() + " water tanks have " + waterTanks.getRemaining() + "L remaining of a total capacity of " + waterTanks.getCapacity() + "L";
        } else {
            details = "Water tanks are offline";
        }

        titleFragment.setName(title);
        IndicatorFragment.State state;
        View wtl = contentView.findViewById(R.id.waterTanksLayout);
        if(waterTanks.isEnabled()){
            wtl.setVisibility(View.VISIBLE);
            state = IndicatorFragment.State.ON;
        } else {
            state = IndicatorFragment.State.DISABLED;
            wtl.setVisibility(View.GONE);
        }
        wtl.invalidate();
        wtl.requestLayout();
        titleFragment.update(state, details);
    }

    public void updateWaterTank(WaterTank wt){
        WaterTankFragment wtf = waterTankFragments.get(wt.getDeviceID());
        if(wtf == null)return;

        wtf.setName(wt.getDeviceID().toUpperCase());
        wtf.updateValue(wt.getPercentFull());
    }
}
