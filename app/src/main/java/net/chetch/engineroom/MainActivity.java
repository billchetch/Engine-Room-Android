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
import net.chetch.messaging.ClientConnection;
import net.chetch.webservices.ConnectManager;
import net.chetch.webservices.WebserviceViewModel;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class MainActivity extends GenericActivity implements IDialogManager, IAlarmPanelListener {

    public enum DisplayOrientation{
        PORTRAIT,
        LANDSCAPE
    }

    static public DisplayOrientation Orientation;

    ConnectManager connectManager = new ConnectManager();

    AlarmsMessagingModel alarmsModel;
    AlarmsWebserviceModel alarmsWebserviceModel;
    EngineRoomMessagingModel engineRoomModel;
    EngineRoomWebserviceModel erServiceModel;

    AlarmPanelFragment alarmPanelFragment;
    ViewPager2 mainViewPager = null;
    ViewPageAdapter mainPageAdapter;

    Observer connectProgress  = obj -> {
        if(obj instanceof WebserviceViewModel.LoadProgress) {
            WebserviceViewModel.LoadProgress progress = (WebserviceViewModel.LoadProgress) obj;
            try {
                String state = progress.startedLoading ? "Loading" : "Loaded";
                String progressInfo = state + (progress.info == null ? "" : " " + progress.info.toLowerCase());
                Log.i("Main", "in load data progress ..." + progressInfo);

            } catch (Exception e) {
                Log.e("Main", "load progress: " + e.getMessage());
            }
        } else if(obj instanceof ClientConnection){

        } else if(obj instanceof ConnectManager){
            ConnectManager cm = (ConnectManager)obj;
            ConstraintLayout mainLayout = findViewById(R.id.erMainLayout);
            View progressCtn = findViewById(R.id.erProgressCtn);
            switch(cm.getState()){
                case CONNECT_REQUEST:
                    if(cm.fromError()){
                        setProgressInfo("There was an error ... retrying...");
                    } else {
                        setProgressInfo("Connecting...");
                    }
                    mainLayout.setVisibility(View.INVISIBLE);
                    alarmPanelFragment.getView().setVisibility(View.INVISIBLE);
                    progressCtn.setVisibility(View.VISIBLE);
                    break;

                case RECONNECT_REQUEST:
                    setProgressInfo("Disconnected!... Attempting to reconnect...");
                    mainLayout.setVisibility(View.INVISIBLE);
                    alarmPanelFragment.getView().setVisibility(View.INVISIBLE);
                    progressCtn.setVisibility(View.VISIBLE);
                    break;

                case CONNECTED:
                    mainLayout.setVisibility(View.VISIBLE);
                    alarmPanelFragment.getView().setVisibility(View.VISIBLE);
                    progressCtn.setVisibility(View.INVISIBLE);
                    onEngineRoomClientConnected();
                    break;
            }
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


        //Alarms models
        alarmsModel = new ViewModelProvider(this).get(AlarmsMessagingModel.class);
        alarmsModel.getError().observe(this, throwable -> {
            handleError(throwable, alarmsModel);
        });

        alarmsWebserviceModel = new ViewModelProvider(this).get(AlarmsWebserviceModel.class);
        alarmsWebserviceModel.getError().observe(this, throwable ->{
            handleError(throwable, alarmsWebserviceModel);
            Log.e("Main", throwable.getMessage());
        });

        //Engine room models
        engineRoomModel = new ViewModelProvider(this).get(EngineRoomMessagingModel.class);
        engineRoomModel.getError().observe(this, throwable -> {
            handleError(throwable, engineRoomModel);
        });

        erServiceModel = new ViewModelProvider(this).get(EngineRoomWebserviceModel.class);
        erServiceModel.getError().observe(this, throwable -> {
            handleError(throwable, erServiceModel);
        });

        //Components
        alarmPanelFragment = (AlarmPanelFragment)getSupportFragmentManager().findFragmentById(R.id.alarmPanelFragment);
        alarmPanelFragment.listener = this;

        //load her up
        try {
            alarmsModel.setClientName("ACMCAlarms");
            engineRoomModel.setClientName("ACMCEngineRoom");

            connectManager.addModel(alarmsModel);
            connectManager.addModel(engineRoomModel);
            connectManager.addModel(alarmsWebserviceModel);
            connectManager.addModel(erServiceModel);

            connectManager.requestConnect(connectProgress);
        } catch (Exception e){
            showError(e);
        }

    }

    private void handleError(Throwable t, Object source){

        Log.e("MAIN", t.getClass() + ": " + t.getMessage());
    }

    private void onEngineRoomClientConnected(){
        Log.i("Main", "on engine room client connected");

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
    protected void onRestart() {
        super.onRestart();
        connectManager.resume();
    }

    @Override
    protected void onStop() {
        super.onStop();

        stopTimer();
        connectManager.pause();
    }

    @Override
    public void onDialogPositiveClick(GenericDialogFragment dialog){

    }

    @Override
    public void onViewAlarmsLog(AlarmsWebserviceModel model) {
        StatsDialogFragment statsDialog = new StatsDialogFragment();
        statsDialog.dialogTitle = "Alarms";
        LinkedHashMap<String, String> tabMap = new LinkedHashMap<>();
        tabMap.put("main:alarms:log", "Log");
        statsDialog.setTabs(tabMap);
        statsDialog.show(getSupportFragmentManager(), "StatsDialog");
    }

    @Override
    public void onSilenceAlarmBuzzer(int duration) {

    }
}