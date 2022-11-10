package com.localkartmarketing.localkart.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.localkartmarketing.localkart.R;
import com.localkartmarketing.localkart.activity.ViewHistoryActivity;
import com.localkartmarketing.localkart.model.HistoryData;
import com.localkartmarketing.localkart.support.tooltip.SimpleTooltip;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder> {
    private Context con;
    private List<HistoryData> arrayList;
    String keyIntent;
    private OnItemClickListener onItemClickListener;

    public HistoryAdapter(Context context, ArrayList<HistoryData> historyData, String keyIntent, OnItemClickListener onItemClickListener) {
        super();
        con = context;
        arrayList = historyData;
        this.keyIntent = keyIntent;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public HistoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.history_list_item, viewGroup, false);
        return new MyViewHolder(v, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryAdapter.MyViewHolder holder, final int position) {
        holder.tvDate.setText(arrayList.get(position).getPostDate());
        holder.tvId.setText(arrayList.get(position).getPostCode());
        holder.tvPost.setText(arrayList.get(position).getHeading());
        holder.tvType.setText(arrayList.get(position).getType());
        if (Integer.parseInt(arrayList.get(position).getCount()) > 1) {
            holder.tvOfferCount.setBackgroundResource(R.drawable.light_pink_curve_box);
            holder.tvOfferCount.setText("+" + arrayList.get(position).getCount());
        } else {
            holder.tvOfferCount.setText("");
        }

        holder.tvStatus.setText(arrayList.get(position).getStatus());

        if (arrayList.get(position).getStatus().equalsIgnoreCase("pending")) {
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.tvDelete.setVisibility(View.GONE);
        } else {
            holder.btnDelete.setVisibility(View.GONE);
            holder.tvDelete.setVisibility(View.VISIBLE);
        }

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(con);

                builder
                        .setMessage("Are you sure you want to delete ?")
                        .setPositiveButton(con.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                // Do nothing but close the dialog
                                dialog.dismiss();

                                onItemClickListener.onItemClickListener(arrayList.get(position));
                            }
                        })
                        .setNegativeButton(con.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Do nothing
                                dialog.dismiss();
                            }
                        });

                android.app.AlertDialog alert = builder.create();
                alert.show();

                Button btn_yes = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                Button btn_no = alert.getButton(DialogInterface.BUTTON_NEGATIVE);

                btn_no.setTextColor(Color.parseColor("#000000"));
                btn_yes.setTextColor(Color.parseColor("#000000"));

            }
        });

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
                ((Activity) con).finish();
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
        TextView tvDate, tvId, tvType, tvPost, tvOfferCount, tvStatus, tvDelete;
        Button btnView, btnDelete;
        OnItemClickListener onItemClickListener;

        public MyViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvId = itemView.findViewById(R.id.tv_id);
            tvType = itemView.findViewById(R.id.tv_type);
            tvPost = itemView.findViewById(R.id.tv_post);
            tvOfferCount = itemView.findViewById(R.id.tv_offer_count);
            btnView = itemView.findViewById(R.id.btn_view);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvDelete = itemView.findViewById(R.id.tv_delete);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            this.onItemClickListener = onItemClickListener;
        }
    }

    public interface OnItemClickListener {
        void onItemClickListener(HistoryData historyData);
    }
}
