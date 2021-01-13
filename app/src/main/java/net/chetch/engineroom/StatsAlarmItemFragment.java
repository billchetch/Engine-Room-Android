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
import net.chetch.engineroom.data.EngineRoomEvent;
import net.chetch.utilities.Utils;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class StatsAlarmItemFragment extends GenericFragment implements RecyclerViewFragmentAdapter.IRecylcerViewFragment {
    View contentView;
    public AlarmsLogEntry entry;

    TextView eventType;
    TextView created;
    TextView description;

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

        //eventType = contentView.findViewById(R.id.eventType);
        //created = contentView.findViewById(R.id.eventCreated);
        //description = contentView.findViewById(R.id.eventDescription);

        return contentView;
    }

    public void populateContent(){
        try{
            /*eventType.setText(event.getValue("event_type").toString());
            description.setText(event.getValue("event_description").toString());
            Calendar cal = event.getCreated();
            created.setText(Utils.formatDate(cal, StatsPageFragment.EVENT_DATE_FORMAT));*/
        } catch (Exception e){
            Log.e("STAIF", e.getMessage());
        }
    }
}
