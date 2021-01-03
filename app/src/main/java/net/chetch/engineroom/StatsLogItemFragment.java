package net.chetch.engineroom;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.chetch.appframework.GenericFragment;
import net.chetch.engineroom.data.EngineRoomEvent;
import net.chetch.utilities.Utils;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class StatsLogItemFragment extends GenericFragment {
    View contentView;
    public EngineRoomEvent event;

    TextView eventType;
    TextView created;
    TextView description;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.stats_log_item, container, false);

        eventType = contentView.findViewById(R.id.eventType);
        created = contentView.findViewById(R.id.eventCreated);
        description = contentView.findViewById(R.id.eventDescription);

        return contentView;
    }

    public void populateContent(){
        try{
            eventType.setText(event.getValue("event_type").toString());
            description.setText(event.getValue("event_description").toString());
            Calendar cal = event.getCreated();
            created.setText(Utils.formatDate(cal, StatsPageFragment.DATE_FORMAT));
        } catch (Exception e){
            Log.e("STLIF", e.getMessage());
        }
    }
}
