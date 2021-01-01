package net.chetch.engineroom;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

abstract public class ViewPageFragment extends Fragment implements IUIUpdatable {
    public String tabKey = null;
    public View.OnClickListener clickListener;
    ViewGroup contentView;
    protected List<IUIUpdatable> fragments2update = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(tabKey == null){
            tabKey = savedInstanceState.getString("tabKey");
        }

        int layout = getResources().getIdentifier(getLayoutNameForTab(), "layout", getActivity().getPackageName());

        try {
            contentView = (ViewGroup) inflater.inflate(
                    layout, container, false);

            if(clickListener != null){
                setClickListener(clickListener);
            }
        } catch (Exception e){
            Log.e("VPF", e.getMessage());
        }

        init();

        return contentView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("tabKey", tabKey);
    }

    public void setClickListener(View.OnClickListener clickListener){
        this.clickListener = clickListener;
        if(contentView != null)contentView.setOnClickListener(this.clickListener);
    }

    abstract protected String getLayoutNameForTab();

    abstract protected void init();

    public void updateUI(){
        for(IUIUpdatable u : fragments2update){
            u.updateUI();
        }
    }

    public void onPageSelected(){

    }
}
