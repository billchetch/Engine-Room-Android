package net.chetch.engineroom;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import net.chetch.appframework.GenericActivity;
import net.chetch.cmalarms.models.AlarmsMessagingModel;
import net.chetch.engineroom.data.PompaCelup;
import net.chetch.engineroom.models.EngineRoomMessagingModel;
import net.chetch.messaging.MessagingViewModel;
import net.chetch.utilities.Utils;
import net.chetch.webservices.WebserviceViewModel;
import net.chetch.webservices.network.NetworkRepository;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MainActivity extends GenericActivity {
    public enum DisplayOrientation{
        PORTRAIT,
        LANDSCAPE
    }

    static public DisplayOrientation Orientation;

    AlarmsMessagingModel alarmsModel;
    EngineRoomMessagingModel engineRoomModel;
    IndicatorFragment pompaCelupFragment;

    EngineFragment genset1;
    EngineFragment genset2;

    Observer dataLoadProgress  = obj -> {
        WebserviceViewModel.LoadProgress progress = (WebserviceViewModel.LoadProgress) obj;
        try {
            String state = progress.startedLoading ? "Loading" : "Loaded";
            String progressInfo = state + (progress.info == null ? "" : " " + progress.info.toLowerCase());
            Log.i("Main", "in load data progress");
        } catch (Exception e){
            Log.e("Main", "load prigress: " + e.getMessage());
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

        try {
            //String apiBaseURL = "http://192.168.43.123:8001/api/";
            //String apiBaseURL = "http://192.168.0.123:8001/api/";
            String apiBaseURL = "http://192.168.0.150:8001/api/";
            //String apiBaseURL = "http://192.168.1.100:8001/api/";
            //String apiBaseURL = "http://192.168.0.106:8001/api/";
            //String apiBaseURL = "http://192.168.0.52:8001/api/";
            NetworkRepository.getInstance().setAPIBaseURL(apiBaseURL);
        } catch (Exception e) {
            Log.e("MVM", e.getMessage());
            return;
        }

        //now load up
        Log.i("Main", "Calling load data");
        MessagingViewModel.setClientName("AndroidCMEngineRoom");

        alarmsModel = ViewModelProviders.of(this).get(AlarmsMessagingModel.class);

        alarmsModel.getError().observe(this, throwable -> {
            showError(throwable);
        });
        alarmsModel.loadData(dataLoadProgress);

        engineRoomModel = ViewModelProviders.of(this).get(EngineRoomMessagingModel.class);
        engineRoomModel.getError().observe(this, throwable -> {
            showError(throwable);
        });
        engineRoomModel.loadData(dataLoadProgress);
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

        engineRoomModel.getPompaCelup().observe(this, pc->{
            updatePompaCelup(pc);
        });



        pompaCelupFragment = (IndicatorFragment)getSupportFragmentManager().findFragmentById(R.id.pompaCelupFragment);
        startTimer(5);

        //initialise genset fragments
        genset1 = (EngineFragment)getSupportFragmentManager().findFragmentById(R.id.genset1);
        genset1.setEngineID("gs1");
        genset1.setName("Genset 1");
        genset1.setMaxRPM(2200);
        genset1.setRPMThresholds(1500, 1600,1700);
        genset1.setTempThresholds(60, 80);

        genset2 = (EngineFragment)getSupportFragmentManager().findFragmentById(R.id.genset2);
        genset2.setEngineID("gs2");
        genset2.setName("Genset 2");
        genset2.setMaxRPM(2200);
        genset2.setRPMThresholds(1500, 1600,1700);
        genset2.setTempThresholds(60, 80);
    }

    @Override
    protected int onTimer() {
        //PompaCelup pc = engineRoomModel.getPompaCelup().getValue();
        //if(pc != null)updatePompaCelup(pc);

        genset1.updateUI();
        genset2.updateUI();

        return super.onTimer();
    }

    private void updatePompaCelup(PompaCelup pc){
        long duration = pc.getOnDuration();
        String fduration = duration > 0 ? Utils.formatDuration(duration, Utils.DurationFormat.D_H_M_S) : "n/a";
        String details = "";
        if(pc.isOn()){
            details = "Pumping started on  " + Utils.formatDate(pc.getLastOn(), "dd MMM, HH:mm:ss") + " (running time " + fduration + ")";
        } else if(pc.getLastOn() != null){
            details = "Last pumped on " + Utils.formatDate(pc.getLastOn(), "dd MMM, HH:mm:ss") + " (duration " + fduration + ")";
        }
        pompaCelupFragment.update(pc.getState(), details);
    }

    @Override
    public void showError(Throwable t) {
        super.showError(t);
    }
}