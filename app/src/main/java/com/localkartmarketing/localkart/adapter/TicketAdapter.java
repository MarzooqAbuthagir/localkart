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

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.MyViewHolder> {
    private Context con;
    private List<Ticket> arrayList;

    public TicketAdapter(Context context, ArrayList<Ticket> tickets) {
        super();
        con = context;
        arrayList = tickets;
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.ticket_view, viewGroup, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        System.out.println("Ticker Adapter "+ arrayList.get(position).getName());
        holder.tvCategory.setText(arrayList.get(position).getName());
        holder.tvPrice.setText(con.getResources().getString(R.string.Rs)+ " "+ arrayList.get(position).getAmount());
        holder.tvQty.setText(arrayList.get(position).getQty());
        holder.tvTotal.setText(arrayList.get(position).getTotal());

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
