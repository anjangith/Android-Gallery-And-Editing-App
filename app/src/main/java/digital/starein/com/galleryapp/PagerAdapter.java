package digital.starein.com.galleryapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


public class PagerAdapter extends FragmentPagerAdapter {
    private static int TAB_COUNT=4;

    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "FAV";

            case 1:
                return "PHOTOS";

            case 2:
                return "ALBUMS";

            case 3:
                return "VIDEOS";

        }
        return super.getPageTitle(position);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return Tab1.getInstance();

            case 1:
                return Tab2.getInstance();

            case 2:
                return Tab3.getInstance();

            case 3:
                return Tab4.getInstance();
        }
        return null;
    }

    @Override
    public int getCount() {
        return TAB_COUNT;
    }
}
