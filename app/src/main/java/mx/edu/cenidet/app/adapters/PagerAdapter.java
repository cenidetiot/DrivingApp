package mx.edu.cenidet.app.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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

public class PagerAdapter extends FragmentStatePagerAdapter {
    private int numberOfTsb;
    private int position;
    public PagerAdapter(FragmentManager fm, int numberOfTsb) {
        super(fm);
        this.numberOfTsb = numberOfTsb;
    }
    /*public PagerAdapter(FragmentManager fm,int numberOfTsb, int position){
        super(fm);
        this.numberOfTsb = numberOfTsb;
        this.position = position;
    }*/

    @Override
    public Fragment getItem(int position) {
        Log.i("ADAPTER: ", "POSITION: -------------------"+(position));
        switch (position){
            /*case 0:
                return new HomeFragment();
            case 1:
                return new SpeedFragment();
            case 2:
                return new ZoneFragment();
            case 3:
                return new AlertsFragment();
            case 4:
                return new MyCampusFragment();
            default:
                return null;*/
            case 0:
                return new HomeFragment();
            case 1:
                return new ZoneFragment();
            case 2:
                return new AlertsFragment();
            case 3:
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
