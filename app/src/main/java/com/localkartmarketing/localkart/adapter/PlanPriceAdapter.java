package com.localkartmarketing.localkart.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.localkartmarketing.localkart.R;
import com.localkartmarketing.localkart.model.PlanPrices;

import java.util.List;

public class PlanPriceAdapter extends RecyclerView.Adapter<PlanPriceAdapter.MyView> {
    Context context;
    List<PlanPrices> arrayList;
    private OnItemClickListener mItemClickListener;

    public PlanPriceAdapter(Context con, List<PlanPrices> planPrices) {
        this.context = con;
        this.arrayList = planPrices;
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    @NonNull
    @Override
    public PlanPriceAdapter.MyView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView
                = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.plan_price_list_item,
                        parent,
                        false);

        return new MyView(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final PlanPriceAdapter.MyView holder, final int position) {
        holder.tvPlanName.setText(arrayList.get(position).getPlanName());
        holder.tvPlanPrice.setText("Rs." + arrayList.get(position).getPlanPrice());
        holder.tvPlanValidity.setText("/" + arrayList.get(position).getPlanValidity());

        if (arrayList.get(position).isCurrentPlan()) {
            holder.tvPlanState.setText("Current Plan");
            holder.ivPlan.setImageDrawable(context.getDrawable(R.drawable.current_plan));
            holder.rootLayout.setBackground(context.getDrawable(R.drawable.upgrade_plan_curve));
        } else {
            holder.tvPlanState.setText("Upgrade To");
            holder.ivPlan.setImageDrawable(context.getDrawable(R.drawable.upgrade_plan));
            holder.rootLayout.setBackground(context.getDrawable(R.drawable.current_plan_curve));
        }

        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
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

    public static class MyView extends RecyclerView.ViewHolder {
        ImageView ivPlan;
        TextView tvPlanName, tvPlanPrice, tvPlanState, tvPlanValidity;
        LinearLayout rootLayout;

        public MyView(@NonNull View itemView) {
            super(itemView);
            ivPlan = itemView.findViewById(R.id.iv_plan);
            tvPlanName = itemView.findViewById(R.id.tv_plan_name);
            tvPlanPrice = itemView.findViewById(R.id.tv_plan_price);
            tvPlanState = itemView.findViewById(R.id.tv_plan_status);
            tvPlanValidity = itemView.findViewById(R.id.tv_plan_validity);
            rootLayout = itemView.findViewById(R.id.root_layout);
        }
    }


    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

}
