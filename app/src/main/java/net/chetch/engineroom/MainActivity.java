package net.chetch.engineroom;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import net.chetch.appframework.GenericActivity;
import net.chetch.appframework.GenericDialogFragment;
import net.chetch.appframework.IDialogManager;
import net.chetch.cmalarms.AlarmPanelFragment;
import net.chetch.cmalarms.IAlarmPanelListener;
import net.chetch.cmalarms.models.AlarmsMessagingModel;
import net.chetch.cmalarms.models.AlarmsWebserviceModel;
import net.chetch.engineroom.models.EngineRoomMessagingModel;
import net.chetch.engineroom.models.EngineRoomWebserviceModel;
import net.chetch.webservices.WebserviceViewModel;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class MainActivity extends GenericActivity implements IDialogManager, IAlarmPanelListener {

    public enum DisplayOrientation{
        PORTRAIT,
        LANDSCAPE
    }

    static public DisplayOrientation Orientation;

    AlarmsMessagingModel alarmsModel;
    AlarmsWebserviceModel alarmsWebserviceModel;
    EngineRoomMessagingModel engineRoomModel;
    EngineRoomWebserviceModel erServiceModel;

    AlarmPanelFragment alarmPanelFragment;
    ViewPager2 mainViewPager = null;
    ViewPageAdapter mainPageAdapter;

    Observer dataLoadProgress  = obj -> {
        if(obj instanceof WebserviceViewModel.LoadProgress) {
            WebserviceViewModel.LoadProgress progress = (WebserviceViewModel.LoadProgress) obj;
            try {
                String state = progress.startedLoading ? "Loading" : "Loaded";
                String progressInfo = state + (progress.info == null ? "" : " " + progress.info.toLowerCase());
                Log.i("Main", "in load data progress ..." + progressInfo);
            } catch (Exception e) {
                Log.e("Main", "load progress: " + e.getMessage());
            }
        } else if(obj.toString().equals(EngineRoomMessagingModel.CLIENT_NAME)){
            onEngineRoomClientConnected();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Orientation = DisplayOrientation.LANDSCAPE.valueOf(getString(R.string.display_orientation));
        Configuration configuration = getResources().getConfiguration();
        Log.i("Main", "Metrics of width, smallest width, height: " + configuration.screenWidthDp + "," + configuration.smallestScreenWidthDp + "," + configuration.screenHeightDp);
        Log.i("Main", "Creating main activity with orientation " + Orientation);

        //now load up
        Log.i("Main", "Calling load data");

        //Alarms models
        alarmsModel = new ViewModelProvider(this).get(AlarmsMessagingModel.class);
        alarmsModel.getError().observe(this, throwable -> {
            showError(throwable);
        });
        alarmsModel.loadData(dataLoadProgress);

        alarmsWebserviceModel = new ViewModelProvider(this).get(AlarmsWebserviceModel.class);
        alarmsWebserviceModel.getError().observe(this, throwable ->{
            Log.e("Main", throwable.getMessage());
        });
        alarmsWebserviceModel.loadData(dataLoadProgress);

        engineRoomModel = new ViewModelProvider(this).get(EngineRoomMessagingModel.class);
        engineRoomModel.getError().observe(this, throwable -> {
            showError(throwable);
        });

        ConstraintLayout mainLayout = findViewById(R.id.erMainLayout);
        mainLayout.setVisibility(View.INVISIBLE);
        View progressCtn = findViewById(R.id.erProgressCtn);

        engineRoomModel.getMessagingService().observe(this, ms ->{
            switch(ms.state){
                case RESPONDING:
                    mainLayout.setVisibility(View.VISIBLE);
                    progressCtn.setVisibility(View.INVISIBLE);
                    break;
                case NOT_CONNECTED:
                case NOT_FOUND:
                case NOT_RESPONDING:
                    mainLayout.setVisibility(View.INVISIBLE);
                    progressCtn.setVisibility(View.VISIBLE);
                    TextView tv = progressCtn.findViewById(R.id.serviceState);
                    tv.setText("Engine Room service is of state " + ms.state);
                    break;
            }
        });

        engineRoomModel.loadData(dataLoadProgress);

        erServiceModel = new ViewModelProvider(this).get(EngineRoomWebserviceModel.class);
        erServiceModel.getError().observe(this, throwable -> {
            showError(throwable);
        });
        erServiceModel.loadData(dataLoadProgress);

        alarmPanelFragment = (AlarmPanelFragment)getSupportFragmentManager().findFragmentById(R.id.alarmPanelFragment);
        alarmPanelFragment.listener = this;
    }

    private void onEngineRoomClientConnected(){
        Log.i("Main", "on client connected");

        if(mainViewPager == null) {
            //link view pager to tabs
            LinkedHashMap<String, String> tabMap = new LinkedHashMap<>();
            tabMap.put("engines", "Engines");
            tabMap.put("gensets", "Gensets");
            //tabMap.put("diesel_tanks", "Diesel");
            tabMap.put("water_tanks", "Water");
            tabMap.put("misc", "Pumps");

            mainViewPager = findViewById(R.id.viewPager);
            mainPageAdapter = new ViewPageAdapter(this, MainPageFragment.class, tabMap);
            mainViewPager.setAdapter(mainPageAdapter);
            mainViewPager.setCurrentItem(0, true);
            TabLayout tabLayout = findViewById(R.id.tabs);
            new TabLayoutMediator(tabLayout, mainViewPager,
                    (tab, position) -> {
                        ArrayList tabKeys = new ArrayList<String>(tabMap.keySet());
                        String title = tabMap.get(tabKeys.get(position));
                        tab.setText(title);
                    }
            ).attach();

            mainViewPager.registerOnPageChangeCallback(new ViewPageChangeCallback(mainPageAdapter));
        }
    }

    @Override
    protected int onTimer() {
        if(mainViewPager != null) {
            int position = mainViewPager.getCurrentItem();
            ViewPageFragment vpf = (ViewPageFragment) mainPageAdapter.getFragment(position);
            if (vpf != null) {
                vpf.updateUI();
            }
        }
        return super.onTimer();
    }

    @Override
    protected void onStart() {
        super.onStart();

        startTimer(5);
    }

    @Override
    protected void onStop() {
        super.onStop();

        stopTimer();
    }

    @Override
    public void showError(Throwable t) {
        super.showError(t);
    }


    @Override
    public void onDialogPositiveClick(GenericDialogFragment dialog){

    }

    @Override
    public void onViewAlarmsLog(AlarmsWebserviceModel model) {
        StatsDialogFragment statsDialog = new StatsDialogFragment();
        LinkedHashMap<String, String> tabMap = new LinkedHashMap<>();
        tabMap.put("main:alarms:log", "Log");
        statsDialog.setTabs(tabMap);
        statsDialog.show(getSupportFragmentManager(), "StatsDialog");
    }

    @Override
    public void onSilenceAlarmBuzzer(int duration) {

    }
}