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
import com.localkartmarketing.localkart.activity.DirectoryMoreDetailsActivity;
import com.localkartmarketing.localkart.model.DirectoryData;
import com.localkartmarketing.localkart.support.tooltip.SimpleTooltip;

import java.util.List;

public class DirectoryAdapter extends RecyclerView.Adapter<DirectoryAdapter.ViewHolder> {
    private Context con;
    private List<DirectoryData> arrayList;
    private String identity;
    private double latitude;
    private double longitude;

    private OnItemClickListener mItemClickListener;

    public DirectoryAdapter(Context context, List<DirectoryData> directoryDataList, String strType, double latitude, double longitude) {
        this.con = context;
        this.arrayList = directoryDataList;
        this.identity = strType;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.directory_list_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.tvShopName.setText(arrayList.get(position).getName());
        holder.tvShopDesc.setText(arrayList.get(position).getDescription());
        holder.tvDistance.setText(arrayList.get(position).getDistance());
        holder.tvCall.setText(arrayList.get(position).getAccessOptions().getKey());

        Glide.with(con).load(arrayList.get(position).getLogo())
                .placeholder(R.mipmap.ic_launcher_round)
//                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.ivShopLogo);

        holder.layCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String telPhone = "tel:" + arrayList.get(position).getAccessOptions().getValue();
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse(telPhone));
                con.startActivity(callIntent);
            }
        });

        holder.layMoreDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(con, DirectoryMoreDetailsActivity.class);
                intent.putExtra("key", identity);
                intent.putExtra("shopId", arrayList.get(position).getShopId());
                intent.putExtra("shopType", arrayList.get(position).getShopType());
                intent.putExtra("latitude", String.valueOf(latitude));
                intent.putExtra("longitude", String.valueOf(longitude));
                intent.putExtra("isSubscribed", arrayList.get(position).getIsSubscribed());
                intent.putExtra("constPostType", "DIRECTORY");
                intent.putExtra("shareUrl", arrayList.get(position).getShareUrl());
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
                    String shareMessage = arrayList.get(position).getName() + "\n\nHere is my Digital vCard " + arrayList.get(position).getShareUrl().replaceAll("(?<!(http:|https:))/+", "/");
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
//                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name");
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
        TextView tvShopName;
        ImageView ivShare, ivNotify;
        ImageView ivShopLogo;
        TextView tvShopDesc;
        TextView tvDistance;
        TextView tvCall;
        LinearLayout layCall;
        LinearLayout layShare;
        LinearLayout layMoreDetails;
        LinearLayout layDirection;
        LinearLayout layNotify;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvShopName = itemView.findViewById(R.id.tv_shop_name);
            ivShare = itemView.findViewById(R.id.iv_share);
            ivShopLogo = itemView.findViewById(R.id.iv_shop_logo);
            tvShopDesc = itemView.findViewById(R.id.tv_shop_desc);
            tvDistance = itemView.findViewById(R.id.tv_distance);
            tvCall = itemView.findViewById(R.id.tv_call);
            layCall = itemView.findViewById(R.id.lay_call);
            layMoreDetails = itemView.findViewById(R.id.lay_more_details);
            layDirection = itemView.findViewById(R.id.lay_direction);
            ivNotify = itemView.findViewById(R.id.iv_notify);
            layNotify = itemView.findViewById(R.id.lay_notify);
            layShare = itemView.findViewById(R.id.lay_share);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}
