package net.chetch.engineroom;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class MainPageFragment extends Fragment {
    public String tabKey = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(tabKey == null){
            tabKey = savedInstanceState.getString("tabKey");
        }

        int layout = getResources().getIdentifier("page_" + tabKey, "layout", getActivity().getPackageName());

        ViewGroup contentView = (ViewGroup) inflater.inflate(
                layout, container, false);

        init();

        return contentView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("tabKey", tabKey);
    }

    public void init() {
        switch(tabKey){
            case "engines":
                EngineFragment engine = (EngineFragment)getChildFragmentManager().findFragmentById(R.id.induk);
                engine.setEngineID("idk");
                engine.setName("Induk");
                engine.setMaxRPM(2000);
                engine.setRPMThresholds(1200, 1400,1500);
                engine.setTempThresholds(50, 60);

                engine = (EngineFragment)getChildFragmentManager().findFragmentById(R.id.bantu);
                engine.setEngineID("bnt");
                engine.setName("Bantu");
                engine.setMaxRPM(2000);
                engine.setRPMThresholds(1200, 1400,1500);
                engine.setTempThresholds(50, 60);
                break;

            case "gensets":
                //initialise genset fragments
                EngineFragment genset = (EngineFragment)getChildFragmentManager().findFragmentById(R.id.genset1);
                genset.setEngineID("gs1");
                genset.setName("Genset 1");
                genset.setMaxRPM(2000);
                genset.setRPMThresholds(1500, 1600,1700);
                genset.setTempThresholds(50, 60);

                genset = (EngineFragment)getChildFragmentManager().findFragmentById(R.id.genset2);
                genset.setEngineID("gs2");
                genset.setName("Genset 2");
                genset.setMaxRPM(2000);
                genset.setRPMThresholds(1500, 1600,1700);
                genset.setTempThresholds(50, 60);
                break;
        }
    }
}
