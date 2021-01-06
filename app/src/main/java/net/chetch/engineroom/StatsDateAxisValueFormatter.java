package net.chetch.engineroom;

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
    private String dateFormat = "HH";
    public long secondsOffset = 0;

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
        long millis = 1000*(secondsOffset + (long)value);

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis);

        return Utils.formatDate(cal, dateFormat);
    }

    @Override
    public String getBarLabel(BarEntry barEntry) {
        return super.getBarLabel(barEntry);
    }
}