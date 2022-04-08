package com.localkartmarketing.localkart.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.localkartmarketing.localkart.R;
import com.localkartmarketing.localkart.activity.MegaSalesCreatePostActivity;
import com.localkartmarketing.localkart.model.ManageBusinessMenu;

import java.util.List;

public class ManageBusinessMenuAdapter extends RecyclerView.Adapter<ManageBusinessMenuAdapter.ViewHolder> {
    private List<ManageBusinessMenu> arrayList;
    private Context con;
    private String keyIntent;

    public ManageBusinessMenuAdapter(Context context, List<ManageBusinessMenu> manageBusinessMenuList, String keyIntent) {
        this.arrayList = manageBusinessMenuList;
        this.con = context;
        this.keyIntent = keyIntent;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.manage_business_list_item, viewGroup, false);
        return new ManageBusinessMenuAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.tvTitle.setText(arrayList.get(position).getOfferTitle());
        holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(con).load(arrayList.get(position).getIcon())
                .placeholder(R.mipmap.ic_launcher_round)
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.imageView);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (arrayList.get(position).getIsAlready().equalsIgnoreCase("0")) {
                    if (arrayList.get(position).getIsAllow().equalsIgnoreCase("1")) {
                        Intent intent = new Intent(con, MegaSalesCreatePostActivity.class);
                        intent.putExtra("key", keyIntent);
                        intent.putExtra("megaSalesIndexId", arrayList.get(position).getMegaSalesIndexId());
                        intent.putExtra("fromDate", arrayList.get(position).getFromDate());
                        intent.putExtra("toDate", arrayList.get(position).getToDate());
                        intent.putExtra("fDate", arrayList.get(position).getfDate());
                        intent.putExtra("tDate", arrayList.get(position).gettDate());
                        intent.putExtra("megaSalesTitle", arrayList.get(position).getOfferTitle());
                        intent.putExtra("dealCount", arrayList.get(position).getTotalDeals());
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        con.startActivity(intent);
                        ((Activity) con).finish();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(con);
                        builder.setMessage(arrayList.get(position).getErrorMessage())
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();

                        Button btnOk = alert.getButton(DialogInterface.BUTTON_NEUTRAL);
                        btnOk.setTextColor(Color.parseColor("#000000"));
                    }
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(con);
                    builder.setMessage(arrayList.get(position).getAlreadyMessage())
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();

                    Button btnOk = alert.getButton(DialogInterface.BUTTON_NEUTRAL);
                    btnOk.setTextColor(Color.parseColor("#000000"));
                }


            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvTitle;
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_layout);
            tvTitle = itemView.findViewById(R.id.text_view);
            imageView = itemView.findViewById(R.id.image_view);
        }
    }
}
