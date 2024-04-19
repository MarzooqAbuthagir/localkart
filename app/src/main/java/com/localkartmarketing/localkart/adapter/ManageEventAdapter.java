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
import com.localkartmarketing.localkart.activity.EventScanActivity;
import com.localkartmarketing.localkart.activity.ManageEventDetailActivity;
import com.localkartmarketing.localkart.model.EventListData;

import java.util.ArrayList;
import java.util.List;

public class ManageEventAdapter extends RecyclerView.Adapter<ManageEventAdapter.ViewHolder> {
    private Context con;
    private List<EventListData> arrayList;

    public ManageEventAdapter(Context context, ArrayList<EventListData> eventListData) {
        super();
        con = context;
        arrayList = eventListData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.manage_event_list_item, viewGroup, false);
        return new ManageEventAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.tvName.setText(Html.fromHtml(arrayList.get(position).getEventName()));
        holder.tvDate.setText(arrayList.get(position).getEventDate());
        holder.tvTime.setText(arrayList.get(position).getDistrict());
        Glide.with(con).load(arrayList.get(position).getImage())
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.imageView);

        holder.btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(con, EventScanActivity.class);
                intent1.putExtra("key", "Events");
                intent1.putExtra("eventName", arrayList.get(position).getEventName());
                intent1.putExtra("eventId", arrayList.get(position).getEventId());
                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                con.startActivity(intent1);
                ((Activity) con).finish();
            }
        });

        holder.btnDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(con, ManageEventDetailActivity.class);
                intent1.putExtra("key", "Events");
                intent1.putExtra("eventId", arrayList.get(position).getEventId());
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView tvName, tvDate, tvTime;
        Button btnScan, btnDetails;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvTime = itemView.findViewById(R.id.tv_time);
            imageView = itemView.findViewById(R.id.imageView);
            btnScan = itemView.findViewById(R.id.btn_scan);
            btnDetails = itemView.findViewById(R.id.btn_details);
        }
    }
}
