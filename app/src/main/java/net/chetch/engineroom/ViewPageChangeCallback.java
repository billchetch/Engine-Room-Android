package net.chetch.engineroom;

import androidx.viewpager2.widget.ViewPager2;

public class ViewPageChangeCallback extends ViewPager2.OnPageChangeCallback {
    ViewPageAdapter viewPageAdapter;

    public ViewPageChangeCallback(ViewPageAdapter vpa){
        viewPageAdapter = vpa;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        super.onPageScrolled(position, positionOffset, positionOffsetPixels);
    }

    @Override
    public void onPageSelected(int position) {
        super.onPageSelected(position);

        ViewPageFragment vpf = (ViewPageFragment)viewPageAdapter.getFragment(position);
        if(vpf != null)vpf.onPageSelected();
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        super.onPageScrollStateChanged(state);
    }
}
