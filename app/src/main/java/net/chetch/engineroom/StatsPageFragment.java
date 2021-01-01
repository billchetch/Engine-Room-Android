package net.chetch.engineroom;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.chetch.appframework.GenericDialogFragment;
import net.chetch.appframework.IDialogManager;

public class StatsPageFragment extends ViewPageFragment{

    @Override
    protected String getLayoutNameForTab() {
        return "stats_page_" + tabKey;
    }

    @Override
    protected void init() {
        switch(tabKey){

            default:
                break;
        }
    }
}
