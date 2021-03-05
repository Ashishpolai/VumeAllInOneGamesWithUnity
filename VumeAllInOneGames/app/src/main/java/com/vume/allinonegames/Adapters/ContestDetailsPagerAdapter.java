package com.vume.allinonegames.Adapters;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.vume.allinonegames.Fragment.ContestDetailsFragment;
import com.vume.allinonegames.R;

public class ContestDetailsPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 2;
    private String tabTitles[] = new String[2];
    private Context context;

    public ContestDetailsPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;

        tabTitles[0] = context.getResources().getString(R.string.contestdetails_tabname);
        tabTitles[1] = context.getResources().getString(R.string.leaderboard_tabname);
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        return ContestDetailsFragment.newInstance(position + 1);

    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}