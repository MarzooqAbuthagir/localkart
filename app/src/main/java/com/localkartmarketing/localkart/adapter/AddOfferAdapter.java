package com.localkartmarketing.localkart.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.localkartmarketing.localkart.R;
import com.localkartmarketing.localkart.model.AddOfferData;

import java.util.List;

public class AddOfferAdapter extends RecyclerView.Adapter<AddOfferAdapter.ViewHolder> {
    private Context con;
    private List<AddOfferData> arrayList;
    private OnItemEditClickListener mItemEditClickListener;
    private OnItemDeleteClickListener mItemDeleteClickListener;
    int pageId;
    String strJobImage;

    public AddOfferAdapter(Context context, List<AddOfferData> listOfOffer, int pageId, String image) {
        super();
        con = context;
        arrayList = listOfOffer;
        this.pageId = pageId;
        this.strJobImage = image;
    }

    public void setOnItemEditClickListener(final OnItemEditClickListener mItemClickListener) {
        this.mItemEditClickListener = mItemClickListener;
    }

    public void setOnItemDeleteClickListener(final OnItemDeleteClickListener mItemClickListener) {
        this.mItemDeleteClickListener = mItemClickListener;
    }

    @NonNull
    @Override
    public AddOfferAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.offer_layout, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final AddOfferAdapter.ViewHolder holder, int position) {
        System.out.println("onadapter " + position + " heading " + arrayList.get(position).getHeading());
        holder.tvHeading.setText(arrayList.get(position).getHeading());
        holder.tvDesc.setText(arrayList.get(position).getDesc());

//        holder.tvOffer.setText("Deal "+arrayList.get(position).getOffCount());
        int pos = position + 1;
        holder.tvOffer.setText(pageId == 2 ? "Job Opening " + pos : "Deal " + pos);
        if (pageId == 2) {
            Glide.with(con).load(arrayList.get(position).getImage())
                    .placeholder(R.mipmap.ic_launcher_round)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.imageView);
        } else {
            holder.imageView.getLayoutParams().height = 500;
            holder.imageView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
            holder.imageView.setImageBitmap(arrayList.get(position).getBitmap());
        }
        holder.ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mItemEditClickListener != null) {
                    mItemEditClickListener.onItemEditClick(view, holder.getAdapterPosition());
                }
            }
        });

        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mItemDeleteClickListener != null) {
                    mItemDeleteClickListener.onItemDeleteClick(view, holder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public EditText tvHeading;
        public EditText tvDesc;
        public TextView tvOffer;
        ImageView imageView;
        ImageView ivDelete;
        ImageView ivEdit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHeading = itemView.findViewById(R.id.et_heading);
            tvDesc = itemView.findViewById(R.id.et_desc);
            tvOffer = itemView.findViewById(R.id.tv_offer);
            imageView = itemView.findViewById(R.id.iv_img);
            ivEdit = itemView.findViewById(R.id.iv_edit);
            ivDelete = itemView.findViewById(R.id.iv_delete);
        }
    }

    public interface OnItemEditClickListener {
        void onItemEditClick(View view, int position);
    }

    public interface OnItemDeleteClickListener {
        void onItemDeleteClick(View view, int position);
    }
}
