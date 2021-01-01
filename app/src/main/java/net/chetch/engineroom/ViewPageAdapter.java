package net.chetch.engineroom;

import android.util.Log;
import android.view.View;

import net.chetch.engineroom.data.Engine;
import net.chetch.webservices.DataObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.adapter.FragmentViewHolder;

public class ViewPageAdapter <T extends ViewPageFragment> extends FragmentStateAdapter {

    Class<T> pageFragmentClass;
    LinkedHashMap<String, String> tabMap;
    ArrayList<String> tabKeys;
    public View.OnClickListener pageClickListener;
    FragmentManager fragmentManager;

    public ViewPageAdapter(@NonNull FragmentActivity fragmentActivity, Class<T> pageFragmentClass, LinkedHashMap<String,String> tabMap) {
        super(fragmentActivity);
        this.pageFragmentClass = pageFragmentClass;
        this.tabMap = tabMap;
        tabKeys = new ArrayList<String>(tabMap.keySet());
        fragmentManager = fragmentActivity.getSupportFragmentManager();
    }

    public ViewPageAdapter(@NonNull Fragment fragment, Class<T> pageFragmentClass, LinkedHashMap<String,String> tabMap) {
        super(fragment);
        this.pageFragmentClass = pageFragmentClass;
        this.tabMap = tabMap;
        tabKeys = new ArrayList<String>(tabMap.keySet());
        fragmentManager = fragment.getChildFragmentManager();
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        try {
            T vpf = pageFragmentClass.getDeclaredConstructor().newInstance();
            vpf.tabKey = tabKeys.get(position);
            vpf.clickListener = pageClickListener;
            Log.i("VPA", "Created fragment at position " + position);
            return vpf;
        } catch (Exception e){
            Log.e("VPA", e.getMessage());
            return null;
        }
    }

    public Fragment getFragment(int position){
        Fragment f = fragmentManager.findFragmentByTag("f" + position);
        if(f != null && f instanceof ViewPageFragment){
            return f;
        } else {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull FragmentViewHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);

        T vpf = (T)getFragment(position);
        if(vpf != null) {
            vpf.tabKey = tabKeys.get(position);
            vpf.setClickListener(pageClickListener);
        }
    }

    @Override
    public int getItemCount() {
        return tabKeys.size();
    }


}
