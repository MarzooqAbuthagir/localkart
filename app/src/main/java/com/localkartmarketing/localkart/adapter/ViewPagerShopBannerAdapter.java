package com.localkartmarketing.localkart.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.localkartmarketing.localkart.R;
import com.localkartmarketing.localkart.model.ShopBanner;

import java.util.List;

public class ViewPagerShopBannerAdapter extends PagerAdapter {

    private Context context;
    private List<ShopBanner> silders;

    public ViewPagerShopBannerAdapter(List<ShopBanner> silder, Context context) {
        this.silders = silder;
        this.context = context;
    }

    @Override
    public int getCount() {
        return silders.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.custom_layout, null);

        final ShopBanner silder = silders.get(position);

        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
//        System.out.println("image path " + silder.getImageUrl());

        Glide.with(context).load(silder.getImageUrl())
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);

        ViewPager vp = (ViewPager) container;
        vp.addView(view, 0);
        return view;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        ViewPager vp = (ViewPager) container;
        View view = (View) object;
        vp.removeView(view);

    }
}
