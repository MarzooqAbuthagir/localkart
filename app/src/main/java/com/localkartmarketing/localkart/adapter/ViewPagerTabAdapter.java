package com.localkartmarketing.localkart.adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.localkartmarketing.localkart.activity.DirectoryActivity;
import com.localkartmarketing.localkart.activity.FestivalActivity;
import com.localkartmarketing.localkart.activity.MegaSalesActivity;
import com.localkartmarketing.localkart.activity.TodayActivity;
import com.localkartmarketing.localkart.activity.WeeklyActivity;

public class ViewPagerTabAdapter extends FragmentStatePagerAdapter {

    //integer to count number of tabs
    int tabCount;
    String type;
    String catId;
    String subcatId;
    String subcatName;
    String megaSalesIndexId;
    String offerTitle;

    //Constructor to the class
    public ViewPagerTabAdapter(FragmentManager fm, int tabCount, String type, String catId, String subcatId, String subcategoryName, String megaSalesIndexId, String offerTitle) {
        super(fm);
        //Initializing tab count
        this.tabCount = tabCount;
        this.type = type;
        this.catId = catId;
        this.subcatId = subcatId;
        this.subcatName = subcategoryName;
        this.megaSalesIndexId = megaSalesIndexId;
        this.offerTitle = offerTitle;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        //Returning the current tabs
        if (!offerTitle.isEmpty()) {
            switch (position) {
                case 0:
                    MegaSalesActivity tab = new MegaSalesActivity();
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("type", type);
                    bundle1.putString("catId", catId);
                    bundle1.putString("subcatId", subcatId);
                    bundle1.putString("subcatName", subcatName);
                    bundle1.putString("megaSalesIndexId", megaSalesIndexId);
                    bundle1.putString("offerTitle", offerTitle);
                    tab.setArguments(bundle1);
                    return tab;

                case 1:
                    DirectoryActivity tab1 = new DirectoryActivity();
                    Bundle bundle = new Bundle();
                    bundle.putString("type", type);
                    bundle.putString("catId", catId);
                    bundle.putString("subcatId", subcatId);
                    bundle.putString("subcatName", subcatName);
                    tab1.setArguments(bundle);
                    return tab1;
                case 2:
                    TodayActivity tab3 = new TodayActivity();
                    Bundle bundle3 = new Bundle();
                    bundle3.putString("type", type);
                    bundle3.putString("catId", catId);
                    bundle3.putString("subcatId", subcatId);
                    bundle3.putString("subcatName", subcatName);
                    tab3.setArguments(bundle3);
                    return tab3;
                case 3:
                    WeeklyActivity tab2 = new WeeklyActivity();
                    Bundle bundle2 = new Bundle();
                    bundle2.putString("type", type);
                    bundle2.putString("catId", catId);
                    bundle2.putString("subcatId", subcatId);
                    bundle2.putString("subcatName", subcatName);
                    tab2.setArguments(bundle2);
                    return tab2;
                case 4:
                    FestivalActivity tab4 = new FestivalActivity();
                    Bundle bundle4 = new Bundle();
                    bundle4.putString("type", type);
                    bundle4.putString("catId", catId);
                    bundle4.putString("subcatId", subcatId);
                    bundle4.putString("subcatName", subcatName);
                    tab4.setArguments(bundle4);
                    return tab4;
                default:
                    return null;
            }
        } else {
            switch (position) {
                case 0:
                    DirectoryActivity tab1 = new DirectoryActivity();
                    Bundle bundle = new Bundle();
                    bundle.putString("type", type);
                    bundle.putString("catId", catId);
                    bundle.putString("subcatId", subcatId);
                    bundle.putString("subcatName", subcatName);
                    tab1.setArguments(bundle);
                    return tab1;
                case 1:
                    TodayActivity tab3 = new TodayActivity();
                    Bundle bundle3 = new Bundle();
                    bundle3.putString("type", type);
                    bundle3.putString("catId", catId);
                    bundle3.putString("subcatId", subcatId);
                    bundle3.putString("subcatName", subcatName);
                    tab3.setArguments(bundle3);
                    return tab3;
                case 2:
                    WeeklyActivity tab2 = new WeeklyActivity();
                    Bundle bundle2 = new Bundle();
                    bundle2.putString("type", type);
                    bundle2.putString("catId", catId);
                    bundle2.putString("subcatId", subcatId);
                    bundle2.putString("subcatName", subcatName);
                    tab2.setArguments(bundle2);
                    return tab2;
                case 3:
                    FestivalActivity tab4 = new FestivalActivity();
                    Bundle bundle4 = new Bundle();
                    bundle4.putString("type", type);
                    bundle4.putString("catId", catId);
                    bundle4.putString("subcatId", subcatId);
                    bundle4.putString("subcatName", subcatName);
                    tab4.setArguments(bundle4);
                    return tab4;
                default:
                    return null;
            }
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
