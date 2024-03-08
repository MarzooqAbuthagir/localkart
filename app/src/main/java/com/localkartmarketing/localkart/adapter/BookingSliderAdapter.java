package com.localkartmarketing.localkart.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.localkartmarketing.localkart.R;
import com.localkartmarketing.localkart.activity.BookingDetailsActivity;
import com.localkartmarketing.localkart.model.MyBooking;

import java.util.ArrayList;
import java.util.List;

public class BookingSliderAdapter extends PagerAdapter {

    private Context con;
    private List<MyBooking> arrayList;

    public BookingSliderAdapter(Context context, ArrayList<MyBooking> bookingData) {
        super();
        con = context;
        arrayList = bookingData;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        LayoutInflater layoutInflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.hr_booking_list_item, null);

        TextView tvName = view.findViewById(R.id.tv_name);
        TextView tvDate = view.findViewById(R.id.tv_date);
        TextView tvLocation = view.findViewById(R.id.tv_address);
        TextView tvTickets = view.findViewById(R.id.tv_tickets);
        ImageView imageView = view.findViewById(R.id.image_view);
        TextView btnDetails = view.findViewById(R.id.btn_details);

        tvName.setText(Html.fromHtml(arrayList.get(position).getEventName()));
        tvDate.setText(arrayList.get(position).getEventDate() + ", " + arrayList.get(position).getEventTime());

        tvLocation.setText(arrayList.get(position).getCity());
        tvTickets.setText(arrayList.get(position).getTotalQty() + " Tickets");
        Glide.with(con).load(arrayList.get(position).getImage())
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);

        btnDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(con, BookingDetailsActivity.class);
                intent.putExtra("key", "Events");
                intent.putExtra("index", "0");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                con.startActivity(intent);
                ((Activity) con).finish();
            }
        });

        ViewPager vp = (ViewPager) container;
        vp.addView(view, 0);
        return view;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        ViewPager vp = (ViewPager) container;
        View view = (View) object;
        vp.removeView(view);

    }
}
//public class BookingSliderAdapter extends RecyclerView.Adapter<BookingSliderAdapter.SliderViewHolder> {
//    private Context con;
//    private List<MyBooking> arrayList;
//
//    public BookingSliderAdapter(Context context, ArrayList<MyBooking> bookingData) {
//        super();
//        con = context;
//        arrayList = bookingData;
//    }
//
//    @NonNull
//    @Override
//    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
//        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.hr_booking_list_item, viewGroup, false);
//        return new SliderViewHolder(v);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
//        holder.tvName.setText(arrayList.get(position).getEventName());
//        holder.tvDate.setText(arrayList.get(position).getEventDate()+", "+arrayList.get(position).getStartTime());
//
//        holder.tvLocation.setText(arrayList.get(position).getCity());
//        holder.tvTickets.setText(arrayList.get(position).getTotalQty() + " Tickets");
//        Glide.with(con).load(arrayList.get(position).getImage())
//                .diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.imageView);
//
//        holder.btnDetails.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent1 = new Intent(con, BookingDetailsActivity.class);
//                intent1.putExtra("key", "Events");
//                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                con.startActivity(intent1);
//                ((Activity) con).finish();
//            }
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return arrayList.size();
//    }
//
//    public class SliderViewHolder extends RecyclerView.ViewHolder {
//        ImageView imageView;
//        TextView tvName, tvDate, tvLocation, tvTickets;
//        Button btnDetails;
//
//        public SliderViewHolder(@NonNull View itemView) {
//            super(itemView);
//            tvName = itemView.findViewById(R.id.tv_name);
//            tvDate = itemView.findViewById(R.id.tv_date);
//            tvLocation = itemView.findViewById(R.id.tv_address);
//            tvTickets = itemView.findViewById(R.id.tv_tickets);
//            imageView = itemView.findViewById(R.id.image_view);
//            btnDetails = itemView.findViewById(R.id.btn_details);
//        }
//    }
//}
