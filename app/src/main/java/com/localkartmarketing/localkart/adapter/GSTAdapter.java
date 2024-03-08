package com.localkartmarketing.localkart.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.localkartmarketing.localkart.R;
import com.localkartmarketing.localkart.model.GST;

import java.util.ArrayList;
import java.util.List;

public class GSTAdapter extends RecyclerView.Adapter<GSTAdapter.ViewHolder> {
    private Context con;
    private List<GST> arrayList;

    public GSTAdapter(Context context, ArrayList<GST> gstArrayList) {
        super();
        con = context;
        arrayList = gstArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.gst_list_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.tvLabel.setText(arrayList.get(i).getLabel()+" "+ arrayList.get(i).getPercent()+"% :");
        viewHolder.tvAmount.setText(arrayList.get(i).getAmount());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvLabel, tvAmount;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLabel = itemView.findViewById(R.id.tv_label);
            tvAmount = itemView.findViewById(R.id.tv_amount);
        }
    }
}
