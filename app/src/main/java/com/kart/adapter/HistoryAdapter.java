package com.kart.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.kart.R;
import com.kart.activity.HistoryActivity;
import com.kart.activity.ManageBusinessActivity;
import com.kart.activity.ViewHistoryActivity;
import com.kart.model.AddOfferData;
import com.kart.model.HistoryData;
import com.kart.support.tooltip.SimpleTooltip;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder> {
    private Context con;
    private List<HistoryData> arrayList;
    String keyIntent;
    public HistoryAdapter(Context context, ArrayList<HistoryData> historyData, String keyIntent) {
        super();
        con = context;
        arrayList = historyData;
        this.keyIntent = keyIntent;
    }

    @NonNull
    @Override
    public HistoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.history_list_item, viewGroup, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryAdapter.MyViewHolder holder, final int position) {
        holder.tvDate.setText(arrayList.get(position).getPostDate());
        holder.tvId.setText(arrayList.get(position).getPostCode());
        holder.tvPost.setText(arrayList.get(position).getHeading());
        holder.tvType.setText(arrayList.get(position).getType());
        if (Integer.parseInt(arrayList.get(position).getCount()) > 1) {
            holder.tvOfferCount.setBackgroundResource(R.drawable.light_pink_curve_box);
            holder.tvOfferCount.setText("+"+arrayList.get(position).getCount());
        } else {
            holder.tvOfferCount.setText("");
        }

        holder.btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(con, ViewHistoryActivity.class);
                intent.putExtra("key", keyIntent);
                intent.putExtra("postIndexId", arrayList.get(position).getPostIndexId());
                intent.putExtra("shopIndexId", arrayList.get(position).getShopIndexId());
                intent.putExtra("type", arrayList.get(position).getShopType());
                intent.putExtra("shopLatitude", arrayList.get(position).getShopLatitude());
                intent.putExtra("shopLongitude", arrayList.get(position).getShopLongitude());
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                con.startActivity(intent);
                ((Activity)con).finish();
            }
        });

        holder.tvPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SimpleTooltip.Builder(con)
                        .anchorView(view)
                        .text(arrayList.get(position).getHeading())
                        .gravity(Gravity.TOP)
                        .animated(true)
                        .textColor(ContextCompat.getColor(con, R.color.black))
                        .transparentOverlay(true)
                        .build()
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvId, tvType, tvPost, tvOfferCount;
        Button btnView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvId = itemView.findViewById(R.id.tv_id);
            tvType = itemView.findViewById(R.id.tv_type);
            tvPost = itemView.findViewById(R.id.tv_post);
            tvOfferCount = itemView.findViewById(R.id.tv_offer_count);
            btnView = itemView.findViewById(R.id.btn_view);
        }
    }
}
