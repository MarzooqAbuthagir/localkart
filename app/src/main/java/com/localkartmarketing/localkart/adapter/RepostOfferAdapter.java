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

import java.util.List;

public class RepostOfferAdapter extends RecyclerView.Adapter<RepostOfferAdapter.ViewHolder> {
    private Context con;
    private List<OfferData> arrayList;
    private OnItemEditClickListener mItemEditClickListener;
    private OnItemDeleteClickListener mItemDeleteClickListener;

    public RepostOfferAdapter(Context context, List<OfferData> listOfOffer) {
        super();
        con = context;
        arrayList = listOfOffer;
    }

    public void setOnItemEditClickListener(final OnItemEditClickListener mItemClickListener) {
        this.mItemEditClickListener = mItemClickListener;
    }

    public void setOnItemDeleteClickListener(final OnItemDeleteClickListener mItemClickListener) {
        this.mItemDeleteClickListener = mItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.offer_layout, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.tvHeading.setText(arrayList.get(position).getHeading());
        holder.tvDesc.setText(arrayList.get(position).getDesc());

        int count = position+1;
        holder.tvOffer.setText("Deal " + count);
        holder.imageView.getLayoutParams().height = 500;
        holder.imageView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        if (arrayList.get(position).getBitmap() != null) {
            holder.imageView.setImageBitmap(arrayList.get(position).getBitmap());
        } else {
            Glide.with(con).load(arrayList.get(position).getImage())
                    .diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.imageView);
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
