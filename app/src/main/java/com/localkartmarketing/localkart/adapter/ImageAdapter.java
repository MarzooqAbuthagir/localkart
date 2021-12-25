package com.localkartmarketing.localkart.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.localkartmarketing.localkart.R;
import com.localkartmarketing.localkart.model.UploadImages;

import java.util.List;

public class ImageAdapter extends ArrayAdapter<UploadImages> {
    private Context con;
    private AddServiceAdapter.OnItemClickListener mItemClickListener;
    int state;

    public ImageAdapter(Context context, List<UploadImages> imagesArrayList, int visible) {
        super(context, 0, imagesArrayList);
        con = context;
        state = visible;
    }

    public void setOnItemClickListener(final AddServiceAdapter.OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listitemView = convertView;
        if (listitemView == null) {
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.image_list_item, parent, false);
        }
        final UploadImages uploadImages = getItem(position);
        ImageView ivImage = listitemView.findViewById(R.id.iv_gallery);
        ImageView ivDelete = listitemView.findViewById(R.id.iv_remove_image);

        if (state == 0) {
            ivDelete.setVisibility(View.VISIBLE);
            ivImage.setImageBitmap(uploadImages.getBitmap());
        } else if (state == 1) {
            ivDelete.setVisibility(View.VISIBLE);
            if (uploadImages.getImageIndexId().isEmpty()) {
                ivImage.setImageBitmap(uploadImages.getBitmap());
            } else {
                Glide.with(con).load(uploadImages.getImage())
                        .diskCacheStrategy(DiskCacheStrategy.ALL).into(ivImage);
            }
        }

        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(view, position);
                }
            }
        });

        return listitemView;
    }
}
