package com.kart.adapter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.kart.R;
import com.kart.model.ShopBanner;
import com.kart.model.SilderData;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

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
        System.out.println("image path " + silder.getImageUrl());

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
