package net.chetch.engineroom;

import android.os.Bundle;
import android.view.WindowManager;

import net.chetch.appframework.GenericDialogFragment;
import net.chetch.appframework.IDialogManager;
import net.chetch.engineroom.models.EngineRoomMessageSchema;
import net.chetch.engineroom.models.EngineRoomMessagingModel;

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
                WaterTanksFragment wts = (WaterTanksFragment)getChildFragmentManager().findFragmentById((R.id.waterTanksFragment));
                fragments2update.add(wts);
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

    public void openViewStats(String statsSource, String dialogTitle){
        if(statsDialog != null){
            statsDialog.dismiss();
        }
        statsDialog = new StatsDialogFragment();
        statsDialog.dialogTitle = dialogTitle;

        LinkedHashMap<String, String> tabMap = new LinkedHashMap<>();
        String pfx = tabKey + ":" + statsSource + ":";
        tabMap.put(pfx + "log", "Log");
        switch(tabKey) {
            case "engines":
            case "gensets":
                tabMap.put(pfx + "Temperature Average", "Temp");
                tabMap.put(pfx + "RPM Average", "RPM");
                break;

            case "water_tanks":
                tabMap.put(pfx + "Percent Full", "Level");
                break;

            case "misc":
                break;
        }

        statsDialog.setTabs(tabMap);
        statsDialog.show(getChildFragmentManager(), "StatsDialog");
    }

    @Override
    public void onPageSelected() {
        super.onPageSelected();
        EngineFragment engineFragment;
        switch(tabKey){
            case "engines":
                engineFragment = (EngineFragment)getChildFragmentManager().findFragmentById(R.id.induk);
                engineFragment.getEngineStatus();
                engineFragment = (EngineFragment)getChildFragmentManager().findFragmentById(R.id.bantu);
                engineFragment.getEngineStatus();
                break;
            case "gensets":
                engineFragment = (EngineFragment)getChildFragmentManager().findFragmentById(R.id.genset1);
                engineFragment.getEngineStatus();
                engineFragment = (EngineFragment)getChildFragmentManager().findFragmentById(R.id.genset2);
                engineFragment.getEngineStatus();
                break;
        }

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
