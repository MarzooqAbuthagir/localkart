package com.kart.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.kart.R;
import com.kart.activity.PreviewOfferActivity;
import com.kart.model.AddOfferData;

import java.util.List;

public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.ViewHolder> {
    private Context con;
    private List<AddOfferData> arrayList;
    int imgShow;
    boolean isClickable;
    private String key;
    private String postId;
    private String shopId;
    private String shopType;

    public OfferAdapter(Context context, List<AddOfferData> listOfOffer, int i, boolean isClick, String keyIntent, String postId, String shopId, String shopType ) {
        super();
        con = context;
        arrayList = listOfOffer;
        imgShow = i;
        isClickable = isClick;
        key = keyIntent;
        this.postId =  postId;
        this.shopId = shopId;
        this.shopType = shopType;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.offer_list_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.textHeading.setText(arrayList.get(position).getHeading());
        holder.textDescription.setText(arrayList.get(position).getDesc());

        if (imgShow == 1)
            holder.imageView.setImageBitmap(arrayList.get(position).getBitmap());
        else {
            holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Glide.with(con).load(arrayList.get(position).getImage())
                    .placeholder(R.mipmap.ic_launcher_round)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.imageView);
        }
        holder.layMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isClickable) {
                    String dealCount = String.valueOf(position);
                    Intent intent = new Intent(con, PreviewOfferActivity.class);
                    intent.putExtra("key", key);
                    intent.putExtra("dealCount", dealCount);
                    intent.putExtra("shopId", shopId);
                    intent.putExtra("postId", postId);
                    intent.putExtra("shopType", shopType);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    con.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textHeading, textDescription;
        LinearLayout layMain;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            layMain = itemView.findViewById(R.id.lay_main);
            imageView = itemView.findViewById(R.id.image_view);
            textHeading = itemView.findViewById(R.id.tv_heading);
            textDescription = itemView.findViewById(R.id.tv_desc);
        }
    }
}
