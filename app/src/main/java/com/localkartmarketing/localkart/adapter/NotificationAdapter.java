package com.localkartmarketing.localkart.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.localkartmarketing.localkart.R;
import com.localkartmarketing.localkart.activity.NotificationPostActivity;
import com.localkartmarketing.localkart.model.NotificationData;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    private Context con;
    private List<NotificationData> arrayList;
    String key;

    public NotificationAdapter(Context context, List<NotificationData> dataList, String keyIntent) {
        this.con = context;
        this.arrayList = dataList;
        this.key = keyIntent;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.notification_list_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.textHeading.setText(arrayList.get(position).getPostHeading());
        holder.textPostType.setText(arrayList.get(position).getPostType());
        holder.textDate.setText(arrayList.get(position).getFromDate() + " To " + arrayList.get(position).getToDate());

        holder.layMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(con, NotificationPostActivity.class);
                intent.putExtra("key", key);
                intent.putExtra("shopIndexId", arrayList.get(position).getShopIndexId());
                intent.putExtra("postIndexId", arrayList.get(position).getPostIndexId());
                intent.putExtra("shopType", arrayList.get(position).getShopType());
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
        TextView textHeading, textPostType, textDate;
        LinearLayout layMain;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            layMain = itemView.findViewById(R.id.lay_main);
            textPostType = itemView.findViewById(R.id.tv_post_type);
            textDate = itemView.findViewById(R.id.tv_date);
            textHeading = itemView.findViewById(R.id.tv_heading);
        }
    }
}
