package net.chetch.engineroom;

import net.chetch.appframework.controls.PrevNextFragment;

public class StatsPrevNextFragment extends PrevNextFragment {

    @Override
    protected boolean isValidPosition(int position) {
        return position <= 0;
    }

}
