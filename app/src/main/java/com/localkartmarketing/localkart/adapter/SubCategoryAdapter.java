package com.localkartmarketing.localkart.adapter;

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
import com.localkartmarketing.localkart.R;
import com.localkartmarketing.localkart.activity.DealsActivity;
import com.localkartmarketing.localkart.model.SubCategoryData;
import com.localkartmarketing.localkart.support.Utilis;

import java.util.ArrayList;

public class SubCategoryAdapter extends ArrayAdapter<SubCategoryData> {
    private Context con;
    private String identity, catId, catName;
    Utilis utilis;
    public SubCategoryAdapter(Context context, ArrayList<SubCategoryData> subCategoryDataArrayList, String btnId, String categoryId, String categoryName) {
        super(context, 0, subCategoryDataArrayList);
        con = context;
        identity = btnId;
        catId = categoryId;
        catName = categoryName;
        utilis = new Utilis(con);
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listitemView = convertView;
        if (listitemView == null) {
            // Layout Inflater inflates each item to be displayed in GridView.
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.category_layout, parent, false);
        }
        final SubCategoryData subCategoryData = getItem(position);
        TextView categoryName = listitemView.findViewById(R.id.tv_categoryName);
        ImageView categoryImg = listitemView.findViewById(R.id.iv_categoryImg);
        assert subCategoryData != null;
//        System.out.println("category image "+subCategoryData.getSubCategoryImage());
        categoryName.setText(subCategoryData.getSubCategoryName());
        Glide.with(con).load(subCategoryData.getSubCategoryImage())
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(categoryImg);
        listitemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utilis.callResume = 0;
                Utilis.clearFilterPref(con);
                Intent intent = new Intent(con, DealsActivity.class);
                intent.putExtra("key", identity);
                intent.putExtra("categoryId", catId);
                intent.putExtra("categoryName", catName);
                intent.putExtra("subcategoryName", subCategoryData.getSubCategoryName());
                intent.putExtra("subcategoryId", subCategoryData.getSubCategoryId());
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                con.startActivity(intent);
                ((Activity)con).finish();
            }
        });
        return listitemView;
    }
}
