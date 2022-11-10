package com.localkartmarketing.localkart.adapter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.localkartmarketing.localkart.R;
import com.localkartmarketing.localkart.model.NewsData;

import java.util.ArrayList;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {
    private Context con;
    private List<NewsData> arrayList;

    public NewsAdapter(Context context, ArrayList<NewsData> newsListValue) {
        con = context;
        arrayList = newsListValue;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.news_item, viewGroup, false);
        return new NewsAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        holder.tvNews.setHorizontallyScrolling(true);
        holder.tvNews.setText(arrayList.get(position).getNewsDetail());
        holder.tvNews.setSingleLine(true);
        holder.tvNews.setSelected(true);
        holder.tvNews.setEllipsize(TextUtils.TruncateAt.MARQUEE);

        holder.tvNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (arrayList.get(position).getActionType()) {
                    case "URL":
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(arrayList.get(position).getDataLink()));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setPackage("com.android.chrome");
                        try {
                            con.startActivity(intent);
                        } catch (ActivityNotFoundException ex) {
                            // Chrome browser presumably not installed so allow user to choose instead
                            intent.setPackage(null);
                            con.startActivity(intent);
                        }
                        break;
                    case "Phone":
                        String telPhone = "tel:" + arrayList.get(position).getDataLink();
                        Intent callIntent = new Intent(Intent.ACTION_DIAL);
                        callIntent.setData(Uri.parse(telPhone));
                        con.startActivity(callIntent);
                        break;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvNews;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNews = itemView.findViewById(R.id.tvBottom);
        }
    }
}
