package net.chetch.engineroom;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import net.chetch.appframework.GenericDialogFragment;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;

public class StatsDialogFragment extends GenericDialogFragment implements View.OnClickListener, IUIUpdatable {

    String dialogTitle;
    LinkedHashMap<String, String> tabMap;
    ViewPager2 viewStatsViewPager;
    ViewPageAdapter viewStatsAdapter;


    public void setTabs(LinkedHashMap<String, String> tabMap){
        this.tabMap = tabMap;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null){
            if(savedInstanceState.containsKey("tabMap")){
                tabMap = (LinkedHashMap<String,String>)savedInstanceState.getSerializable("tabMap");
            }
            if(savedInstanceState.containsKey("dialogTitle")){
                dialogTitle = savedInstanceState.getString("dialogTitle");
            }
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        inflateContentView(R.layout.stats_dialog);

        Dialog dialog = createDialog();

        Log.i("VSD", "Dialog created");
        getChildFragmentManager().getFragments().clear();

        TextView statsDialogTitle = contentView.findViewById(R.id.statsDialogTitle);
        statsDialogTitle.setText(dialogTitle.toUpperCase() + ":");

        viewStatsViewPager = contentView.findViewById(R.id.viewStatsViewPager);
        viewStatsAdapter = new ViewPageAdapter(this, StatsPageFragment.class, tabMap);
        viewStatsAdapter.pageClickListener = this;
        viewStatsViewPager.setAdapter(viewStatsAdapter);
        viewStatsViewPager.setCurrentItem(0, true);
        viewStatsViewPager.getAdapter().notifyDataSetChanged();
        viewStatsViewPager.registerOnPageChangeCallback(new ViewPageChangeCallback(viewStatsAdapter));


        TabLayout tabLayout = contentView.findViewById(R.id.viewStatsTabs);
        new TabLayoutMediator(tabLayout, viewStatsViewPager,
                (tab, position) -> {
                    ArrayList tabKeys = new ArrayList<String>(tabMap.keySet());
                    String title = tabMap.get(tabKeys.get(position));
                    tab.setText(title);
                }
        ).attach();

        View closeBtn = contentView.findViewById(R.id.statsDialogClose);
        closeBtn.setOnClickListener(view -> {
            dismiss();
        });
        setFullScreen(0.9);

        return dialog;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("dialogTitle", dialogTitle);
        outState.putSerializable("tabMap", tabMap);
    }

    @Override
    public void onClick(View view) {

        //dismiss();
    }

    @Override
    public void updateUI() {

    }
}
