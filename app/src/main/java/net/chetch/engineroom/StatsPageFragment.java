package net.chetch.engineroom;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import net.chetch.appframework.RecyclerViewFragmentAdapter;
import net.chetch.cmalarms.models.AlarmsWebserviceModel;
import net.chetch.engineroom.data.EngineRoomEvent;
import net.chetch.engineroom.data.EngineRoomEvents;
import net.chetch.engineroom.data.EngineRoomState;
import net.chetch.engineroom.data.EngineRoomStates;
import net.chetch.engineroom.models.EngineRoomWebserviceModel;
import net.chetch.utilities.DatePeriod;
import net.chetch.utilities.Utils;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class StatsPageFragment extends ViewPageFragment implements OnChartValueSelectedListener {
    static public final String EVENT_DATE_FORMAT = "MMM d, HH:mm";
    static public final String STATE_DATE_FORMAT = "MMM d, HH:mm:ss";
    static public final String DATE_PERIOD_DATE_FORMAT = "MMM d";

    EngineRoomWebserviceModel erServiceModel;
    AlarmsWebserviceModel alarmsWebserviceModel;
    StatsPrevNextFragment prevNextFragment;

    TextView statsTitle;

    TimeUnit datePeriodTimeUnit = TimeUnit.HOURS;

    String openingTabKey;
    String statsSource;
    String statsName;

    ProgressBar progressBar;

    RecyclerView logRecyclerView;
    RecyclerViewFragmentAdapter<StatsEventItemFragment> eventLogAdapter;
    RecyclerViewFragmentAdapter<StatsStateItemFragment> stateLogAdapter;
    RecyclerViewFragmentAdapter<StatsAlarmItemFragment> alarmsLogAdapter;

    LineChart chart;
    StatsDateAxisValueFormatter dateXAxisFormatter;
    DatePeriod period;
    int dpinterval;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(tabKey == null){
            tabKey = savedInstanceState.getString("tabKey");
        }

        //we get these first as they are used in getLayoutNameForTab which is called in the parent onCreateView method
        String[] parts = tabKey.split(":");
        openingTabKey = parts[0];
        statsSource = parts[1];
        statsName = parts[2];

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected String getLayoutNameForTab() {
        String pageName = null;
        switch(statsName){
            case "Temperature":
            case "Temperature Average":
            case "RPM":
            case "RPM Average":
            case "Percent Full":
            case "Level":
                pageName = "graph";
                break;
            default:
                pageName = "log";
                break;
        }


        return "stats_page_" + pageName;
    }

    private void loadData(){
        //get the date period and interval to load
        int position = prevNextFragment.getCurrentPosition();
        boolean loadEvents = false;
        String eventSources = statsSource;
        boolean loadStates = false;
        String stateSource = statsSource;

        //determine what model methods to call
        switch(statsName) {
            case "Temperature":
            case "Temperature Average":
            case "RPM":
            case "RPM Average":
                period = DatePeriod.getPeriod(datePeriodTimeUnit, 1*24, position);
                loadStates = true;
                break;

            case "Percent Full":
            case "Level":
                period = DatePeriod.getPeriod(datePeriodTimeUnit, 7*24, position);
                loadStates = true;
                break;

            case "log":
                if(statsSource.toLowerCase().equals("alarms")){
                    period = DatePeriod.getPeriod(datePeriodTimeUnit, 4*7*24, position);
                    alarmsWebserviceModel.getLog(period.fromDate, period.toDate).observe(getViewLifecycleOwner(), entries->{
                        if(alarmsLogAdapter != null){
                            alarmsLogAdapter.setDataset(entries);
                        }
                        onLoadData();
                        Log.i("SPF", "  ..... Loaded " + entries.size() + " entries (#" + entries.hashCode() + ") for " + statsName + " " + period.toString("yyyy-MM-dd HH:mm:ss"));
                    });
                } else {
                    period = DatePeriod.getPeriod(datePeriodTimeUnit, 7*24, position);
                    loadEvents = true;
                    String[] sources = new String[]{"rpm", "oil", "temp"};
                    for (String es : sources) {
                        eventSources += "," + statsSource + "_" + es;
                    }
                }
                break;

        }

        //update the title
        if(statsTitle != null){
            String s = "";
            s += Utils.formatDate(period.fromDate, DATE_PERIOD_DATE_FORMAT);
            s += " to ";
            s += Utils.formatDate(period.toDate, DATE_PERIOD_DATE_FORMAT);
            statsTitle.setText(s);
        }

        if(loadStates){
            Log.i("SPF", "Loading states for " + statsName);
            dpinterval = 5*60;
            erServiceModel.getStates(stateSource, statsName, period.fromDate, period.toDate, dpinterval).observe(getViewLifecycleOwner(), states->{
                if(stateLogAdapter != null){
                    stateLogAdapter.setDataset(states);
                } else if(chart != null) {
                    initGraph(states);
                }

                onLoadData();
                Log.i("SPF", " ...... Loaded " + states.size() + " states for " + statsName);
            });
        } //end load states

        if(loadEvents){
            Log.i("SPF", "Loading events for " + statsName);
            erServiceModel.getEvents(eventSources, EngineRoomEvent.ALL_TYPES, period.fromDate, period.toDate, 0).observe(getViewLifecycleOwner(), events ->{
                if (eventLogAdapter != null) {
                    eventLogAdapter.setDataset(events);
                }
                onLoadData();
                Log.i("SPF", "  ..... Loaded " + events.size() + " events (#" + events.hashCode() + ") for " + statsName + " " + period.toString("yyyy-MM-dd HH:mm:ss"));
            });
        }

        //clear away previous data
        if(eventLogAdapter != null){
            eventLogAdapter.clear();
        }
        if(stateLogAdapter != null){
            stateLogAdapter.clear();
        }

        if(chart != null){
            chart.clear();
            chart.setVisibility(View.INVISIBLE);
        }

        if(progressBar != null){
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    private void onLoadData(){
        if(logRecyclerView != null)logRecyclerView.smoothScrollToPosition(0);
        if(progressBar != null){
            progressBar.setVisibility(View.INVISIBLE);
        }
        if(chart != null){
            chart.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void init() {
        erServiceModel = new ViewModelProvider(getActivity()).get(EngineRoomWebserviceModel.class);
        alarmsWebserviceModel = new ViewModelProvider(getActivity()).get(AlarmsWebserviceModel.class);

        statsTitle = contentView.findViewById(R.id.statsTitle);
        progressBar = contentView.findViewById(R.id.progressBar);

        logRecyclerView = contentView.findViewById(R.id.logRecylerView);
        if(logRecyclerView != null){
            logRecyclerView.setHasFixedSize(true);
            logRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            if(statsName.equals("log")) {
                //events log
                if(statsSource.toLowerCase().equals("alarms")){
                    alarmsLogAdapter = new RecyclerViewFragmentAdapter(StatsAlarmItemFragment.class);
                    logRecyclerView.setAdapter(alarmsLogAdapter);
                } else {
                    eventLogAdapter = new RecyclerViewFragmentAdapter(StatsEventItemFragment.class);
                    logRecyclerView.setAdapter(eventLogAdapter);
                }
            } else {
                //state log
                //stateLogAdapter = new RecyclerViewFragmentAdapter<>(StatsStateItemFragment.class);
                //logRecyclerView.setAdapter(stateLogAdapter);
            }
        }

        //graph
        chart = contentView.findViewById(R.id.chart1);
        if(chart != null){
            //state graph
            chart.setPinchZoom(true);
            chart.setOnChartValueSelectedListener(this);

            dateXAxisFormatter = new StatsDateAxisValueFormatter(chart);
            XAxis xAxis = chart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

            //xAxis.setGranularity(1f); // only intervals of 1 day
            xAxis.setDrawGridLines(false);
            // vertical grid lines
            //xAxis.enableGridDashedLine(10f, 10f, 0f);
            xAxis.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.lightGrey));
            xAxis.setValueFormatter(dateXAxisFormatter);

            YAxis yAxis;
            yAxis = chart.getAxisLeft();
            chart.getAxisRight().setEnabled(false);

            // horizontal grid lines
            //yAxis.enableGridDashedLine(10f, 10f, 0f);
            yAxis.setDrawGridLines(false);
            yAxis.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.lightGrey));

            chart.getDescription().setEnabled(false);
            chart.getLegend().setEnabled(false);
            chart.setNoDataText("");

            View valueBox = contentView.findViewById(R.id.valueBox);
            valueBox.setOnClickListener(view -> {
                    chart.highlightValues(null);
                    onNothingSelected();
                });
        }


        prevNextFragment = (StatsPrevNextFragment)getChildFragmentManager().findFragmentById(R.id.prevNextStats);
        if(prevNextFragment != null){
            prevNextFragment.observe(o -> {
                loadData();
            });

            loadData();
        }
    }

    @Override
    public void onPageSelected() {
        super.onPageSelected();
        if(stateLogAdapter != null){
            //logRecyclerView.removeAllViewsInLayout();
            //stateLogAdapter.notifyItemRangeInserted(0, stateLogAdapter.getItemCount());

            Log.i("SPF", "onPageSelected ... stats adapater for " + statsName + ": " + stateLogAdapter.hashCode() + ", log recycler view " + logRecyclerView.hashCode());
        }
    }

    private void initGraph(EngineRoomStates states){
        states.sortEarliestFirst();
        ArrayList<Entry> values = new ArrayList<>();
        long intervalCount = period.getDuration(TimeUnit.SECONDS) / dpinterval;
        dateXAxisFormatter.secondsInterval = dpinterval;
        dateXAxisFormatter.secondsOffset = (int)(period.fromDate.getTimeInMillis() / 1000);
        for(EngineRoomState state : states){
            long seconds = state.getCreated().getTimeInMillis() / 1000;
            float x = (float)(seconds - dateXAxisFormatter.secondsOffset) / (float)dpinterval;
            Entry entry = new Entry(x, state.getStateAsFloat(), state);
            values.add(entry);
        }

        LineDataSet set1 = new LineDataSet(values, statsName);

        //set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        //set1.setCubicIntensity(0.2f);
        set1.setMode(LineDataSet.Mode.LINEAR);
        set1.setDrawFilled(false);
        set1.setDrawCircles(false);
        set1.setLineWidth(1.8f);
        set1.setHighLightColor(Color.rgb(0, 255, 0));
        set1.setColor(Color.WHITE);
        set1.setFillAlpha(100);
        set1.setDrawHorizontalHighlightIndicator(true);
        set1.setDrawVerticalHighlightIndicator(true);

        set1.setFillFormatter(new IFillFormatter() {
            @Override
            public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                return chart.getAxisLeft().getAxisMinimum();
            }
        });

        // create a data object with the data sets
        LineData data = new LineData(set1);
        //data.setValueTypeface(tfLight);
        data.setValueTextSize(9f);
        data.setDrawValues(false);

        // set data
        chart.setData(data);

        //modify appearance
        YAxis yAxis = chart.getAxisLeft();
        XAxis xAxis = chart.getXAxis();
        xAxis.setAxisMinimum(0);
        xAxis.setAxisMaximum(intervalCount);
        xAxis.setLabelCount(9, true);
        dateXAxisFormatter.secondsInterval = dpinterval;

        switch(statsName){
            case "Temperature":
            case "Temperature Average":
            case "Percent Full":
                yAxis.setAxisMinimum(0f);
                yAxis.setAxisMaximum(100f);
                break;

            case "RPM":
            case "RPM Average":
                yAxis.setAxisMinimum(250f);
                yAxis.setAxisMaximum(2000f);
                break;
        }

        //draw
        onNothingSelected();
        chart.fitScreen();
        chart.invalidate();
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        if(chart != null){
            TextView tvX = contentView.findViewById(R.id.xValue);
            TextView tvY = contentView.findViewById(R.id.yValue);

            View valueBox = contentView.findViewById(R.id.valueBox);
            valueBox.setVisibility(View.VISIBLE);

            EngineRoomState state = (EngineRoomState)e.getData();
            tvX.setText(Utils.formatDate(state.getCreated(), "dd MMM HH:mm:ss"));
            tvY.setText(String.format("%.1f", state.getStateAsFloat()) + "\u2103");

        }
    }

    @Override
    public void onNothingSelected() {
        View valueBox = contentView.findViewById(R.id.valueBox);
        valueBox.setVisibility(View.INVISIBLE);
    }
}
