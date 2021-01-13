package net.chetch.engineroom;

import android.util.Log;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import net.chetch.utilities.Utils;

import java.util.Calendar;

public class StatsDateAxisValueFormatter extends ValueFormatter
{
    private final LineChart chart;
    private String dateFormat = "HH:mm";
    public int secondsOffset = 0;
    public int secondsInterval = 0;

    public StatsDateAxisValueFormatter(LineChart chart, String dateFormat) {
        this.chart = chart;
        if(dateFormat != null) {
            this.dateFormat = dateFormat;
        }
    }

    public StatsDateAxisValueFormatter(LineChart chart) {
        this(chart, null);
    }

    @Override
    public String getAxisLabel(float value, AxisBase axis) {
        long seconds = secondsOffset + secondsInterval* (int)value;

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(seconds * 1000);

        //Log.i("SDAVF", chart.getScaleX() + " - " + secondsInterval);
        Log.i("SDAVF", "Seconds: " + seconds + " gives date: " + Utils.formatDate(cal, "yyyy-MM-dd HH:mm:ss"));
        return Utils.formatDate(cal, dateFormat);
        //return (int)value + "";
    }

    @Override
    public String getBarLabel(BarEntry barEntry) {
        return super.getBarLabel(barEntry);
    }
}