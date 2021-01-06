package net.chetch.engineroom;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.chetch.appframework.GenericFragment;
import net.chetch.appframework.RecyclerViewFragmentAdapter;
import net.chetch.engineroom.data.EngineRoomState;
import net.chetch.utilities.Utils;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class StatsStateItemFragment extends GenericFragment implements RecyclerViewFragmentAdapter.IRecylcerViewFragment {
    View contentView;
    public EngineRoomState state;

    TextView stateValue;
    TextView created;
    TextView description;

    @Override
    public void onBindData(Object data) {
        if(data != null) {
            state = (EngineRoomState) data;
            populateContent();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.stats_state_log_item, container, false);

        stateValue = contentView.findViewById(R.id.stateValue);
        created = contentView.findViewById(R.id.stateCreated);
        description = contentView.findViewById(R.id.stateDescription);

        return contentView;
    }

    public void populateContent(){
        try{
            stateValue.setText(state.getValue("state").toString());
            description.setText(state.getDescription());
            Calendar cal = state.getCreated();
            created.setText(Utils.formatDate(cal, StatsPageFragment.STATE_DATE_FORMAT) + ": ");
        } catch (Exception e){
            Log.e("STLIF", e.getMessage());
        }
    }
}
