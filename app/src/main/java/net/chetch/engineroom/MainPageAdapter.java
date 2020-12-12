package net.chetch.engineroom;

import net.chetch.engineroom.data.Engine;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class MainPageAdapter extends FragmentStateAdapter {

    LinkedHashMap<String, String> tabMap;
    ArrayList<String> tabKeys;

    public MainPageAdapter(@NonNull FragmentActivity fragmentActivity, LinkedHashMap<String,String> tabMap) {
        super(fragmentActivity);
        this.tabMap = tabMap;
        tabKeys = new ArrayList<String>(tabMap.keySet());
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        MainPageFragment mpf = new MainPageFragment();
        mpf.tabKey = tabKeys.get(position);
        return mpf;
    }

    @Override
    public int getItemCount() {
        return tabKeys.size();
    }
}
