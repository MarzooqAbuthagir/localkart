package com.localkartmarketing.localkart.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.localkartmarketing.localkart.R;
import com.localkartmarketing.localkart.model.EventTicket;

import java.util.ArrayList;
import java.util.List;

public class EventPayTicketAdapter extends RecyclerView.Adapter<EventPayTicketAdapter.MyViewHolder> {
    private Context con;
    private List<EventTicket> arrayList;

    public EventPayTicketAdapter(Context context, ArrayList<EventTicket> tickets) {
        super();
        con = context;
        arrayList = tickets;
    }

    @NonNull
    @Override
    public EventPayTicketAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.ticket_view, viewGroup, false);
        return new EventPayTicketAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull EventPayTicketAdapter.MyViewHolder holder, int position) {
        holder.tvCategory.setText(arrayList.get(position).getName());
        holder.tvPrice.setText(con.getResources().getString(R.string.Rs) + " " + arrayList.get(position).getPrice());
        holder.tvQty.setText(""+arrayList.get(position).getQty());
        int total = arrayList.get(position).getQty() * Integer.parseInt(arrayList.get(position).getPrice());
        holder.tvTotal.setText("" + total);
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
