package net.chetch.engineroom;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;

public class WaterTankFragment extends LinearScaleFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setThresholdValues(5, 10, 20, 50, 75);
        thresholdColours.add(ContextCompat.getColor(getContext(), R.color.age5));
        thresholdColours.add(ContextCompat.getColor(getContext(), R.color.age3));
        thresholdColours.add(ContextCompat.getColor(getContext(), R.color.age2));
        thresholdColours.add(ContextCompat.getColor(getContext(), R.color.age1));
        thresholdColours.add(ContextCompat.getColor(getContext(), R.color.age0));
        thresholdColours.add(ContextCompat.getColor(getContext(), R.color.bluegreen));


        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void setValue(double value) {
        int val = (int)value;
        valueView.setText(val + "%");
    }
}
