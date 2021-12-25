package com.localkartmarketing.localkart.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.localkartmarketing.localkart.R;

import java.util.List;

public class AddServiceAdapter extends RecyclerView.Adapter<AddServiceAdapter.ViewHolder> {
    List<String> arrayList;
    Context context;
    private OnItemClickListener mItemClickListener;
    int state;

    public AddServiceAdapter(Context context, List<String> listOfService, int visible) {
        super();
        this.context = context;
        this.arrayList = listOfService;
        state = visible;
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.service_layout, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.tvServiceName.setText(arrayList.get(position));
        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(view, holder.getAdapterPosition());
                }
            }
        });
        if (state == 0) {
            holder.ivDelete.setVisibility(View.VISIBLE);
        } else if (state == 1) {
            holder.ivDelete.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvServiceName;
        public ImageView ivDelete;

        public ViewHolder(View itemView) {
            super(itemView);
            tvServiceName = itemView.findViewById(R.id.tv_service_name);
            ivDelete = itemView.findViewById(R.id.iv_delete);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}
