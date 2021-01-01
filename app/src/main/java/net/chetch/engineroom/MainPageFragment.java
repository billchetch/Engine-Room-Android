package net.chetch.engineroom;

import android.os.Bundle;
import android.view.WindowManager;

import net.chetch.appframework.GenericDialogFragment;
import net.chetch.appframework.IDialogManager;
import net.chetch.engineroom.models.EngineRoomMessageSchema;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import androidx.annotation.NonNull;

public class MainPageFragment extends ViewPageFragment implements IDialogManager {

    StatsDialogFragment statsDialog;

    @Override
    protected String getLayoutNameForTab() {
        return "page_" + tabKey;
    }

    @Override
    protected void init() {
        switch(tabKey){
            case "engines":
                EngineFragment engine = (EngineFragment)getChildFragmentManager().findFragmentById(R.id.induk);
                engine.setEngineID("idk");
                engine.setName("Induk");
                engine.setMaxRPM(2000);
                engine.setRPMThresholds(1200, 1400,1500);
                engine.setTempThresholds(50, 60);
                fragments2update.add(engine);

                engine = (EngineFragment)getChildFragmentManager().findFragmentById(R.id.bantu);
                engine.setEngineID("bnt");
                engine.setName("Bantu");
                engine.setMaxRPM(2000);
                engine.setRPMThresholds(1200, 1400,1500);
                engine.setTempThresholds(50, 60);
                fragments2update.add(engine);
                break;

            case "gensets":
                //initialise genset fragments
                EngineFragment genset = (EngineFragment)getChildFragmentManager().findFragmentById(R.id.genset1);
                genset.setEngineID("gs1");
                genset.setName("Genset 1");
                genset.setMaxRPM(2000);
                genset.setRPMThresholds(1500, 1600,1700);
                genset.setTempThresholds(50, 60);
                fragments2update.add(genset);

                genset = (EngineFragment)getChildFragmentManager().findFragmentById(R.id.genset2);
                genset.setEngineID("gs2");
                genset.setName("Genset 2");
                genset.setMaxRPM(2000);
                genset.setRPMThresholds(1500, 1600,1700);
                genset.setTempThresholds(50, 60);
                fragments2update.add(genset);
                break;

            case "water_tanks":
                break;

            case "misc":
                PumpFragment pumpFragment = (PumpFragment)getChildFragmentManager().findFragmentById(R.id.pompaCelup);
                pumpFragment.setName("Pompa Celup");
                pumpFragment.setPumpID(EngineRoomMessageSchema.POMPA_CELUP_ID);
                fragments2update.add(pumpFragment);

                pumpFragment = (PumpFragment)getChildFragmentManager().findFragmentById(R.id.pompaSolar);
                pumpFragment.setName("Pompa Solar");
                pumpFragment.setPumpID(EngineRoomMessageSchema.POMPA_SOLAR_ID);
                fragments2update.add(pumpFragment);
                break;
        }
    }

    public void openViewStats(String statsArea){
        if(statsDialog != null){
            statsDialog.dismiss();
        }
        statsDialog = new StatsDialogFragment();

        LinkedHashMap<String, String> tabMap = new LinkedHashMap<>();
        switch(tabKey) {
            case "engines":
                tabMap.put("engine_temp", "Temp");
                tabMap.put("engine_rpm", "RPM");
                tabMap.put("alerts", "Alerts");
                break;

            case "gensets":
                tabMap.put("engine_temp", "Temp");
                tabMap.put("engine_rpm", "RPM");
                tabMap.put("alerts", "Alerts");
                break;

            case "water_tanks":
                tabMap.put("level", "Level");
                tabMap.put("wt1", "WT1");
                tabMap.put("wt2", "WT2");
                tabMap.put("alerts", "Alerts");
                break;

            case "misc":
                switch(statsArea){
                    case "pompa-celup":
                    case "pompa-solar":
                        tabMap.put("level", "Level");
                        tabMap.put("alerts", "Alerts");
                        break;
                }
                break;
        }

        statsDialog.setTabs(tabMap);
        statsDialog.show(getChildFragmentManager(), "StatsDialog");
    }

    @Override
    public void updateUI() {
        super.updateUI();

        if(statsDialog != null && statsDialog.isShowing()){
            statsDialog.updateUI();
        }
    }

    @Override
    public void showWarningDialog(String warning) {

    }

    @Override
    public void onDialogPositiveClick(GenericDialogFragment dialog){

    }

    @Override
    public void onDialogNegativeClick(GenericDialogFragment dialog) {

    }
}
