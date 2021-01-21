package net.chetch.engineroom;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.chetch.appframework.GenericFragment;
import net.chetch.appframework.RecyclerViewFragmentAdapter;
import net.chetch.cmalarms.data.AlarmsLogEntry;
import net.chetch.utilities.Utils;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class StatsAlarmItemFragment extends GenericFragment implements RecyclerViewFragmentAdapter.IRecylcerViewFragment {
    View contentView;
    public AlarmsLogEntry entry;

    TextView alarmName;
    TextView alarmEntryCreated;
    TextView alarmState;
    TextView alarmEntryDescription;

    @Override
    public void onBindData(Object data) {
        if(data != null) {
            entry = (AlarmsLogEntry) data;
            populateContent();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.stats_alarms_log_item, container, false);

        alarmName = contentView.findViewById(R.id.alarmName);
        alarmState = contentView.findViewById(R.id.alarmState);
        alarmEntryCreated = contentView.findViewById(R.id.alarmEntryCreated);
        alarmEntryDescription = contentView.findViewById(R.id.alarmEntryDescription);

        return contentView;
    }

    public void populateContent(){
        try{
            alarmName.setText(entry.getAlarmName());
            alarmState.setText(entry.getAlarmState().toString());
            String desc = entry.getAlarmMessage();
            if(desc == null || desc.isEmpty())desc = entry.getComments();
            alarmEntryDescription.setText(desc == null || desc.isEmpty() ? "N/A" : desc);
            Calendar cal = entry.getCreated();
            alarmEntryCreated.setText(Utils.formatDate(cal, StatsPageFragment.EVENT_DATE_FORMAT));
        } catch (Exception e){
            Log.e("STAIF", e.getMessage());
        }
    }
}
