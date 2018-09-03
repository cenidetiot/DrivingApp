package mx.edu.cenidet.app.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import mx.edu.cenidet.app.fragments.AlertsFragment;
import mx.edu.cenidet.app.fragments.ZoneFragment;
import mx.edu.cenidet.app.fragments.HomeFragment;
import mx.edu.cenidet.app.fragments.MyCampusFragment;
import mx.edu.cenidet.app.fragments.SpeedFragment;

/**
 * Created by Cipriano on 2/14/2018.
 */

public class PagerAdapter extends FragmentPagerAdapter {
    private int numberOfTsb;
    private int position;

    public PagerAdapter(FragmentManager fm, int numberOfTsb) {
        super(fm);
        this.numberOfTsb = numberOfTsb;
    }

    @Override
    public Fragment getItem(int position) {
        Log.i("ADAPTER: ", "POSITION: -------------------" + (position));
        switch (position) {
            case 0:
                return new ZoneFragment();
            case 1:
                return new AlertsFragment();
            case 2:
                return new MyCampusFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numberOfTsb;
    }

}
