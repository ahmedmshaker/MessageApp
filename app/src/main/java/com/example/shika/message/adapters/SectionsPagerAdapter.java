package com.example.shika.message.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.shika.message.R;
import com.example.shika.message.ui.FriendsFragment;
import com.example.shika.message.ui.InboxFragment;
import com.example.shika.message.ui.MainActivity;

import java.util.Locale;

/**
 * A {@link android.support.v4.app.FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private MainActivity mainActivity;

    public SectionsPagerAdapter(MainActivity mainActivity, FragmentManager fm) {
        super(fm);
        this.mainActivity = mainActivity;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
       switch (position){
           case 0:
                return new InboxFragment();
           case 1:
                return new FriendsFragment();
       }
        return null;
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Locale l = Locale.getDefault();
        switch (position) {
            case 0:
                return mainActivity.getString(R.string.title_section1).toUpperCase(l);
            case 1:
                return mainActivity.getString(R.string.title_section2).toUpperCase(l);

        }
        return null;
    }
    public int getIcon(int position){
        switch (position) {
            case 0:
                return R.drawable.ic_tab_inbox;
            case 1:
                return R.drawable.ic_tab_friends;

        }
        return R.drawable.ic_tab_inbox;

    }

}
