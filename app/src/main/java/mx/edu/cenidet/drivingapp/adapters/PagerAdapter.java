package mx.edu.cenidet.drivingapp.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import mx.edu.cenidet.drivingapp.fragments.AlertsFragment;
import mx.edu.cenidet.drivingapp.fragments.ZoneFragment;
import mx.edu.cenidet.drivingapp.fragments.HomeFragment;
import mx.edu.cenidet.drivingapp.fragments.MyCampusFragment;
import mx.edu.cenidet.drivingapp.fragments.SpeedFragment;

/**
 * Created by Cipriano on 2/14/2018.
 */

public class PagerAdapter extends FragmentStatePagerAdapter {
    private int numberOfTsb;
    public PagerAdapter(FragmentManager fm, int numberOfTsb) {
        super(fm);
        this.numberOfTsb = numberOfTsb;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new HomeFragment();
            case 1:
                return new SpeedFragment();
            case 2:
                return new ZoneFragment();
            case 3:
                return new AlertsFragment();
            case 4:
                return new MyCampusFragment();
            /*case 5:
                return new MyLocationFragment();*/
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numberOfTsb;
    }
}
