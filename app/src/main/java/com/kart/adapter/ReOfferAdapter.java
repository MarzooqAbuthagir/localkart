package com.kart.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.kart.R;
import com.kart.model.AddOfferData;

import java.util.List;

public class ReOfferAdapter extends RecyclerView.Adapter<ReOfferAdapter.ViewHolder> {
    private Context con;
    private List<OfferData> arrayList;

    public ReOfferAdapter(Context context, List<OfferData> listOfOffer) {
        super();
        con = context;
        arrayList = listOfOffer;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.offer_list_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textHeading.setText(arrayList.get(position).getHeading());
        holder.textDescription.setText(arrayList.get(position).getDesc());


        if (arrayList.get(position).getBitmap() != null )
            holder.imageView.setImageBitmap(arrayList.get(position).getBitmap());
        else
            Glide.with(con).load(arrayList.get(position).getImage())
                    .diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textHeading, textDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
            textHeading = itemView.findViewById(R.id.tv_heading);
            textDescription = itemView.findViewById(R.id.tv_desc);
        }
    }
}
