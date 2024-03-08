package com.localkartmarketing.localkart.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.localkartmarketing.localkart.R;
import com.localkartmarketing.localkart.model.EventTicketSummary;

import java.util.ArrayList;
import java.util.List;

public class EventSummaryAdapter extends RecyclerView.Adapter<EventSummaryAdapter.MyViewHolder> {
    private Context con;
    private List<EventTicketSummary> arrayList;

    public EventSummaryAdapter(Context context, ArrayList<EventTicketSummary> tickets) {
        super();
        con = context;
        arrayList = tickets;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.event_summary_list_item, viewGroup, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        int count = i +1;
        myViewHolder.tvNum.setText(""+ count);
        myViewHolder.tvDesc.setText(arrayList.get(i).getDescription());
        myViewHolder.tvName.setText(arrayList.get(i).getName()+"("+arrayList.get(i).getTicketCount()+")");
        myViewHolder.tvAmount.setText(arrayList.get(i).getAmount());
        myViewHolder.tvAvailable.setText(arrayList.get(i).getAvailable());
        myViewHolder.tvBookings.setText(arrayList.get(i).getBooked());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvNum, tvDesc, tvName, tvAmount, tvAvailable, tvBookings;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNum = itemView.findViewById(R.id.tv_num);
            tvDesc = itemView.findViewById(R.id.tv_desc);
            tvName = itemView.findViewById(R.id.tv_ticket_name);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            tvAvailable = itemView.findViewById(R.id.tv_available);
            tvBookings = itemView.findViewById(R.id.tv_bookings);
        }
    }
}
