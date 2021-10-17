package com.kart.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kart.R;
import com.kart.model.PaymentHistoryData;

import java.util.ArrayList;
import java.util.List;

public class PaymentHistoryAdapter extends RecyclerView.Adapter<PaymentHistoryAdapter.MyViewHolder> {
    private Context con;
    private List<PaymentHistoryData> arrayList;
    int totalCount;
    private OnItemClickListener mItemClickListener;

    public PaymentHistoryAdapter(Context context, ArrayList<PaymentHistoryData> historyData) {
        super();
        con = context;
        arrayList = historyData;
        totalCount = arrayList.size();
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.pay_history_list_item, viewGroup, false);
        return new PaymentHistoryAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        holder.tvSn.setText(String.valueOf(totalCount));
        totalCount--;
        holder.tvDate.setText(arrayList.get(position).getPaymentDate());
        holder.tvPackage.setText(arrayList.get(position).getPackageName());
        holder.tvAmount.setText(arrayList.get(position).getAmount());
        holder.tvStatus.setText(arrayList.get(position).getStatus());
        holder.tvValidity.setText(arrayList.get(position).getValidity()+" Days");

        holder.btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(view, holder.getAdapterPosition());
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvSn, tvValidity, tvPackage, tvAmount, tvStatus;
        Button btnView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSn = itemView.findViewById(R.id.tv_sn);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvPackage = itemView.findViewById(R.id.tv_package);
            tvValidity = itemView.findViewById(R.id.tv_validity);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            tvStatus = itemView.findViewById(R.id.tv_status);
            btnView = itemView.findViewById(R.id.btn_view);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}
