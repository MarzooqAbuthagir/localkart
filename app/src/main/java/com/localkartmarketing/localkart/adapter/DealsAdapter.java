package com.localkartmarketing.localkart.adapter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.localkartmarketing.localkart.R;
import com.localkartmarketing.localkart.activity.DealsMoreDetailsActivity;
import com.localkartmarketing.localkart.model.DealsOfferData;
import com.localkartmarketing.localkart.support.Utils;
import com.localkartmarketing.localkart.support.tooltip.SimpleTooltip;

import java.util.List;

public class DealsAdapter extends RecyclerView.Adapter<DealsAdapter.ViewHolder> {
    private Context con;
    private List<DealsOfferData> arrayList;
    private String identity;
    private int tabPos;
    private double latitude;
    private double longitude;
    private String constPostType;

    private OnItemClickListener mItemClickListener;

    public DealsAdapter(Context context, List<DealsOfferData> directoryDataList, String strType, int i, double latitude, double longitude, String constPostType) {
        this.con = context;
        this.arrayList = directoryDataList;
        this.identity = strType;
        this.tabPos = i;
        this.latitude = latitude;
        this.longitude = longitude;
        this.constPostType = constPostType;
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    @NonNull
    @Override
    public DealsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.deals_list_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final DealsAdapter.ViewHolder holder, final int position) {
        if (tabPos == 0) {
            holder.tvDate.setText(arrayList.get(position).getFromDate() + " To " + arrayList.get(position).getToDate());
        } else {
            holder.tvDate.setText(arrayList.get(position).getFestivalName());
        }
        holder.tvShopName.setText(arrayList.get(position).getName());
        holder.tvShopDesc.setText(arrayList.get(position).getDescription());
        String suffixTxt;
        if (arrayList.get(position).getOfferCount().equalsIgnoreCase("1")) {
            suffixTxt = constPostType.equalsIgnoreCase("JOB OPENING") ? " Job" : " Deal";
        } else {
            suffixTxt = constPostType.equalsIgnoreCase("JOB OPENING") ? " Jobs" : " Deals";
        }
        holder.tvOfferCount.setText("+" + arrayList.get(position).getOfferCount() + suffixTxt);

        if(constPostType.equalsIgnoreCase("JOB OPENING") || constPostType.equalsIgnoreCase("MEGASALES")){
            holder.ivViewCount.setVisibility(View.GONE);
            holder.tvViewCount.setVisibility(View.GONE);
        } else {
            holder.ivViewCount.setVisibility(View.VISIBLE);
            holder.tvViewCount.setVisibility(View.VISIBLE);

            holder.tvViewCount.setText(arrayList.get(position).getViewCount());
        }

        holder.tvDistance.setText(arrayList.get(position).getDistance());
        Glide.with(con).load(arrayList.get(position).getLogo())
                .placeholder(R.mipmap.ic_launcher_round)
//                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.ivShopLogo);

        if (arrayList.get(position).getAccessOptions().getKey().equalsIgnoreCase("Phone") ||
                arrayList.get(position).getAccessOptions().getKey().equalsIgnoreCase("Mobile") ||
                arrayList.get(position).getAccessOptions().getKey().equalsIgnoreCase("Alternate Number") ||
                arrayList.get(position).getAccessOptions().getKey().equalsIgnoreCase("COD")) {
//            holder.tvAccessOption.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_call, 0, 0, 0);
//            for (Drawable drawable : holder.tvAccessOption.getCompoundDrawables()) {
//                if (drawable != null) {
//                    drawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(holder.tvAccessOption.getContext(), R.color.bottom_box_color), PorterDuff.Mode.SRC_IN));
//                }
//            }
            holder.ivAccessOption.setImageResource(R.drawable.ic_call);
        } else if (arrayList.get(position).getAccessOptions().getKey().equalsIgnoreCase("WhatsApp")) {
//            holder.tvAccessOption.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_whatsapp, 0, 0, 0);
//            for (Drawable drawable : holder.tvAccessOption.getCompoundDrawables()) {
//                if (drawable != null) {
//                    drawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(holder.tvAccessOption.getContext(), R.color.bottom_box_color), PorterDuff.Mode.SRC_IN));
//                }
//            }
            holder.ivAccessOption.setImageResource(R.drawable.ic_whatsapp);
        } else if (arrayList.get(position).getAccessOptions().getKey().equalsIgnoreCase("Email")) {
//            holder.tvAccessOption.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_email, 0, 0, 0);
//            for (Drawable drawable : holder.tvAccessOption.getCompoundDrawables()) {
//                if (drawable != null) {
//                    drawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(holder.tvAccessOption.getContext(), R.color.bottom_box_color), PorterDuff.Mode.SRC_IN));
//                }
//            }
            holder.ivAccessOption.setImageResource(R.drawable.ic_email);
        } else if (arrayList.get(position).getAccessOptions().getKey().equalsIgnoreCase("Website")) {
//            holder.tvAccessOption.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_website, 0, 0, 0);
//            for (Drawable drawable : holder.tvAccessOption.getCompoundDrawables()) {
//                if (drawable != null) {
//                    drawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(holder.tvAccessOption.getContext(), R.color.bottom_box_color), PorterDuff.Mode.SRC_IN));
//                }
//            }
            holder.ivAccessOption.setImageResource(R.drawable.ic_website);
        } else if (arrayList.get(position).getAccessOptions().getKey().equalsIgnoreCase("Facebook")) {
//            holder.tvAccessOption.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_facebook, 0, 0, 0);
//            for (Drawable drawable : holder.tvAccessOption.getCompoundDrawables()) {
//                if (drawable != null) {
//                    drawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(holder.tvAccessOption.getContext(), R.color.bottom_box_color), PorterDuff.Mode.SRC_IN));
//                }
//            }
            holder.ivAccessOption.setImageResource(R.drawable.ic_facebook);
        } else if (arrayList.get(position).getAccessOptions().getKey().equalsIgnoreCase("Digital VCard")) {
//            holder.tvAccessOption.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_vcard, 0, 0, 0);
//            for (Drawable drawable : holder.tvAccessOption.getCompoundDrawables()) {
//                if (drawable != null) {
//                    drawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(holder.tvAccessOption.getContext(), R.color.bottom_box_color), PorterDuff.Mode.SRC_IN));
//                }
//            }
            holder.ivAccessOption.setImageResource(R.drawable.ic_vcard);
        }

        if (arrayList.get(position).getAccessOptions().getKey().equalsIgnoreCase("Phone") ||
                arrayList.get(position).getAccessOptions().getKey().equalsIgnoreCase("Mobile") ||
                arrayList.get(position).getAccessOptions().getKey().equalsIgnoreCase("Alternate Number")) {
            holder.tvAccessOption.setText("Call");
        } else {
            holder.tvAccessOption.setText(arrayList.get(position).getAccessOptions().getKey());
        }
        holder.layAccessOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (arrayList.get(position).getAccessOptions().getKey()) {
                    case "Website":
                    case "Facebook":
                    case "Digital VCard":
                        Uri uri = Uri.parse("googlechrome://navigate?url=" + arrayList.get(position).getAccessOptions().getValue());
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setPackage("com.android.chrome");
                        try {
                            con.startActivity(intent);
                        } catch (ActivityNotFoundException ex) {
                            // Chrome browser presumably not installed so allow user to choose instead
                            intent.setPackage(null);
                            con.startActivity(intent);
                        }
                        break;

                    case "WhatsApp":
                        String url = "https://api.whatsapp.com/send?phone=+91" + arrayList.get(position).getAccessOptions().getValue();
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        con.startActivity(i);
                        break;

                    case "Phone":
                    case "Mobile":
                    case "Alternate Number":
                    case "COD":
                        String telPhone = "tel:" + arrayList.get(position).getAccessOptions().getValue();
                        Intent callIntent = new Intent(Intent.ACTION_DIAL);
                        callIntent.setData(Uri.parse(telPhone));
                        con.startActivity(callIntent);
                        break;

                    case "Email":
                        try {
//                            To open email app through intent
//                            Intent emailIntent=Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_EMAIL);
//                            emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            con.startActivity(emailIntent);
                            Intent emailIntent = new Intent(Intent.ACTION_SENDTO,
                                    Uri.fromParts("mailto", arrayList.get(position).getAccessOptions().getValue(), null));
                            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
                            emailIntent.putExtra(Intent.EXTRA_TEXT, "");
                            con.startActivity(Intent.createChooser(emailIntent, "Choose an email client"));
                        } catch (android.content.ActivityNotFoundException e) {
                            System.out.println("There is no email client installed.");
                        }
                        break;
                }
            }
        });

        holder.layMoreDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(con, DealsMoreDetailsActivity.class);
                intent.putExtra("key", identity);
                intent.putExtra("postIndexId", arrayList.get(position).getPostIndexId());
                intent.putExtra("shopIndexId", arrayList.get(position).getShopIndexId());
                intent.putExtra("type", arrayList.get(position).getType());
                intent.putExtra("latitude", String.valueOf(latitude));
                intent.putExtra("longitude", String.valueOf(longitude));
                intent.putExtra("shopLatitude", arrayList.get(position).getLatitude());
                intent.putExtra("shopLongitude", arrayList.get(position).getLongitude());
                intent.putExtra("isSubscribed", arrayList.get(position).getIsSubscribed());
                intent.putExtra("constPostType", constPostType);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                con.startActivity(intent);
            }
        });

        holder.layDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String geoUri = "http://maps.google.com/maps?q=loc:" + arrayList.get(position).getLatitude() + "," + arrayList.get(position).getLongitude();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setPackage("com.android.chrome");
                try {
                    con.startActivity(intent);
                } catch (ActivityNotFoundException ex) {
                    // Chrome browser presumably not installed so allow user to choose instead
                    intent.setPackage(null);
                    con.startActivity(intent);
                }
            }
        });

        holder.tvShopDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SimpleTooltip.Builder(con)
                        .anchorView(view)
                        .text(arrayList.get(position).getDescription())
                        .gravity(Gravity.TOP)
                        .animated(true)
                        .textColor(ContextCompat.getColor(con, R.color.black))
                        .transparentOverlay(true)
                        .build()
                        .show();
            }
        });

        if (arrayList.get(position).getIsSubscribed().equalsIgnoreCase("0")) {
            holder.ivNotify.setBackgroundResource(R.drawable.ic_bell);
        } else {
            holder.ivNotify.setBackgroundResource(R.drawable.ic_bell_subscribe);
        }
        holder.layNotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(view, holder.getAdapterPosition());
                }
            }
        });

        holder.layShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String shareMessage = "";
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
//                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name");
                    if (arrayList.get(position).getOfferCount().equalsIgnoreCase("1"))
                        shareMessage = "Valid From " + arrayList.get(position).getFromDate() + " To " + arrayList.get(position).getToDate() + "\n\n" + arrayList.get(position).getName() + " \n\n" + arrayList.get(position).getOfferHeading() + " \n\n" + arrayList.get(position).getOffferDesc() + " \n\nDownload LocalKart App Now ";
                    else {
                        int count = Integer.parseInt(arrayList.get(position).getOfferCount()) - 1;
                        shareMessage = "Valid From " + arrayList.get(position).getFromDate() + " To " + arrayList.get(position).getToDate() + "\n\n" + arrayList.get(position).getName() + " \n\n" + arrayList.get(position).getOfferHeading() + " \n\n" + arrayList.get(position).getOffferDesc() + "\n\nand " + count + " more deals" + " \n\nDownload LocalKart App Now ";
                    }
                    shareMessage = shareMessage + Utils.shareUrl;
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    con.startActivity(Intent.createChooser(shareIntent, "choose one"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvShopName;
        ImageView ivShare, ivNotify, ivAccessOption, ivViewCount;
        ImageView ivShopLogo;
        TextView tvShopDesc;
        TextView tvDistance;
        TextView tvOfferCount;
        TextView tvAccessOption;
        TextView tvViewCount;
        LinearLayout layAccessOption;
        LinearLayout layMoreDetails;
        LinearLayout layDirection;
        LinearLayout layNotify, layShare;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tv_date);
            ivNotify = itemView.findViewById(R.id.iv_notify);
            ivShare = itemView.findViewById(R.id.iv_share);
            tvShopName = itemView.findViewById(R.id.tv_shop_name);
            ivShopLogo = itemView.findViewById(R.id.iv_shop_logo);
            tvShopDesc = itemView.findViewById(R.id.tv_shop_desc);
            tvOfferCount = itemView.findViewById(R.id.tv_offer_count);
            tvDistance = itemView.findViewById(R.id.tv_distance);
            tvAccessOption = itemView.findViewById(R.id.tv_access_option);
            layAccessOption = itemView.findViewById(R.id.lay_access_option);
            layMoreDetails = itemView.findViewById(R.id.lay_more_details);
            layDirection = itemView.findViewById(R.id.lay_direction);
            ivAccessOption = itemView.findViewById(R.id.iv_access_option);
            layNotify = itemView.findViewById(R.id.lay_notify);
            layShare = itemView.findViewById(R.id.lay_share);
            ivViewCount = itemView.findViewById(R.id.iv_view_count);
            tvViewCount = itemView.findViewById(R.id.tv_view_count);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}