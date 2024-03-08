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

public class EventTicketAdapter extends RecyclerView.Adapter<EventTicketAdapter.MyViewHolder> {
    private Context con;
    private List<EventTicket> arrayList;
    public EventTicketAdapter(Context context, ArrayList<EventTicket> tickets) {
        super();
        con = context;
        arrayList = tickets;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.event_ticket_view, viewGroup, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tvPrice.setText(con.getResources().getString(R.string.Rs)+ " "+ arrayList.get(position).getPrice());
        holder.tvDesc.setText(arrayList.get(position).getDescription());
        holder.tvName.setText(arrayList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvPrice, tvDesc;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvDesc = itemView.findViewById(R.id.tv_desc);

        }
    }
}
