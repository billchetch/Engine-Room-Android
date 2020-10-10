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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class LinearScaleFragment extends Fragment {
    View contentView;
    int minValue = 0;
    int maxValue = 100;
    ImageView linearScaleView;
    TextView valueView;
    List<Integer> thresholdValues = new ArrayList();
    List<Integer> thresholdColours = new ArrayList();
    String name;
    boolean scaleWidth = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        contentView = inflater.inflate(R.layout.linear_scale, container, false);

        linearScaleView = contentView.findViewById(R.id.lsScaleInterior);
        linearScaleView.setVisibility(View.INVISIBLE);

        valueView = contentView.findViewById(R.id.lsValue);

        if(thresholdColours.size() == 0){
            thresholdColours.add(ContextCompat.getColor(getContext(), R.color.age0));
            thresholdColours.add(ContextCompat.getColor(getContext(), R.color.age2));
            thresholdColours.add(ContextCompat.getColor(getContext(), R.color.age4));
        }

        return contentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View lsView = contentView.findViewById(R.id.lsScaleBorder);
    }

    @Override
    public void onInflate(Context context, AttributeSet attrs, Bundle savedInstanceState) {
        super.onInflate(context, attrs, savedInstanceState);

        /*
        try {
            TypedArray a = getActivity().obtainStyledAttributes(attrs, R.styleable.IndicatorFragment);
            //indicatorName = a.getString(R.styleable.IndicatorFragment_indicator_name);
            a.recycle();
        } catch (Exception e){
            Log.e("RPMFragment", e.getMessage());
        }*/
    }

    public void setLimits(int min, int max){
        minValue = min;
        maxValue = max;
    }

    public void setThresholdValues(int ... thresholds){
        thresholdValues.clear();
        for(int i : thresholds){
            thresholdValues.add(i);
        }
    }

    public void setThresholdColours(int ... colours){
        thresholdColours.clear();
        for(int c : colours){
            thresholdColours.add(c);
        }
    }

    public void setName(String name){
        TextView tv = contentView.findViewById(R.id.lsName);
        tv.setText(name);
    }

    public void updateValue(int value){
        View scaleBorder = contentView.findViewById(R.id.lsScaleBorder);
        int lsMax = scaleWidth ? scaleBorder.getWidth() : scaleBorder.getHeight();

        double scale = 0;
        if(value <= minValue){
            scale = 0;
        } else if(value >= maxValue){
            scale = 1;
        } else {
            scale = (double)(value - minValue) / (double)maxValue;
        }

        int dim = (int)(scale*(double)lsMax);
        ViewGroup.LayoutParams layoutParams = linearScaleView.getLayoutParams();
        if(scaleWidth){
            layoutParams.width = dim;
        } else {
            layoutParams.height = dim;
        }
        linearScaleView.invalidate();
        linearScaleView.requestLayout();

        int colour = thresholdColours.get(Math.max(thresholdColours.size()-1, 0));
        for(int i = 0; i < thresholdValues.size(); i++){
            if(value < thresholdValues.get(i)){
                colour = thresholdColours.get(i);
                break;
            }
        }
        GradientDrawable gd = (GradientDrawable)linearScaleView.getDrawable();
        gd.setColor(colour);

        linearScaleView.setVisibility(dim > 0 ? View.VISIBLE: View.INVISIBLE);

        valueView.setText(value == 0 ? "" : value + "");
    }
}
