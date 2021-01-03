package net.chetch.engineroom;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.chetch.engineroom.data.EngineRoomEvent;
import net.chetch.engineroom.data.EngineRoomEvents;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class StatsLogAdapter extends RecyclerView.Adapter<StatsLogAdapter.ViewHolder>{

    public static class ViewHolder extends RecyclerView.ViewHolder{
        StatsLogItemFragment logItemFragment;
        public ViewHolder(View v, StatsLogItemFragment logItemFragment) {
            super(v);
            this.logItemFragment = logItemFragment;
        }
    }

    EngineRoomEvents events;

    public void setDataset(EngineRoomEvents events){
        this.events = events;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        StatsLogItemFragment logItemFragment = new StatsLogItemFragment();
        View v = logItemFragment.onCreateView(LayoutInflater.from(parent.getContext()), parent, null);

        ViewHolder vh = new ViewHolder(v, logItemFragment);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(events != null) {
            EngineRoomEvent event = events.get(position);
            holder.logItemFragment.event = event;
            holder.logItemFragment.populateContent();
        }
    }

    @Override
    public int getItemCount() {
        return this.events == null ? 0 : events.size();
    }

}
