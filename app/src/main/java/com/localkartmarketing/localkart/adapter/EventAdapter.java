package com.localkartmarketing.localkart.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.localkartmarketing.localkart.R;
import com.localkartmarketing.localkart.activity.EventBookNowActivity;
import com.localkartmarketing.localkart.activity.EventDetailActivity;
import com.localkartmarketing.localkart.model.EventListData;

import java.util.ArrayList;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.MyViewHolder> {
    private Context con;
    private List<EventListData> arrayList;

    public EventAdapter(Context context, ArrayList<EventListData> eventListData) {
        super();
        con = context;
        arrayList = eventListData;
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.event_list_item, viewGroup, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

        holder.tvName.setText(Html.fromHtml(arrayList.get(position).getEventName()));
        holder.tvDate.setText(arrayList.get(position).getEventDate());
        holder.tvTime.setText(arrayList.get(position).getDistrict());
        Glide.with(con).load(arrayList.get(position).getImage())
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.imageView);

        holder.btnBookNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(con, EventBookNowActivity.class);
                intent1.putExtra("key", "Events");
                intent1.putExtra("index", "0");
                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                con.startActivity(intent1);
                ((Activity) con).finish();
            }
        });

        holder.btnDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(con, EventDetailActivity.class);
                intent1.putExtra("key", "Events");
                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                con.startActivity(intent1);
                ((Activity) con).finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView tvName, tvDate, tvTime;
        Button btnBookNow, btnDetails;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvTime = itemView.findViewById(R.id.tv_time);
            imageView = itemView.findViewById(R.id.imageView);
            btnBookNow = itemView.findViewById(R.id.btn_book_now);
            btnDetails = itemView.findViewById(R.id.btn_details);
        }
    }
}
