package net.chetch.engineroom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class IndicatorFragment extends Fragment {
    enum State{
        ON,
        OFF,
        DISABLED
    }

    enum Size{
        SMALL,
        MEDIUM,
        LARGE
    }

    View contentView;
    String indicatorName;
    String indicatorDetails;
    int onColour;
    int offColour;
    int disabledColour;
    State state = State.OFF;
    public Size size = Size.MEDIUM;
    MenuItem.OnMenuItemClickListener selectMenuItem;
    Map<State, Map<Integer, String>> menuItems;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        disabledColour = ContextCompat.getColor(getContext(), R.color.darkGrey);
        offColour = ContextCompat.getColor(getContext(), R.color.mediumnDarkGrey);
        onColour =  ContextCompat.getColor(getContext(), R.color.bluegreen2);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        int rid = R.layout.indicator_medium;
        switch(size){
            case SMALL:
                rid = R.layout.indicator_small; break;
            case MEDIUM:
                rid = R.layout.indicator_medium; break;
            case LARGE:
                rid = R.layout.indicator_large; break;
        }
        contentView = inflater.inflate(rid, container, false);

        registerForContextMenu(contentView);
        setName(indicatorName);

        return contentView;
    }

    @Override
    public void onInflate(Context context, AttributeSet attrs, Bundle savedInstanceState) {
        super.onInflate(context, attrs, savedInstanceState);

        try {
            TypedArray a = getActivity().obtainStyledAttributes(attrs, R.styleable.IndicatorFragment);
            indicatorName = a.getString(R.styleable.IndicatorFragment_indicator_name);
            String sz = a.getString(R.styleable.IndicatorFragment_indicator_size);
            if(sz != null) {
                switch (sz.toLowerCase()) {
                    case "small":
                        size = Size.SMALL;
                        break;
                    case "medium":
                        size = Size.MEDIUM;
                        break;
                    case "large":
                        size = Size.LARGE;
                        break;
                }
            }
            a.recycle();
        } catch (Exception e){
            Log.e("IndicatorFragment", e.getMessage());
        }
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        try {
            Map<Integer, String> items = menuItems.containsKey(state) ? menuItems.get(state) : null;
            if(items != null){
                for(Map.Entry<Integer, String> item : items.entrySet()){
                    menu.add(0, item.getKey(), 0, item.getValue()).setOnMenuItemClickListener(selectMenuItem);
                }
            }
        } catch (Exception e){
            Log.e("IndicatorFragment", "onCreateContextMenu: " + e.getMessage());
        }
    }

    public void setContextMenu(Map<State, Map<Integer, String>> menuItems, MenuItem.OnMenuItemClickListener selectItem ){
        this.menuItems = menuItems;
        this.selectMenuItem = selectItem;
        registerForContextMenu(contentView);

    }

    public void setName(String name){
        indicatorName = name;
        TextView tv = contentView.findViewById(R.id.indicatorName);
        tv.setText(indicatorName);
    }

    public void update(boolean state, String details){
        update(state ? State.ON : State.OFF, details);
    }

    public void update(IndicatorFragment.State state, String details) {
        this.state = state;
        indicatorDetails = details;

        ImageView iv = contentView.findViewById(R.id.indicatorFg);
        GradientDrawable gd = (GradientDrawable) iv.getDrawable();
        int indicatorColour = 0;
        int textColour = ContextCompat.getColor(getContext(), R.color.white);
        switch (state){
            case ON:
                indicatorColour = onColour;
                break;
            case OFF:
                indicatorColour = offColour;
                break;
            case DISABLED:
                indicatorColour = disabledColour;
                textColour = ContextCompat.getColor(getContext(), R.color.mediumGrey);;
                break;
        }
        gd.setColor(indicatorColour);

        TextView tv = contentView.findViewById(R.id.indicatorDetails);
        if(tv != null) {
            tv.setText(details == null ? "" : details);
            tv.setTextColor(textColour);
        }

        tv = contentView.findViewById(R.id.indicatorName);
        if(tv != null) {
            tv.setTextColor(textColour);
        }
    }
}
