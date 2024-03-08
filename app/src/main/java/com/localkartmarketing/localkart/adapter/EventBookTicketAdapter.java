package com.localkartmarketing.localkart.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.localkartmarketing.localkart.R;
import com.localkartmarketing.localkart.model.EventTicket;

import java.util.ArrayList;
import java.util.List;

public class EventBookTicketAdapter extends RecyclerView.Adapter<EventBookTicketAdapter.MyViewHolder> {
    private Context con;
    private List<EventTicket> arrayList;
    private int maxValue = 10;
    private TextView tvTotalAmount;
    private TextView tvTotalQty;

    public EventBookTicketAdapter(Context context, ArrayList<EventTicket> tickets, TextView tvQty, TextView tvTotal) {
        super();
        con = context;
        arrayList = tickets;
        tvTotalQty = tvQty;
        tvTotalAmount = tvTotal;
    }

    @NonNull
    @Override
    public EventBookTicketAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.event_book_ticket_view, viewGroup, false);
        return new EventBookTicketAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final EventBookTicketAdapter.MyViewHolder holder, final int position) {
        final EventTicket eventTicket = arrayList.get(position);
        if (Integer.parseInt(arrayList.get(position).getPrice()) > 0) {
            holder.tvDesc.setText(con.getResources().getString(R.string.Rs) + " " + arrayList.get(position).getPrice());
        } else {
            holder.tvDesc.setText(con.getResources().getString(R.string.Rs) + " " + arrayList.get(position).getPrice() + " Sold Out");
        }
        holder.tvName.setText(arrayList.get(position).getName());
        int currentValue = arrayList.get(position).getRemaining();

        // Create a list to hold the values for the Spinner
        List<String> spinnerValues = new ArrayList<>();

        // Populate the Spinner based on the condition

        for (int i = 0; i <= currentValue && i <= maxValue; i++) {
            spinnerValues.add(String.valueOf(i));
        }


        // Create an ArrayAdapter to populate the Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(con, android.R.layout.simple_spinner_item, spinnerValues);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set the ArrayAdapter to the Spinner
        holder.spin.setAdapter(adapter);

        holder.spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View selectedItemView, int spinPosition, long id) {
                ((TextView) adapterView.getChildAt(0)).setGravity(Gravity.CENTER);
                String selectedValue = (String) adapterView.getItemAtPosition(spinPosition);
                int lineTotal = Integer.parseInt(arrayList.get(position).getPrice()) * Integer.parseInt(selectedValue);
                holder.tvPrice.setText("" + lineTotal);
                eventTicket.setQty(Integer.parseInt(selectedValue));

                int totalQty = 0, totalAmt = 0;
                for (EventTicket ticket : arrayList) {
                    if (ticket.getQty() > 0) {
                        totalQty = totalQty + ticket.getQty();
                        totalAmt = totalAmt + (ticket.getQty() * Integer.parseInt(ticket.getPrice()));
                    }
                }
                tvTotalQty.setText("" + totalQty);
                tvTotalAmount.setText("" + totalAmt);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvDesc;
        Spinner spin;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvDesc = itemView.findViewById(R.id.tv_desc);
            spin = itemView.findViewById(R.id.spin_ticket_qty);
        }
    }
}
