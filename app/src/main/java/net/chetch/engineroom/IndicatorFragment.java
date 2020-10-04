package net.chetch.engineroom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class IndicatorFragment extends Fragment {
    View contentView;
    String indicatorName;
    String indicatorDetails;
    int onColour;
    int offColour;
    boolean state = false;
    public boolean small = false; //TODO: make this settable

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        offColour = ContextCompat.getColor(getContext(), R.color.mediumnDarkGrey);
        onColour =  ContextCompat.getColor(getContext(), R.color.bluegreen2);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        contentView = inflater.inflate(small ? R.layout.indicator_small : R.layout.indicator, container, false);

        setName(indicatorName);

        return contentView;
    }

    @Override
    public void onInflate(Context context, AttributeSet attrs, Bundle savedInstanceState) {
        super.onInflate(context, attrs, savedInstanceState);

        try {
            TypedArray a = getActivity().obtainStyledAttributes(attrs, R.styleable.IndicatorFragment);
            indicatorName = a.getString(R.styleable.IndicatorFragment_indicator_name);
            small = a.getBoolean(R.styleable.IndicatorFragment_small_indicator, false);
            a.recycle();
        } catch (Exception e){
            Log.e("IndicatorFragment", e.getMessage());
        }
    }

    public void setName(String name){
        indicatorName = name;
        TextView tv = contentView.findViewById(R.id.indicatorName);
        tv.setText(indicatorName);
    }
    public void update(boolean state, String details){
        this.state = state;
        indicatorDetails = details;

        ImageView iv = contentView.findViewById(R.id.indicatorFg);
        GradientDrawable gd = (GradientDrawable)iv.getDrawable();
        int indicatorColour = state ? onColour : offColour;
        gd.setColor(indicatorColour);

        TextView tv = contentView.findViewById(R.id.indicatorDetails);
        if(tv != null) {
            tv.setText(details == null ? "" : details);
        }
    }
}
