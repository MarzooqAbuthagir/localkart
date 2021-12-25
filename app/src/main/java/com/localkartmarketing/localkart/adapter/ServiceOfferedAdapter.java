package com.localkartmarketing.localkart.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.localkartmarketing.localkart.R;

import java.util.List;

public class ServiceOfferedAdapter extends RecyclerView.Adapter<ServiceOfferedAdapter.ViewHolder> {

    private List<String> mData;
    private LayoutInflater mInflater;

    // data is passed into the constructor
    public ServiceOfferedAdapter(Context context, List<String> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.service_offered_list_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvServiceName.setText((position + 1)+". "+mData.get(position));
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvServiceName;

        ViewHolder(View itemView) {
            super(itemView);
            tvServiceName = itemView.findViewById(R.id.tv_service_name);
        }
    }
}
