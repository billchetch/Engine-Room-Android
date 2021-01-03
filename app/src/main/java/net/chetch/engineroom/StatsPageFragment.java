package net.chetch.engineroom;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.chetch.appframework.GenericDialogFragment;
import net.chetch.appframework.IDialogManager;
import net.chetch.engineroom.data.EngineRoomEvent;
import net.chetch.engineroom.data.EngineRoomStates;
import net.chetch.engineroom.models.EngineRoomServiceModel;
import net.chetch.utilities.DatePeriod;
import net.chetch.utilities.Utils;

import java.util.Calendar;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class StatsPageFragment extends ViewPageFragment {
    static public final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    EngineRoomServiceModel erServiceModel;
    StatsPrevNextFragment prevNextFragment;
    RecyclerView logRecyclerView;
    StatsLogAdapter statsLogAdapter;
    TextView statsTitle;

    String openingTabKey;
    String statsSource;
    String statsName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(tabKey == null){
            tabKey = savedInstanceState.getString("tabKey");
        }

        //we get these first as they are used in getLayoutNameForTab which is called in the parent onCreateView method
        String[] parts = tabKey.split(":");
        openingTabKey = parts[0];
        statsSource = parts[1];
        statsName = parts[2];

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected String getLayoutNameForTab() {
        String pageName = null;
        switch(statsName){
            case "Temperature":
            case "Temperature Average":
            case "RPM":
            case "RPM Average":
            case "Percent Full":
            case "Level":
                pageName = "graph";
                break;
            default:
                pageName = "log";
                break;
        }


        return "stats_page_" + pageName;
    }

    private void loadData(){
        //get the date period and interval to load
        int position = prevNextFragment.getCurrentPosition();
        DatePeriod period = DatePeriod.getWeekPeriod(position - 1);
        int interval = 0;

        //update the title
        if(statsTitle != null){
            String s = "From ";
            s += Utils.formatDate(period.fromDate, DATE_FORMAT);
            s += " to ";
            s += Utils.formatDate(period.toDate, DATE_FORMAT);
            statsTitle.setText(s);
        }

        //clear away previous data
        if(statsLogAdapter != null){
            statsLogAdapter.setDataset(null);
        }

        //determine what model methods to call
        switch(statsName) {
            case "Temperature":
            case "Temperature Average":
            case "RPM":
            case "RPM Average":
            case "Percent Full":
            case "Level":
                erServiceModel.getStates(statsSource, statsName, period.fromDate, period.toDate, interval).observe(getViewLifecycleOwner(), states -> {
                    Log.i("SPF", "Loaded " + states.size() + " states for position " + position);
                });
                break;
        }

        //we always get events
        erServiceModel.getEvents(statsSource, EngineRoomEvent.ALL_TYPES, period.fromDate, period.toDate, 0).observe(getViewLifecycleOwner(), events -> {
            if(statsLogAdapter != null){
                statsLogAdapter.setDataset(events);
            }
            if(logRecyclerView != null)logRecyclerView.smoothScrollToPosition(0);

            Log.i("SPF", "Loaded " + events.size() + " events for position " + position);
        });
    }

    @Override
    protected void init() {
        erServiceModel = new ViewModelProvider(getActivity()).get(EngineRoomServiceModel.class);

        statsTitle = contentView.findViewById(R.id.statsTitle);

        logRecyclerView = contentView.findViewById(R.id.logRecylerView);
        if(logRecyclerView != null){
            logRecyclerView.setHasFixedSize(true);
            logRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            statsLogAdapter = new StatsLogAdapter();
            logRecyclerView.setAdapter(statsLogAdapter);
        }

        prevNextFragment = (StatsPrevNextFragment)getChildFragmentManager().findFragmentById(R.id.prevNextStats);
        if(prevNextFragment != null){
            prevNextFragment.observe(o -> {
                loadData();
            });

            loadData();
        }
    }
}
