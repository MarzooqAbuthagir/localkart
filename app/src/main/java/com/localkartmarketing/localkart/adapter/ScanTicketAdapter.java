package com.localkartmarketing.localkart.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.localkartmarketing.localkart.R;
import com.localkartmarketing.localkart.model.Ticket;

import java.util.ArrayList;
import java.util.List;

public class ScanTicketAdapter extends RecyclerView.Adapter<ScanTicketAdapter.MyViewHolder> {
    private Context con;
    private List<Ticket> arrayList;
    public ScanTicketAdapter(Context context, ArrayList<Ticket> tickets) {
        super();
        con = context;
        arrayList = tickets;
    }

    @NonNull
    @Override
    public ScanTicketAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.ticket_view, viewGroup, false);
        return new ScanTicketAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ScanTicketAdapter.MyViewHolder holder, int position) {
        holder.tvCategory.setText(arrayList.get(position).getName());
        holder.tvPrice.setText(arrayList.get(position).getDescription());
        holder.tvQty.setText(arrayList.get(position).getQty());
        holder.tvTotal.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategory, tvPrice, tvQty, tvTotal;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tv_ticket_category);
            tvPrice = itemView.findViewById(R.id.tv_ticket_price);
            tvQty = itemView.findViewById(R.id.tv_ticket_qty);
            tvTotal = itemView.findViewById(R.id.tv_ticket_total);
        }
    }
}
