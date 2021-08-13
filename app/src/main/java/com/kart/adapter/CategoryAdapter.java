package com.kart.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.kart.R;
import com.kart.activity.SubActivity;
import com.kart.model.CategoryData;

import java.util.ArrayList;

public class CategoryAdapter extends ArrayAdapter<CategoryData> {
    private Context con;
    private String identity;
    public CategoryAdapter(@NonNull Context context, ArrayList<CategoryData> categoryDataArrayList, String btnId) {
        super(context, 0, categoryDataArrayList);
        con = context;
        identity = btnId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listitemView = convertView;
        if (listitemView == null) {
            // Layout Inflater inflates each item to be displayed in GridView.
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.category_layout, parent, false);
        }
        final CategoryData categoryData = getItem(position);
        TextView categoryName = listitemView.findViewById(R.id.tv_categoryName);
        ImageView categoryImg = listitemView.findViewById(R.id.iv_categoryImg);
        assert categoryData != null;
        System.out.println("category image "+categoryData.getCategoryImage());
        categoryName.setText(categoryData.getCategoryName());
        Glide.with(con).load(categoryData.getCategoryImage())
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(categoryImg);
        listitemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(con, SubActivity.class);
                intent.putExtra("key", identity);
                intent.putExtra("categoryId", categoryData.getCategoryId());
                intent.putExtra("categoryName", categoryData.getCategoryName());
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                con.startActivity(intent);
                ((Activity)con).finish();
            }
        });
        return listitemView;
    }
}
