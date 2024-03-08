package com.localkartmarketing.localkart.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.localkartmarketing.localkart.R;
import com.localkartmarketing.localkart.activity.BookingDetailsActivity;
import com.localkartmarketing.localkart.model.MyBooking;

import java.util.ArrayList;
import java.util.List;

public class MyBookingAdapter extends RecyclerView.Adapter<MyBookingAdapter.MyViewHolder> {
    private Context con;
    private List<MyBooking> arrayList;

    public MyBookingAdapter(Context context, ArrayList<MyBooking> bookingData) {
        super();
        con = context;
        arrayList = bookingData;
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.my_booking_list_item, viewGroup, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

        holder.tvName.setText(Html.fromHtml(arrayList.get(position).getEventName()));
        holder.tvDate.setText(arrayList.get(position).getEventDate());
        holder.tvTime.setText(arrayList.get(position).getEventTime());
        holder.tvLocation.setText(arrayList.get(position).getCity());
        holder.tvTickets.setText(arrayList.get(position).getTotalQty() + " Tickets");
        Glide.with(con).load(arrayList.get(position).getImage())
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.imageView);

        holder.btnDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(con, BookingDetailsActivity.class);
                intent.putExtra("key", "Events");
                intent.putExtra("index", "1");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                con.startActivity(intent);
                ((Activity) con).finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView tvName, tvDate, tvTime, tvLocation, tvTickets;
        Button btnDetails;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvLocation = itemView.findViewById(R.id.tv_location);
            tvTickets = itemView.findViewById(R.id.tv_tickets);
            imageView = itemView.findViewById(R.id.image_view);
            btnDetails = itemView.findViewById(R.id.btn_details);
        }
    }
}
