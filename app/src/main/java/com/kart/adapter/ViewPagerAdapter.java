package com.kart.adapter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.kart.R;
import com.kart.model.SilderData;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class ViewPagerAdapter extends PagerAdapter {

    private Context context;
    private List<SilderData> silders;

    public ViewPagerAdapter(List<SilderData> silder, Context context) {
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

        final SilderData silder = silders.get(position);

        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
//        System.out.println("image path " + silder.getImage());

        MultiTransformation multiLeft = new MultiTransformation(
                new CenterCrop(),
                new RoundedCornersTransformation(50, 0, RoundedCornersTransformation.CornerType.BOTTOM));

        Glide.with(context).load(silder.getImage())
                .apply(RequestOptions.bitmapTransform(multiLeft))
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (silder.getActionType()) {
                    case "URL":
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(silder.getDataLink()));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setPackage("com.android.chrome");
                        try {
                            context.startActivity(intent);
                        } catch (ActivityNotFoundException ex) {
                            // Chrome browser presumably not installed so allow user to choose instead
                            intent.setPackage(null);
                            context.startActivity(intent);
                        }
                        break;
                    case "Phone":
                        String telPhone = "tel:" + silder.getDataLink();
                        Intent callIntent = new Intent(Intent.ACTION_DIAL);
                        callIntent.setData(Uri.parse(telPhone));
                        context.startActivity(callIntent);
                        break;
                }
            }
        });

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