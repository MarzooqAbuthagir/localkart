package com.localkartmarketing.localkart.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.localkartmarketing.localkart.R;
import com.localkartmarketing.localkart.activity.ManageEventBookingDetailActivity;
import com.localkartmarketing.localkart.model.EventBookingList;

import java.util.ArrayList;
import java.util.List;

public class EventBookingListAdapter extends RecyclerView.Adapter<EventBookingListAdapter.ViewHolder> {
    private Context con;
    private List<EventBookingList> arrayList;
    String keyIntent;

    public EventBookingListAdapter(Context context, ArrayList<EventBookingList> tickets, String keyIntent) {
        super();
        con = context;
        arrayList = tickets;
        this.keyIntent = keyIntent;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.event_booking_list_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        int count = i + 1;
        viewHolder.tvNum.setText("" + count);
        viewHolder.tvName.setText(arrayList.get(i).getName());
        viewHolder.tvMobile.setText(arrayList.get(i).getMobile());
        viewHolder.tvDate.setText(arrayList.get(i).getDate());
        viewHolder.tvTime.setText(arrayList.get(i).getTime());
        viewHolder.btnQty.setText(arrayList.get(i).getQty());

        viewHolder.layClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String telPhone = "tel:" + arrayList.get(i).getMobile();
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse(telPhone));
                con.startActivity(callIntent);
            }
        });

        viewHolder.btnQty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(con, ManageEventBookingDetailActivity.class);
                intent.putExtra("key", keyIntent);
                intent.putExtra("eventId", arrayList.get(i).getId());
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                con.startActivity(intent);
                ((Activity) con).finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNum, tvName, tvMobile, tvDate, tvTime;
        Button btnQty;
        LinearLayout layClick;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNum = itemView.findViewById(R.id.tv_num);
            tvName = itemView.findViewById(R.id.tv_name);
            tvMobile = itemView.findViewById(R.id.tv_mobile);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvTime = itemView.findViewById(R.id.tv_time);
            btnQty = itemView.findViewById(R.id.btn_qty);
            layClick = itemView.findViewById(R.id.lay_click);
        }
    }
}
